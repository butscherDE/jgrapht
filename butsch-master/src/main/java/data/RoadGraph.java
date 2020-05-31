package data;

import geometry.BoundingBox;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RoadGraph extends DefaultDirectedWeightedGraph<Node, Edge> {
    public static Node INVALID_NODE;
    public static Edge INVALID_EDGE;
    final Map<Long, Node> nodes = new HashMap<>();

    public RoadGraph(Class<? extends Edge> edgeClass) {
        super(edgeClass);

        INVALID_NODE = createInvalidNode();
        addInvalidNode();
        INVALID_EDGE = addInvalidEdge();
    }

    public RoadGraph(final Supplier<Node> vertexSupplier, final Supplier<Edge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier);

        INVALID_NODE = createInvalidNode();
        INVALID_EDGE = addInvalidEdge();
    }

    private Node createInvalidNode() {
        return new Node(-1, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void addInvalidNode() {
        this.addVertex(INVALID_NODE);
    }

    private Edge addInvalidEdge() {
        return this.addEdge(INVALID_NODE, INVALID_NODE);
    }

    @Override
    public boolean addVertex(Node node) {
        boolean isAdded = nodes.get(node.id) == null && super.addVertex(node);

        if (isAdded) {
            nodes.put(node.id, node);
        }

        return isAdded;
    }

    public Node getVertex(final long id) {
        return nodes.get(id);
    }

    public boolean removeVertex(final long id) {
        final Node removed = nodes.remove(id);
        return super.removeVertex(removed);
    }

    public boolean removeVertex(final Node node) {
        return removeVertex(node.id);
    }

    public int getNumNodes() {
        return vertexSet().size();
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.createFrom(this);
    }
}
