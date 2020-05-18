package routing.regionAware;

import data.Node;
import data.RegionOfInterest;
import data.VisibilityCell;
import geometry.BoundingBox;
import index.GridIndex;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.w3c.dom.ls.LSOutput;
import routing.regionAware.util.VCROIIntersector;
import util.PolygonRoutingTestGraph;

import javax.swing.plaf.synth.Region;
import java.util.*;
import java.util.stream.Collectors;

public class VCROIIntersectorTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    @Test
    public void testGraphTest() {
        final Coordinate[] roiCoordinates = new Coordinate[] {
                new Coordinate(17,16),
                new Coordinate(21,16),
                new Coordinate(21,12),
                new Coordinate(17,12),
                new Coordinate(17,16)
        };
        final RegionOfInterest roi = new RegionOfInterest(new GeometryFactory().createPolygon(roiCoordinates));

        final BoundingBox roiBB = BoundingBox.createFrom(roi.getPolygon());
        final TestVisitorAllCells visitor = new TestVisitorAllCells();
        GRAPH_MOCKER.gridIndex.queryVisibilityCells(roiBB, visitor);
        final List<VisibilityCell> vcs = visitor.getVisibilityCells();

        final VCROIIntersector vcroiIntersector = new VCROIIntersector();
        final List<VisibilityCell> intersectingCells = vcroiIntersector.getIntersectingCells(vcs, roi);
        final List<List<Long>> allNodes = getNodes(vcs);
        Collections.sort(allNodes, Comparator.comparingLong(a -> a.get(0)));
        allNodes.stream().forEach(a -> System.out.println(a));
        System.out.println("########################");

        intersectingCells.stream().forEach(a -> System.out.println(a));
        final List<List<Long>> collect = getNodes(intersectingCells);


        collect.sort(Comparator.comparingLong(a -> a.get(0)));
        System.out.println("#######################################");
        collect.stream().forEach(a -> System.out.println(a));
    }

    public List<List<Long>> getNodes(final List<VisibilityCell> intersectingCells) {
        return intersectingCells.stream().map(a -> {
                final Coordinate[] coordinates = a.getPolygon().getCoordinates();
                final List<Long> vcNodes = Arrays
                        .stream(coordinates)
                        .map(b -> GRAPH_MOCKER.gridIndex.getClosestNode(b.getX(), b.getY()).id)
                        .collect(Collectors.toList());
    //            Collections.sort(vcNodes);
                System.out.println(vcNodes);
                return vcNodes;
            }).collect(Collectors.toList());
    }

    private class TestVisitorAllCells implements GridIndex.GridIndexVisitor {
        final LinkedHashSet<VisibilityCell> vcs = new LinkedHashSet<>();

        @Override
        public void accept(final Object entity, final BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(final Object entity) {
            vcs.add((VisibilityCell) entity);
        }

        public List<VisibilityCell> getVisibilityCells() {
            return new LinkedList<>(vcs);
        }
    }
}
