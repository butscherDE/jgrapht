package util;

import data.*;
import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.*;

public class PolygonRoutingTestGraph {
    public static PolygonRoutingTestGraph DEFAULT_INSTANCE = new PolygonRoutingTestGraph();

    private final Node[] nodes;
    private final List<Pair<Long,Long>> edges;
    private final Map<Long, Node> nodeMap = new HashMap<>();
    public RoadGraph graph;
    public RoadCH ch;
    public Polygon polygon;

    private PolygonRoutingTestGraph() {
        this(getDefaultNodeList(), getDefaultEdgeList());
    }

    public PolygonRoutingTestGraph(final Node[] nodes, final List<Pair<Long, Long>> edges) {
        this.nodes = nodes;
        this.edges = edges;

        this.createTestPolygon();
        this.createTestGraph();
        this.createPolygonTestGraphWithCh();
    }

    private void createPolygonTestGraphWithCh() {
        ch = new CHPreprocessing(graph).createCHGraph();
    }

    private RoadGraph createPolygonTestGraph() {
        this.graph = new RoadGraph(Edge.class);

        // Exterior this.graph including to Entry / Exit nodes
        buildNodes();
        buildEdges();

        return graph;
    }

    private void buildNodes() {
        for (final Node node : nodes) {
            graph.addVertex(node);
            nodeMap.put(node.id, node);
        }
    }

    public static Node[] getDefaultNodeList() {
        return new Node[]{new Node(0, 25, 0, 0),
                          new Node(1, 25, 8, 0),
                          new Node(2, 25, 16, 0),
                          new Node(3, 25, 25, 0),
                          new Node(4, 25, 34, 0),
                          new Node(5, 25, 43, 0),
                          new Node(6, 25, 46, 0),
                          new Node(7, 20, 3, 0),
                          new Node(8, 22, 8, 0),
                          new Node(9, 22, 29, 0),
                          new Node(10, 21, 35, 0),
                          new Node(11, 20, 42, 0),
                          new Node(12, 16, 34, 0),
                          new Node(13, 17, 38, 0),
                          new Node(14, 15, 43, 0),
                          new Node(15, 11, 34, 0),
                          new Node(16, 12, 38, 0),
                          new Node(17, 7, 32, 0),
                          new Node(18, 7, 38, 0),
                          new Node(19, 7, 1, 0),
                          new Node(20, 1, 1, 0),
                          new Node(21, 3, 7, 0),
                          new Node(22, 2, 13, 0),
                          new Node(23, 0, 16, 0),
                          new Node(24, 0, 21, 0),
                          new Node(25, 2, 25, 0),
                          new Node(26, 3, 33, 0),
                          new Node(27, 2, 36, 0),
                          new Node(28, 22, 16, 0),
                          new Node(29, 22, 20, 0),
                          new Node(30, 21, 23, 0),
                          new Node(31, 19, 25, 0),
                          new Node(32, 14, 30, 0),
                          new Node(33, 11, 30, 0),
                          new Node(34, 7, 27, 0),
                          new Node(35, 6, 25, 0),
                          new Node(36, 5, 22, 0),
                          new Node(37, 5, 20, 0),
                          new Node(38, 5, 17, 0),
                          new Node(39, 6, 14, 0),
                          new Node(40, 7, 11, 0),
                          new Node(41, 10, 10, 0),
                          new Node(42, 13, 9, 0),
                          new Node(43, 15, 9, 0),
                          new Node(44, 19, 10, 0),
                          new Node(45, 21, 12, 0),
                          new Node(46, 17, 16, 0),
                          new Node(47, 18, 19, 0),
                          new Node(48, 17, 22, 0),
                          new Node(49, 14, 23, 0),
                          new Node(50, 11, 22, 0),
                          new Node(51, 10, 19, 0),
                          new Node(52, 11, 16, 0),
                          new Node(53, 14, 15, 0),
                          new Node(54, 15, 18, 0),
                          new Node(55, 15, 20, 0),
                          new Node(56, 13, 20, 0),
                          new Node(57, 13, 18, 0),
                          new Node(100, 7, 42, 0),
                          new Node(101, 9, 44, 0),
                          new Node(102, 5, 44, 0),
                          new Node(103, 12, 51, 0),
                          new Node(104, 10, 51, 0),
                          new Node(105, 8, 51, 0),
                          new Node(106, 10, 47, 0),
                          new Node(107, 3, 47, 0),
                          new Node(108, 3, 41, 0),
                          new Node(109, 11, 43, 0),
                          new Node(110, 11, 43, 0),
                          new Node(111, 9, 41, 0),

                          // Multi node test part
                          new Node(200, 19, 28, 0),
                          new Node(201, 19, 28, 0),
                          new Node(202, 19, 31, 0)};
    }

