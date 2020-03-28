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

    public PolygonRoutingTestGraph() {
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
        return new Node[]{new Node(0, 0,25,  0),
                          new Node(1, 8, 25,  0),
                          new Node(2, 16, 25,  0),
                          new Node(3, 25,25,  0),
                          new Node(4, 34,25,  0),
                          new Node(5, 43,25,  0),
                          new Node(6, 46,25,  0),
                          new Node(7, 3,20,  0),
                          new Node(8, 8,22,  0),
                          new Node(9, 29,22,  0),
                          new Node(10, 35,21,  0),
                          new Node(11, 42,20,  0),
                          new Node(12, 34,16,  0),
                          new Node(13, 38,17,  0),
                          new Node(14, 43,15,  0),
                          new Node(15, 34,11,  0),
                          new Node(16, 38,12,  0),
                          new Node(17, 32,7,  0),
                          new Node(18, 38,7,  0),
                          new Node(19, 1,7, 0),
                          new Node(20, 1,1, 0),
                          new Node(21, 7,3, 0),
                          new Node(22, 13,2,  0),
                          new Node(23, 16,0,  0),
                          new Node(24, 21,0,  0),
                          new Node(25, 25,2,  0),
                          new Node(26, 33,3,  0),
                          new Node(27, 36,2,  0),
                          new Node(28, 16,22,  0),
                          new Node(29, 20,22,  0),
                          new Node(30, 23,21,  0),
                          new Node(31, 25,19,  0),
                          new Node(32, 30,14,  0),
                          new Node(33, 30,11,  0),
                          new Node(34, 27,7,  0),
                          new Node(35, 25,6,  0),
                          new Node(36, 22,5,  0),
                          new Node(37, 20,5,  0),
                          new Node(38, 17,5,  0),
                          new Node(39, 14,6,  0),
                          new Node(40, 11,7,  0),
                          new Node(41, 10,10,  0),
                          new Node(42, 9,13,  0),
                          new Node(43, 9,15,  0),
                          new Node(44, 10,19,  0),
                          new Node(45, 12,21,  0),
                          new Node(46, 16,17,  0),
                          new Node(47, 19,18,  0),
                          new Node(48, 22,17,  0),
                          new Node(49, 23,14,  0),
                          new Node(50, 22,11,  0),
                          new Node(51, 19,10,  0),
                          new Node(52, 16,11,  0),
                          new Node(53, 15,14,  0),
                          new Node(54, 18,15,  0),
                          new Node(55, 20,15,  0),
                          new Node(56, 20,13,  0),
                          new Node(57, 18,13,  0),
                          new Node(100, 42,7,  0),
                          new Node(101, 44,9,  0),
                          new Node(102, 44,5,  0),
                          new Node(103, 51,12,  0),
                          new Node(104, 51,10,  0),
                          new Node(105, 51,8,  0),
                          new Node(106, 47,10,  0),
                          new Node(107, 47,3,  0),
                          new Node(108, 41,3,  0),
                          new Node(109, 43,11,  0),
                          new Node(110, 43,11,  0),
                          new Node(111, 41,9,  0),

                          // Multi node test part
                          new Node(200, 28, 19, 0),
                          new Node(201, 28, 19, 0),
                          new Node(202, 31, 19,0)};
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

            final Edge edge2 = graph.addEdge(targetNode, sourceNode);
            graph.setEdgeWeight(edge2, weight);
        }
    }

    private Polygon createPolygon() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(14, 19),
                new Coordinate(24, 19),
                new Coordinate(24, 8),
                new Coordinate(14, 8),
                new Coordinate(14, 19)
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
