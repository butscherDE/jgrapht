package data;

import org.jgrapht.graph.GraphWalk;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();
    private static Path testPath1;
    private static Path testPath2;
    private static Path merged;

    @BeforeAll
    public static void initiateTestPaths() {
        initiateTestPath1();
        initiateTestPath2();
        initiateMergedPath();
    }

    private static void initiateTestPath1() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final List<Edge> edges = new LinkedList<>(Arrays.asList(graph.getEdge(graph.getVertex(0), graph.getVertex(1)),
                                                                graph.getEdge(graph.getVertex(1), graph.getVertex(2))));
        final double combinedWeight = graph.getEdgeWeight(edges.get(0)) + graph.getEdgeWeight(edges.get(1));

        testPath1 = new Path(new GraphWalk<>(graph, graph.getVertex(0), graph.getVertex(2), edges, combinedWeight));
    }

    private static void initiateTestPath2() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final List<Edge> edges = new LinkedList<>(Arrays.asList(graph.getEdge(graph.getVertex(2), graph.getVertex(3)),
                                                                graph.getEdge(graph.getVertex(3), graph.getVertex(4))));
        final double combinedWeight = graph.getEdgeWeight(edges.get(0)) + graph.getEdgeWeight(edges.get(1));

        testPath2 = new Path(new GraphWalk<>(graph, graph.getVertex(2), graph.getVertex(4), edges, combinedWeight));
    }

    private static void initiateMergedPath() {
        merged = testPath1.createMergedPath(testPath2);
    }

    @Test
    public void testToString() {
        final String expectedString = "(0, 0.0, 25.0, 0.0-2, 16.0, 25.0, 0.0)=[(0, 0.0, 25.0, 0.0 : 1, 8.0, 25.0, 0.0), (1, 8.0, 25.0, 0.0 : 2, 16.0, 25.0, 0.0)]";
        assertEquals(expectedString, testPath1.toString());
    }

    @Test
    public void startVertex() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        assertEquals(graph.getVertex(0), testPath1.getStartVertex());
        assertEquals(graph.getVertex(2), testPath2.getStartVertex());
        assertEquals(graph.getVertex(0), merged.getStartVertex());
    }

    @Test
    public void endVertex() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        assertEquals(graph.getVertex(2), testPath1.getEndVertex());
        assertEquals(graph.getVertex(4), testPath2.getEndVertex());
        assertEquals(graph.getVertex(4), merged.getEndVertex());
    }

    @Test
    public void length() {
        assertEquals(2, testPath1.getLength());
        assertEquals(2, testPath2.getLength());
        assertEquals(4, merged.getLength());
    }

    @Test
    public void vertexList() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final List<Node> expectedPath1Vertices = new LinkedList<>();
        expectedPath1Vertices.add(graph.getVertex(0));
        expectedPath1Vertices.add(graph.getVertex(1));
        expectedPath1Vertices.add(graph.getVertex(2));
        final List<Node> expectedPath2Vertices = new LinkedList<>();
        expectedPath2Vertices.add(graph.getVertex(2));
        expectedPath2Vertices.add(graph.getVertex(3));
        expectedPath2Vertices.add(graph.getVertex(4));
        final List<Node> expectedMergedVertices = new LinkedList<>();
        expectedMergedVertices.add(graph.getVertex(0));
        expectedMergedVertices.add(graph.getVertex(1));
        expectedMergedVertices.addAll(expectedPath2Vertices);

        assertEquals(expectedPath1Vertices, testPath1.getVertexList());
        assertEquals(expectedPath2Vertices, testPath2.getVertexList());
        assertEquals(expectedMergedVertices, merged.getVertexList());
    }

    @Test
    public void weight() {
        final double expectedTestPath1Weight = 16;
        final double expectedTestPath2Weight = 18;
        final double expectedMergedWeight = expectedTestPath1Weight + expectedTestPath2Weight;

        assertEquals(expectedTestPath1Weight, testPath1.getWeight());
        assertEquals(expectedTestPath2Weight, testPath2.getWeight());
        assertEquals(expectedMergedWeight, merged.getWeight());
    }
}
