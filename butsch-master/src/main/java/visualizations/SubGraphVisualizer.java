package visualizations;

import data.Node;
import data.RoadGraph;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SubGraphVisualizer {
    private final RoadGraph graph;
    private final Set<Node> nodes;

    public SubGraphVisualizer(RoadGraph graph, List<Node> nodes) {
        this.graph = graph;
        this.nodes = new LinkedHashSet<>(nodes);
    }

    public void add(final Node node) {
        nodes.add(node);
    }

    /**
     * Visualizes the graph for the given time.
     *
     * @param millis Millis to wait until proceed.
     */
    public void visualize(final int millis) {
        GeometryVisualizer.GeometryDrawCollection collection = new GeometryVisualizer.GeometryDrawCollection();
        nodes.stream().forEach(a -> addNode(3, a, collection));

        new GeometryVisualizer(collection).visualizeGraph(millis);


    }

    private void addNode(final int recursionDepth, final Node node, final GeometryVisualizer.GeometryDrawCollection collection) {
        collection.addNode(Color.BLACK, node);

        if (recursionDepth < 0) {
            return;
        }

        graph.outgoingEdgesOf(node)
                .stream()
                .forEach(e -> {
                    addNode(recursionDepth - 1, graph.getEdgeTarget(e), collection);
                    collection.addEdge(Color.BLACK, e, graph);
                });
    }

}
