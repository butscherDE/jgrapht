package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.RoadGraph;
import geometry.BoundingBox;
import index.GridIndex;
import index.Index;
import org.locationtech.jts.geom.Polygon;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EntryExitPointExtractor {
    private final Polygon region;
    private final RoadGraph graph;
    private final Index gridIndex;

    public EntryExitPointExtractor(final Polygon region, final RoadGraph graph, final Index gridIndex) {
        this.region = region;
        this.graph = graph;
        this.gridIndex = gridIndex;
    }

    public Set<Node> extract() {
        final EntryExitNodeVisitor entryExitNodeVisitor = new EntryExitNodeVisitor(graph, region);
        final BoundingBox boundingBox = BoundingBox.createFrom(region);
        //gridIndex.queryNodes(boundingBox, entryExitNodeVisitor);
        throw new UnsupportedOperationException("fix the query on index first");

//        return entryExitNodeVisitor.getEntryExitNodes();
    }

    private class EntryExitNodeVisitor implements Consumer<Node> {
        private final RoadGraph graph;
        private final Polygon region;
        private final LinkedHashSet<Node> entryExitNodes = new LinkedHashSet<>();

        private EntryExitNodeVisitor(final RoadGraph graph, final Polygon region) {
            this.graph = graph;
            this.region = region;
        }

        @Override
        public void accept(final Node node) {
            if (isNodeInRegion(node)) {
                addNodesOnIncomingIncidence(node);
                addNodesOnOutgoingIncidence(node);
            }
        }

        public void addNodesOnIncomingIncidence(final Node node) {
            for (final Edge edge : graph.incomingEdgesOf(node)) {
                final Node source = graph.getEdgeSource(edge);

                if (!isNodeInRegion(source)) {
                    entryExitNodes.add(source);
                }
            }
        }

        public void addNodesOnOutgoingIncidence(final Node node) {
            for (final Edge edge : graph.outgoingEdgesOf(node)) {
                final Node target = graph.getEdgeTarget(edge);

                if (!isNodeInRegion(target)) {
                    entryExitNodes.add(target);
                }
            }
        }

        public boolean isNodeInRegion(final Node node) {
            return region.contains(node.getPoint());
        }

        public Set<Node> getEntryExitNodes() {
            return entryExitNodes;
        }
    }
}
