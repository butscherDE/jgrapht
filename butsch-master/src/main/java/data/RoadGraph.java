package data;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoadGraph extends DefaultDirectedWeightedGraph<Node, Edge> {
    final Map<Integer, Node> nodes = new HashMap<>();

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

    public Node queryNode(final double longitude, final double latitude, final double elevation) {
        for (Map.Entry<Integer, Node> integerNodeEntry : nodes.entrySet()) {
            Node node = integerNodeEntry.getValue();
            if (node.longitude == longitude && node.latitude == latitude && node.elevation == elevation) {
                return node;
            }
        }

        throw new IllegalArgumentException("There does not exist a node at " + longitude + ", " + latitude + ", " + elevation);
    }

    public Node getVertex(final int id) {
        return nodes.get(id);
    }

    public int getNumNodes() {
        return vertexSet().size();
    }
}
