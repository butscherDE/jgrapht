package routing;

import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.Config;
import org.junit.jupiter.api.Test;
import storage.ImportERPGraph;
import util.GeneralTestGraph;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DijkstraTest {
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
    void costOf0to0() { singleDijkstraAssert(0, 0, 0); }

    private void singleDijkstraAssert(double expected, int startNodeId, int endNodeId) {
        final Dijkstra dijkstra = new Dijkstra(GeneralTestGraph.createTestGraph());
        final RoadGraph graph = dijkstra.graph;

        final double cost = dijkstra.getWeight(graph.getVertex(startNodeId), graph.getVertex(endNodeId));

        assertEquals(expected, cost, 0);
    }

    @Test
    void multipleRuns() {
        final Dijkstra dijkstra = new Dijkstra(GeneralTestGraph.createTestGraph());

        execMultipleRuns(dijkstra);
    }

    private void execMultipleRuns(Dijkstra dijkstra) {
        final RoadGraph graph = dijkstra.graph;

        assertEquals(1, dijkstra.getWeight(graph.getVertex(0), graph.getVertex(1)));
        assertEquals(1, dijkstra.getWeight(graph.getVertex(0), graph.getVertex(3)));
        assertEquals(3, dijkstra.getWeight(graph.getVertex(0), graph.getVertex(8)));
        assertEquals(1, dijkstra.getWeight(graph.getVertex(5), graph.getVertex(8)));
        assertEquals(2, dijkstra.getWeight(graph.getVertex(2), graph.getVertex(8)));
        assertEquals(2, dijkstra.getWeight(graph.getVertex(6), graph.getVertex(5)));
    }

    @Test
    void testDirected() {
        final Node node1 = new Node(0, 0, 0, 0);
        final Node node2 = new Node(1, 1, 1, 1);

        final RoadGraph graph = getDirectedGraph(node1, node2);

        try {
            final Dijkstra dijkstra = new Dijkstra(graph);
            assertEquals(10, dijkstra.getWeight(node1, node2));
            assertEquals(20, dijkstra.getWeight(node2, node1));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

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
    void testDirectedDisconnected() {
        final Node node1 = new Node(0, 0, 0, 0);
        final Node node2 = new Node(1, 1, 0, 0);

        final RoadGraph graph = getDirectedDisconnectedGraph(node1, node2);

        try {
            final Dijkstra dijkstra = new Dijkstra(graph);
            assertEquals(10, dijkstra.getWeight(node1, node2));
            assertEquals(Double.MAX_VALUE, dijkstra.getWeight(node2, node1));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
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
    public void testRealGraphExample() {
        final int startNodeId = 67267;
        final int endNodeId = 37710;

        try {
            final RoadGraph graph = new ImportERPGraph(Config.ERP_PATH).createGraph();
            final RoutingAlgorithm dijkstra = new DijkstraFactorySimple(graph).createRoutingAlgorithm();

            Node startNode = graph.getVertex(startNodeId);
            Node endNode = graph.getVertex(endNodeId);
            final double weight = dijkstra.getWeight(startNode, endNode);

            assertEquals(26085, weight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }
}
