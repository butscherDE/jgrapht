package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.RegionOfInterest;
import data.RoadGraph;
import geometry.BoundingBox;
import geometry.PolygonContainsChecker;
import index.GridIndex;
import index.Index;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntryExitPointExtractor {
    private final RegionOfInterest region;
    private final RoadGraph graph;
    private final Index gridIndex;

    public EntryExitPointExtractor(final RegionOfInterest region, final GridIndex gridIndex) {
        this.region = region;
        this.graph = gridIndex.graph;
        this.gridIndex = gridIndex;
    }

    public Set<Node> extract() {
        final EntryExitNodeVisitor entryExitNodeVisitor = new EntryExitNodeVisitor(graph, region.getPolygon());
        final BoundingBox boundingBox = BoundingBox.createFrom(region.getPolygon());
        gridIndex.queryEdges(boundingBox, entryExitNodeVisitor);

        return entryExitNodeVisitor.getEntryExitNodes();
    }

    private static class EntryExitNodeVisitor implements Index.IndexVisitor<Edge> {
        private final RoadGraph graph;
        private final PolygonContainsChecker containsChecker;
        private final LinkedHashSet<Node> entryExitNodes = new LinkedHashSet<>();

        private EntryExitNodeVisitor(final RoadGraph graph, final Polygon region) {
            this.graph = graph;
            this.containsChecker = new PolygonContainsChecker(region);
        }

        @Override
        public void accept(final Edge entity) {
            final Node source = graph.getEdgeSource(entity);
            final Node target = graph.getEdgeTarget(entity);

            addIfOneIsEntryExitNode(source, target);
        }

        public void addIfOneIsEntryExitNode(final Node source, final Node target) {
            final boolean isSourceContained = containsChecker.contains(source.getPoint());
            final boolean isTargetContained = containsChecker.contains(target.getPoint());

            if (isSourceEntryExitPoint(isSourceContained, isTargetContained)) {
                entryExitNodes.add(source);
            } else if (isTargetEntryExitPoint(isSourceContained, isTargetContained)) {
                entryExitNodes.add(target);
            }
        }

        public boolean isTargetEntryExitPoint(final boolean isSourceContained, final boolean isTargetContained) {
            return isSourceContained && !isTargetContained;
        }

        public boolean isSourceEntryExitPoint(final boolean isSourceContained, final boolean isTargetContained) {
            return !isSourceContained && isTargetContained;
        }

        public Set<Node> getEntryExitNodes() {
            return entryExitNodes;
        }
    }
}
