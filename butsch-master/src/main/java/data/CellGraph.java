package data;

import geometry.BoundingBox;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CellGraph extends DefaultUndirectedWeightedGraph<Node, Edge> {
    public static Node INVALID_NODE;
    public static Edge INVALID_EDGE;
    final Map<Long, Node> nodes = new HashMap<>();

    public CellGraph(final Class<? extends Edge> edgeClass) {
        super(edgeClass);

        INVALID_NODE = createInvalidNode();
        addInvalidNode();
        INVALID_EDGE = addInvalidEdge();
    }

    public CellGraph(final Supplier<Node> vertexSupplier, final Supplier<Edge> edgeSupplier) {
        super(vertexSupplier, edgeSupplier);

        INVALID_NODE = createInvalidNode();
        INVALID_EDGE = addInvalidEdge();
    }

    public static CellGraph create(final RoadGraph roadGraph) {
        final CellGraph cellGraph = new CellGraph(Edge.class);

        roadGraph.vertexSet().stream().forEach(node -> cellGraph.addVertex(node));
        roadGraph.edgeSet().stream().forEach(edge -> {
            final Node edgeSource = roadGraph.getEdgeSource(edge);
            final Node edgeTarget = roadGraph.getEdgeTarget(edge);
            final double edgeWeight = roadGraph.getEdgeWeight(edge);
            cellGraph.addEdge(edgeSource, edgeTarget);
            cellGraph.setEdgeWeight(edgeSource, edgeTarget, edgeWeight);
        });

        return cellGraph;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        vertexSet().stream().forEach(a -> sb.append(a.toString() + "\n"));
        edgeSet().stream().forEach(a -> sb.append(a.toString() + "\n"));

        return sb.toString();
    }

    public RoadGraph deepCopy() {
        final RoadGraph newGraph = new RoadGraph(Edge.class);

        addVertices(newGraph);
        addAllEdgesAndSetWeight(newGraph);

        return newGraph;
    }

    private void addVertices(RoadGraph newGraph) {
        vertexSet().stream().forEach(a -> newGraph.addVertex(a));
    }

    private void addAllEdgesAndSetWeight(RoadGraph newGraph) {
        edgeSet().stream().filter(a -> newGraph.getEdgeSource(a).id >= 0).forEach(e -> {
            final Node edgeSource = getEdgeSource(e);
            final Node edgeTarget = getEdgeTarget(e);

            final Edge newEdge = newGraph.addEdge(edgeSource, edgeTarget);
            newGraph.setEdgeWeight(newEdge, getEdgeWeight(e));
        });
    }


}
