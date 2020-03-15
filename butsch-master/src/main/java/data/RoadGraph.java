package data;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoadGraph extends DefaultDirectedWeightedGraph<Node, Edge> {
    final Map<Long, Node> nodes = new HashMap<>();

    public RoadGraph(Class<? extends Edge> edgeClass) {
        super(edgeClass);
    }

    public RoadGraph(final Supplier<Node> vertexSupplier, final Supplier<Edge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier);
    }

    @Override
    public boolean addVertex(Node node) {
        boolean isAdded = super.addVertex(node);

        if (isAdded) {
            nodes.put(node.id, node);
        }

        return isAdded;
    }

    public Node getVertex(final long id) {
        return nodes.get(id);
    }

    public int getNumNodes() {
        return vertexSet().size();
    }
}
