package routing;

import data.*;
import evalutation.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import storage.ImportERPGraph;
import util.GeneralTestGraph;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DijkstraCHTest {
    private static RoadGraph erpGraph;
    private static RoadCH erpCh;

    @BeforeAll
    public static void prepareRealRoadNetwork() {
        try {
            erpGraph = new ImportERPGraph(Config.ERP_PATH).createGraph();
            erpCh = getChGraph(erpGraph);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void costOf0to1() {
        singleDijkstraAssert(1, 0, 1);
    }

    @Test
    void costOf0to3() {
        singleDijkstraAssert(1, 0, 3);
    }

    @Test
    void costOf0to8() {
        singleDijkstraAssert(3, 0, 8);
    }

    @Test
    void costOf5to8() {
        singleDijkstraAssert(1, 5, 8);
    }

    @Test
    void costOf2to8() {
        singleDijkstraAssert(2, 2, 8);
    }

    @Test
    void costOf6to5() {
        singleDijkstraAssert(2, 6, 5);
    }

    @Test
    void costOf0to0() {
        singleDijkstraAssert(0, 0, 0);
    }

    private void singleDijkstraAssert(double expected, int startNodeId, int endNodeId) {
        RoadGraph testGraph = GeneralTestGraph.createTestGraph();
        RoadCH chGraph = getChGraph(testGraph);

        final Node startNode = testGraph.getVertex(startNodeId);
        final Node endNode = testGraph.getVertex(endNodeId);

        final DijkstraCH dijkstra = new DijkstraCH(chGraph, true);
        final double cost = dijkstra.getWeight(startNode, endNode);

        assertEquals(expected, cost);
    }

    @Test
    void multipleRuns() {
        final DijkstraCH dijkstra = new DijkstraCH(getChGraph(GeneralTestGraph.createTestGraph()), true);
        final RoadGraph graph = dijkstra.graph;

        final Node node0 = graph.getVertex(0);
        final Node node1 = graph.getVertex(1);
        final Node node2 = graph.getVertex(2);
        final Node node3 = graph.getVertex(3);
        final Node node5 = graph.getVertex(5);
        final Node node6 = graph.getVertex(6);
        final Node node8 = graph.getVertex(8);

        assertEquals(1, dijkstra.getWeight(node0, node1));
        assertEquals(1, dijkstra.getWeight(node0, node3));
        assertEquals(3, dijkstra.getWeight(node0, node8));
        assertEquals(1, dijkstra.getWeight(node5, node8));
        assertEquals(2, dijkstra.getWeight(node2, node8));
        assertEquals(2, dijkstra.getWeight(node6, node5));
    }

    @Test
    void testAllAcceptanceNodeFilter() {
        costOf0to1();
        costOf0to3();
        costOf0to8();
        costOf2to8();
        costOf5to8();
        costOf6to5();
        multipleRuns();
    }

    @Test
    void testDirected() {
        final Node node1 = new Node(0, 0, 0, 0);
        final Node node2 = new Node(1, 1, 1, 1);
        final RoadGraph graph = getDirectedGraph(node1, node2);
        final RoadCH ch = getChGraph(graph);

        final DijkstraCH dijkstra = new DijkstraCH(ch, true);
        assertEquals(10, dijkstra.getWeight(node1, node2));
        assertEquals(20, dijkstra.getWeight(node2, node1));
    }

    private RoadGraph getDirectedGraph(final Node node1, final Node node2) {
        final RoadGraph graph = new RoadGraph(Edge.class);

        addDirectedVertices(node1, node2, graph);
        addDirectedEdges(node1, node2, graph);

        return graph;
    }

    private void addDirectedVertices(final Node node1, final Node node2, final RoadGraph graph) {
        graph.addVertex(node1);
        graph.addVertex(node2);
    }

    private void addDirectedEdges(final Node node1, final Node node2, final RoadGraph graph) {
        final Edge edge1 = graph.addEdge(node1, node2);
        graph.setEdgeWeight(edge1, 10);
        final Edge edge2 = graph.addEdge(node2, node1);
        graph.setEdgeWeight(edge2, 20);
    }

    @Test
    void testDirectedLong() {
        final Node[] nodes = new Node[]{new Node(0, 0, 0, 0), new Node(1, 1, 1, 1), new Node(2, 2, 2, 2), new Node(3, 3,
                                                                                                                   3,
                                                                                                                   3)};
        final RoadGraph graph = getDirectedLongGraph(nodes);
        final RoadCH ch = getChGraph(graph);


        final DijkstraCH dijkstra = new DijkstraCH(ch, true);
        assertEquals(30, dijkstra.getWeight(nodes[0], nodes[3]));
        assertEquals(40, dijkstra.getWeight(nodes[3], nodes[0]));
    }

    private RoadGraph getDirectedLongGraph(final Node[] nodes) {
        final RoadGraph graph = new RoadGraph(Edge.class);

        addDirectedLongVertices(nodes, graph);
        addDirectedLongEdges(nodes, graph);

        return graph;
    }

    private void addDirectedLongVertices(final Node[] nodes, final RoadGraph graph) {
        for (final Node node : nodes) {
            graph.addVertex(node);
        }
    }

    private void addDirectedLongEdges(final Node[] nodes, final RoadGraph graph) {
        Edge edge = graph.addEdge(nodes[0], nodes[1]);
        graph.setEdgeWeight(edge, 10);
        edge = graph.addEdge(nodes[1], nodes[0]);
        graph.setEdgeWeight(edge, 10);
        edge = graph.addEdge(nodes[1], nodes[2]);
        graph.setEdgeWeight(edge, 10);
        edge = graph.addEdge(nodes[2], nodes[1]);
        graph.setEdgeWeight(edge, 20);
        edge = graph.addEdge(nodes[2], nodes[3]);
        graph.setEdgeWeight(edge, 10);
        edge = graph.addEdge(nodes[3], nodes[2]);
        graph.setEdgeWeight(edge, 10);
    }

    @Test
    void testDirectedDisconnected() {
        final Node node1 = new Node(0, 0, 0, 0);
        final Node node2 = new Node(1, 1, 1, 1);
        final RoadGraph graph = getDirectedDisconnectedGraph(node1, node2);
        final RoadCH ch = getChGraph(graph);

        final DijkstraCH dijkstra = new DijkstraCH(ch, true);
        assertEquals(10, dijkstra.getWeight(node1, node2));
        assertEquals(Double.MAX_VALUE, dijkstra.getWeight(node2, node1));
    }

    private RoadGraph getDirectedDisconnectedGraph(final Node node1, final Node node2) {
        final RoadGraph graph = new RoadGraph(Edge.class);

        addDirectedDisconnectedVertices(node1, node2, graph);
        addDirectedDisconnectedEdges(node1, node2, graph);

        return graph;
    }

    private void addDirectedDisconnectedVertices(final Node node1, final Node node2, final RoadGraph graph) {
        graph.addVertex(node1);
        graph.addVertex(node2);
    }

    private void addDirectedDisconnectedEdges(final Node node1, final Node node2, final RoadGraph graph) {
        final Edge edge = graph.addEdge(node1, node2);
        graph.setEdgeWeight(edge, 10);
    }

    @Test
    void compareCostToTraditionalDijkstra() {
        final Dijkstra dijkstra = new Dijkstra(erpGraph);
        final DijkstraCH dijkstraCH = new DijkstraCH(erpCh, true);

        final Random random = new Random();
        final int numNodes = erpGraph.getNumNodes();
        for (int i = 0; i < 100; i++) {
            final int startNodeId = random.nextInt(numNodes);
            final int endNodeId = random.nextInt(numNodes);

            final Node startNode = erpGraph.getVertex(startNodeId);
            final Node endNode = erpGraph.getVertex(endNodeId);

            final double dijkstraCost = dijkstra.getWeight(startNode, endNode);
            final double dijkstraBiDirCost = dijkstraCH.getWeight(startNode, endNode);
            assertEquals(dijkstraCost, dijkstraBiDirCost, "startNode: " + startNode + ", endNode: " + endNode);
        }
    }

    private static RoadCH getChGraph(final RoadGraph graph) {
        final CHPreprocessing chPreprocessing = new CHPreprocessing(graph);
        final RoadCH roadCH = chPreprocessing.createCHGraph();
        return roadCH;
    }

    @Test
    public void route5699to91369() {
        try {
            Node startNode = erpGraph.getVertex(5699);
            Node endNode = erpGraph.getVertex(91369);

            final DijkstraCH dch = new DijkstraCH(erpCh, true);
            dch.getWeight(startNode, endNode);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