    public static List<Pair<Long, Long>> getDefaultEdgeList() {
        final LinkedList<Pair<Long, Long>> edges = new LinkedList<>(Arrays.asList(
                new Pair<>(0l, 1l),
                new Pair<>(0l, 7l),
                new Pair<>(0l, 19l),
                new Pair<>(1l, 2l),
                new Pair<>(1l, 7l),
                new Pair<>(1l, 8l),
                new Pair<>(1l, 45l),
                new Pair<>(1l, 28l),
                new Pair<>(2l, 3l),
                new Pair<>(2l, 28l),
                new Pair<>(2l, 29l),
                new Pair<>(3l, 4l),
                new Pair<>(3l, 29l),
                new Pair<>(3l, 30l),
                new Pair<>(3l, 9l),
                new Pair<>(4l, 5l),
                new Pair<>(4l, 9l),
                new Pair<>(4l, 10l),
                new Pair<>(4l, 13l),
                new Pair<>(5l, 6l),
                new Pair<>(5l, 10l),
                new Pair<>(5l, 11l),
                new Pair<>(5l, 13l),
                new Pair<>(5l, 14l),
                new Pair<>(6l, 14l),
                new Pair<>(7l, 19l),
                new Pair<>(7l, 43l),
                new Pair<>(7l, 44l),
                new Pair<>(8l, 44l),
                new Pair<>(8l, 45l),
                new Pair<>(9l, 30l),
                new Pair<>(9l, 31l),
                new Pair<>(10l, 12l),
                new Pair<>(11l, 16l),
                new Pair<>(12l, 13l),
                new Pair<>(12l, 15l),
                new Pair<>(12l, 32l),
                new Pair<>(13l, 15l),
                new Pair<>(13l, 16l),
                new Pair<>(14l, 16l),
                new Pair<>(14l, 18l),
                new Pair<>(15l, 16l),
                new Pair<>(15l, 17l),
                new Pair<>(15l, 18l),
                new Pair<>(15l, 33l),
                new Pair<>(15l, 34l),
                new Pair<>(17l, 18l),
                new Pair<>(17l, 26l),
                new Pair<>(17l, 34l),
                new Pair<>(17l, 35l),
                new Pair<>(18l, 26l),
                new Pair<>(18l, 27l),
                new Pair<>(19l, 20l),
                new Pair<>(19l, 21l),
                new Pair<>(19l, 41l),
                new Pair<>(19l, 42l),
                new Pair<>(20l, 21l),
                new Pair<>(20l, 23l),
                new Pair<>(21l, 22l),
                new Pair<>(21l, 39l),
                new Pair<>(21l, 40l),
                new Pair<>(22l, 23l),
                new Pair<>(22l, 25l),
                new Pair<>(22l, 39l),
                new Pair<>(23l, 24l),
                new Pair<>(23l, 38l),
                new Pair<>(24l, 25l),
                new Pair<>(24l, 37l),
                new Pair<>(25l, 27l),
                new Pair<>(25l, 35l),
                new Pair<>(25l, 36l),
                new Pair<>(25l, 37l),
                new Pair<>(26l, 35l),

                // Entry/Exit to Interior this.graph
                new Pair<>(28l, 29l),
                new Pair<>(28l, 46l),
                new Pair<>(28l, 47l),
                new Pair<>(29l, 30l),
                new Pair<>(29l, 48l),
                new Pair<>(30l, 31l),
                new Pair<>(30l, 47l),
                new Pair<>(30l, 48l),
                new Pair<>(31l, 48l),
                new Pair<>(31l, 49l),
                new Pair<>(32l, 33l),
                new Pair<>(32l, 49l),
                new Pair<>(33l, 49l),
                new Pair<>(34l, 35l),
                new Pair<>(34l, 50l),
                new Pair<>(35l, 36l),
                new Pair<>(35l, 50l),
                new Pair<>(36l, 37l),
                new Pair<>(36l, 50l),
                new Pair<>(37l, 38l),
                new Pair<>(37l, 51l),
                new Pair<>(38l, 39l),
                new Pair<>(38l, 50l),
                new Pair<>(39l, 40l),
                new Pair<>(39l, 51l),
                new Pair<>(40l, 52l),
                new Pair<>(41l, 52l),
                new Pair<>(41l, 53l),
                new Pair<>(42l, 53l),
                new Pair<>(43l, 44l),
                new Pair<>(43l, 46l),
                new Pair<>(43l, 53l),
                new Pair<>(44l, 45l),
                new Pair<>(44l, 46l),
                new Pair<>(44l, 53l),
                new Pair<>(45l, 46l),

                // Interior this.graph
                new Pair<>(46l, 47l),
                new Pair<>(46l, 53l),
                new Pair<>(46l, 54l),
                new Pair<>(47l, 48l),
                new Pair<>(47l, 54l),
                new Pair<>(47l, 55l),
                new Pair<>(48l, 49l),
                new Pair<>(48l, 55l),
                new Pair<>(49l, 50l),
                new Pair<>(49l, 55l),
                new Pair<>(49l, 56l),
                new Pair<>(50l, 51l),
                new Pair<>(50l, 56l),
                new Pair<>(51l, 52l),
                new Pair<>(51l, 56l),
                new Pair<>(51l, 57l),
                new Pair<>(52l, 53l),
                new Pair<>(52l, 57l),
                new Pair<>(53l, 57l),
                new Pair<>(53l, 54l),
                new Pair<>(54l, 55l),
                new Pair<>(54l, 56l),
                new Pair<>(54l, 57l),
                new Pair<>(55l, 56l),
                new Pair<>(55l, 57l),
                new Pair<>(56l, 57l),

                // VisibilityCellTestScenarios
                new Pair<>(14l, 106l),
                new Pair<>(18l, 100l),
                new Pair<>(18l, 108l),
                new Pair<>(100l, 101l),
                new Pair<>(100l, 102l),
                new Pair<>(103l, 104l),
                new Pair<>(104l, 105l),
                new Pair<>(104l, 106l),
                new Pair<>(106l, 107l),
                new Pair<>(107l, 108l),
                new Pair<>(14l, 109l),
                new Pair<>(109l, 110l),
                new Pair<>(110l, 106l),
                new Pair<>(110l, 111l),

                //Multi node test part
                new Pair<>(31l, 200l),
                new Pair<>(200l, 201l),
                new Pair<>(201l, 202l)
        ));

        return edges;
    }

