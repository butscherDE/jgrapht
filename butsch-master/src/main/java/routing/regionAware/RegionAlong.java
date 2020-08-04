package routing.regionAware;

import data.*;
import evalutation.StopWatchVerbose;
import geometry.*;
import index.GridIndex;
import index.vc.ReflectiveEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegionAlong extends AbstractRegion {
    public RegionAlong(final RoadGraph globalGraph, final RoadCH globalCH, final GridIndex globalIndex,
                       final RegionOfInterest region) {
        super(globalGraph, globalCH, globalIndex, region);
    }

    @Override
    RoadCH getRegionCH() {
        final Set<VisibilityCell> intersectedCells = getIntersectingCells();
        final RoadGraph regionGraph = prepareGraph(intersectedCells);
        final RoadCH regionCH = prepCh(regionGraph);

        return regionCH;
    }

    private Set<VisibilityCell> getIntersectingCells() {
        final BoundingBox limiter = BoundingBox.createFrom(region.getPolygon());
        final VCLogger vcLogger = new VCLogger(region);
        index.queryVisibilityCells(limiter, vcLogger);
        final Set<VisibilityCell> intersectedCells = vcLogger.intersectedCells;

        if (intersectedCells.size() > 0) {
            System.out.println("found intersecting cells");
            return intersectedCells;
        } else {
            System.out.println("found overlapping cells");
            return getContainingCell(vcLogger);
        }
    }

    private Set<VisibilityCell> getContainingCell(final VCLogger vcLogger) {
        final GeometryFactory gf = new GeometryFactory();
        final BoundingBox regionBB = BoundingBox.createFrom(region.getPolygon());
        for (VisibilityCell vc : vcLogger.nonIntersectingCells) {
            final BoundingBox vcBB = BoundingBox.createFrom(vc);
            if (vcBB.contains(regionBB)) {
                final PolygonContainsChecker polygonContainsChecker = new PolygonContainsChecker(vc.getPolygon());
                final Coordinate[] regionCoordinates = region.getPolygon()
                        .getCoordinates();
                final int numContainedPointsOfRegion = Arrays.stream(regionCoordinates)
                        .map(c -> gf.createPoint(c))
                        .mapToInt(p -> polygonContainsChecker.contains(p) ? 1 : 0)
                        .sum();
                if (numContainedPointsOfRegion == regionCoordinates.length) {
                    return Collections.singleton(vc);
                }
            }
        }

        return Collections.emptySet();
    }

    private RoadGraph prepareGraph(Set<VisibilityCell> intersectedCells) {
        final List<ReflectiveEdge> edges = getEdgesFromCells(intersectedCells);
        final Set<Node> nodes = getNodesFromCells(edges);

        return createGraph(edges, nodes);
    }

    private List<ReflectiveEdge> getEdgesFromCells(Set<VisibilityCell> intersectedCells) {
        return intersectedCells.stream()
                .map(a -> a.edges)
                .flatMap(a -> a.stream())
                .collect(Collectors.toList());
    }

    private Set<Node> getNodesFromCells(List<ReflectiveEdge> edges) {
        return edges.stream()
                .flatMap(a -> Stream.of(a.source, a.target))
                .collect(Collectors.toSet());
    }

    private RoadGraph createGraph(List<ReflectiveEdge> edges, Set<Node> nodes) {
        final RoadGraph regionGraph = new RoadGraph(Edge.class);

        addInternalNodesEdges(edges, nodes, regionGraph);
        addEntryExitNodesEdges(nodes, regionGraph);

        return regionGraph;
    }

    private void addInternalNodesEdges(List<ReflectiveEdge> edges, Set<Node> nodes, RoadGraph regionGraph) {
        nodes.forEach(node -> regionGraph.addVertex(node));
        edges.forEach(edge -> {
            if (!regionGraph.containsEdge(edge.source, edge.target)) {
                final Edge regionEdge = regionGraph.addEdge(edge.source, edge.target);
                final double edgeWeight = globalGraph.getEdgeWeight(globalGraph.getEdge(edge.source, edge.target));
                regionGraph.setEdgeWeight(regionEdge, edgeWeight);
            }
        });
    }

    private void addEntryExitNodesEdges(Set<Node> nodes, RoadGraph regionGraph) {
        final Map<Long, Node> internalNodesMap = nodes.stream()
                .collect(Collectors.toMap(a -> a.id, Function.identity()));
        entryExitNodes.forEach(eeNode -> {
            regionGraph.addVertex(eeNode);
            globalGraph.outgoingEdgesOf(eeNode)
                    .stream()
                    .filter(e -> internalNodesMap.get(globalGraph.getEdgeTarget(e)) != null)
                    .forEach(e -> addEdge(e, regionGraph));
            globalGraph.incomingEdgesOf(eeNode)
                    .stream()
                    .filter(e -> internalNodesMap.get(globalGraph.getEdgeSource(e)) != null)
                    .forEach(e -> addEdge(e, regionGraph));
        });
    }

    private void addEdge(final Edge edge, final RoadGraph regionGraph) {
        final Node edgeSource = globalGraph.getEdgeSource(edge);
        final Node edgeTarget = globalGraph.getEdgeTarget(edge);
        final double weight = globalGraph.getEdgeWeight(edge);

        final Edge regionEdge = regionGraph.addEdge(edgeSource, edgeTarget);
        regionGraph.setEdgeWeight(regionEdge, weight);
    }

    private RoadCH prepCh(RoadGraph regionGraph) {
        final ContractionHierarchyPrecomputation<Node, Edge> chPrep = new ContractionHierarchyPrecomputation<>(regionGraph);
        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = chPrep.computeContractionHierarchy();
        return new RoadCH(ch);
    }

    private class VCLogger implements GridIndex.GridIndexVisitor<VisibilityCell> {
        final Set<VisibilityCell> intersectedCells = new LinkedHashSet<>();
        final Set<VisibilityCell> nonIntersectingCells = new LinkedHashSet<>();
        private final List<LineSegment> redSegments;
//        private final RedBlueSegmentIntersectionCrossProductFactory factory = new RedBlueSegmentIntersectionCrossProductFactory();
        private final RedBlueSegmentIntersectionFactory factory = new SweepShapeIntersectionFactory();

        public VCLogger(final RegionOfInterest roi) {
            this.redSegments = roi.getSortedLineSegments();
        }

        @Override
        public void accept(VisibilityCell entity, BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(VisibilityCell entity) {
            if (!intersectedCells.contains(entity) && !nonIntersectingCells.contains(entity)) {
                final List<LineSegment> blueSegments = entity.lineSegments;
                final SegmentIntersectionAlgorithm intersectionAlgo = factory.createInstance(redSegments, blueSegments);

                if (intersectionAlgo.isIntersectionPresent()) {
                    intersectedCells.add(entity);
                } else {
                    nonIntersectingCells.add(entity);
                }
            }
        }
    }
}
