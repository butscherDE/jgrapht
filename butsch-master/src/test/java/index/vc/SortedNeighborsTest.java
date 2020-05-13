package index.vc;

import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortedNeighborsTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
    private final static RoadGraph graph = GRAPH_MOCKER.graph;

    @Test
    public void orderingWithoutCollinearEdges() {
        final SortedNeighbors sortedNeighbors = getSortedNeighbors(GRAPH_MOCKER, 57, 52);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(56),
                graph.getVertex(55),
                graph.getVertex(54),
                graph.getVertex(53),
                graph.getVertex(52),
                graph.getVertex(51)};

        assertOrdering(expectedOrder, sortedNeighbors);
        final Edge edge = GRAPH_MOCKER.getEdge(57, 52);
        final ReflectiveEdge edgeReflective = new ReflectiveEdge(edge, graph);
        final ReflectiveEdge mostOrientedEdge = sortedNeighbors.getMostOrientedEdge(edgeReflective);
        assertEquals(53, mostOrientedEdge.target);
    }

    @Test
    public void orderingWithMultiEdges() {
        final PolygonRoutingTestGraph graphMocker = getMultiEdgeTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        assertEquals(0, sortedNeighbors.get(0).id);
        assertEquals(1, sortedNeighbors.get(1).id);
    }

    private PolygonRoutingTestGraph getMultiEdgeTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, Double.MIN_VALUE, 0, 0),
                new Node(1, 0, 1, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(0,1));

        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void collinearInEdges1() {
        final PolygonRoutingTestGraph graphMocker = getCollinearInEdgesTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 2, 0);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(3),
                graph.getVertex(0),
                graph.getVertex(1)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    @Test
    public void collinearInEdges2() {
        final PolygonRoutingTestGraph graphMocker = getCollinearInEdgesTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 2, 1);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(3),
                graph.getVertex(0),
                graph.getVertex(1)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    private PolygonRoutingTestGraph getCollinearInEdgesTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, Double.MIN_VALUE, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 2, 0),
                new Node(3, 0, 3, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,2));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void collinearOutEdges() {
        final PolygonRoutingTestGraph graphMocker = getCollinearOutEdgesTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(2),
                graph.getVertex(3),
                graph.getVertex(0)};

        assertOrdering(expectedOrder, sortedNeighbors);
        final Edge edge = graphMocker.getEdge(1, 0);
        final ReflectiveEdge edgeReflective = new ReflectiveEdge(edge, graph);
        assertEquals(3, sortedNeighbors.getMostOrientedEdge(edgeReflective).target);
    }

    private PolygonRoutingTestGraph getCollinearOutEdgesTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 0, 0),
                new Node(1, Double.MIN_VALUE, 1, 0),
                new Node(2, 0, 2, 0),
                new Node(3, 0, 3, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(1,3));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void multipleNeighborsWithSameCoordinates() {
        final PolygonRoutingTestGraph graphMocker = getMultipleNeighborsWithSameCoordinatesTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(2),
                graph.getVertex(3),
                graph.getVertex(0)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    private PolygonRoutingTestGraph getMultipleNeighborsWithSameCoordinatesTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, Double.MIN_VALUE, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 2, 0),
                new Node(3, 0, 2, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,2));
        edges.add(new Pair(1,2));
        edges.add(new Pair(1,3));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void equalCoordinateNeighbor() {
        final PolygonRoutingTestGraph graphMocker = getEqualCoordinateNeighborTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(3),
                graph.getVertex(0)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    private PolygonRoutingTestGraph getEqualCoordinateNeighborTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, Double.MIN_VALUE, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 1, 0),
                new Node(3, 0, 2, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void equalCoordinateNeighborPath() {
        final PolygonRoutingTestGraph graphMocker = getEqualCoordinateNeighborPathTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        final Node[] expectedOrder = new Node[]{
                graph.getVertex(5),
                graph.getVertex(0)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    private PolygonRoutingTestGraph getEqualCoordinateNeighborPathTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, Double.MIN_VALUE, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 1, 0),
                new Node(3, 0, 1, 0),
                new Node(4, 0, 1, 0),
                new Node(5, 0, 2, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        edges.add(new Pair(3,4));
        edges.add(new Pair(4,5));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void equalCoordinateImpasse() {
        final PolygonRoutingTestGraph graphMocker = equalCoordinateImpasseTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        final Node[] expectedOrder = new Node[]{graph.getVertex(0)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    private PolygonRoutingTestGraph equalCoordinateImpasseTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 0, 0),
                new Node(1, Double.MIN_VALUE, 1, 0),
                new Node(2, 0, 1, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void equalCoordinateNeighborPathImpasse() {
        final PolygonRoutingTestGraph graphMocker = equalCoordinateNeighborPathImpasseTestGraph();

        final SortedNeighbors sortedNeighbors = getSortedNeighbors(graphMocker, 1, 0);

        final Node[] expectedOrder = new Node[]{graph.getVertex(0)};

        assertOrdering(expectedOrder, sortedNeighbors);
    }

    private PolygonRoutingTestGraph equalCoordinateNeighborPathImpasseTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, Double.MIN_VALUE, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 1, 0),
                new Node(3, 0, 1, 0),
                new Node(4, 0, 1, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        edges.add(new Pair(3,4));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    private SortedNeighbors getSortedNeighbors(final PolygonRoutingTestGraph graphMocker, final int baseNode, final int adjNode) {
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final Edge lastEdgeReversed = graphMocker.getEdge(baseNode, adjNode);
        return new SortedNeighbors(graphMocker.graph, graph.getEdgeSource(lastEdgeReversed), SortedNeighbors.DO_NOT_IGNORE_NODE, vac);
    }

    private void assertOrdering(Node[] expectedOrder, SortedNeighbors sortedNeighbors) {
        final Node[] actualOrder = extractAdjNodes(sortedNeighbors);
        System.out.println("Expected: " + Arrays.toString(expectedOrder));
        System.out.println("Actual: " + Arrays.toString(actualOrder));
        assertArrayEquals(expectedOrder, actualOrder);
    }

    private Node[] extractAdjNodes(SortedNeighbors sortedNeighbors) {
        final Node[] actualOrder = new Node[sortedNeighbors.size()];

        for (int i = 0; i < sortedNeighbors.size(); i++) {
            actualOrder[i] = sortedNeighbors.get(i).target;
        }
        return actualOrder;
    }
}