    private void buildEdges() {
        for (Pair<Long,Long> nodeIdPair : edges) {
            final Node sourceNode = nodeMap.get(nodeIdPair.getFirst());
            final Node targetNode = nodeMap.get(nodeIdPair.getSecond());
            final double weight = sourceNode.euclideanDistance(targetNode);

            final Edge edge = graph.addEdge(sourceNode, targetNode);
            graph.setEdgeWeight(edge, weight);
        }
    }

    private Polygon createPolygon() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(19, 14),
                new Coordinate(19,24),
                new Coordinate(8, 24),
                new Coordinate(8, 14),
                new Coordinate(19, 14)
        };
        return new GeometryFactory().createPolygon(coordinates);
    }

    private void createTestGraph() {
        this.graph = this.createPolygonTestGraph();
    }

    private void createTestPolygon() {
        this.polygon = createPolygon();
    }

    public List<Edge> getAllEdges() {
        return new LinkedList<>(graph.edgeSet());
    }

    public Edge getEdge(final int sourceNodeId, final int targetNodeId) {
        final Node sourceNode = nodeMap.get(sourceNodeId);
        final Node targetNode = nodeMap.get(targetNodeId);
        return graph.getEdge(sourceNode, targetNode);
    }

    private boolean isEdgeEqual(int baseNode, int adjNode, Edge edge) {
        final Node edgeSourceNode = graph.getEdgeSource(edge);
        final Node edgeTargetNode = graph.getEdgeTarget(edge);
        return edgeSourceNode.id == baseNode && edgeTargetNode.id == adjNode;
    }
}
