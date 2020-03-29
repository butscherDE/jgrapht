package data;

import org.jgrapht.graph.GraphWalk;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();
    private static Path testPath1;
    private static Path testPath2;
    private static Path merged;
    private static Path[] selfIntersectingPaths;

    @BeforeAll
    public static void initiateTestPaths() {
        setInitiateTestPath1();
        setInitiateTestPath2();
        setInitiateMergedPath();
        setSelfIntersectingPaths();
    }

    private static void setInitiateTestPath1() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final List<Edge> edges = new LinkedList<>(Arrays.asList(graph.getEdge(graph.getVertex(0), graph.getVertex(1)),
                                                                graph.getEdge(graph.getVertex(1), graph.getVertex(2))));
        final double combinedWeight = graph.getEdgeWeight(edges.get(0)) + graph.getEdgeWeight(edges.get(1));

        testPath1 = new Path(graph, graph.getVertex(0), graph.getVertex(2), edges, combinedWeight);
    }

    private static void setInitiateTestPath2() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final List<Edge> edges = new LinkedList<>(Arrays.asList(graph.getEdge(graph.getVertex(2), graph.getVertex(3)),
                                                                graph.getEdge(graph.getVertex(3), graph.getVertex(4))));
        final double combinedWeight = graph.getEdgeWeight(edges.get(0)) + graph.getEdgeWeight(edges.get(1));

        testPath2 = new Path(graph, graph.getVertex(2), graph.getVertex(4), edges, combinedWeight);
    }

    private static void setInitiateMergedPath() {
        merged = testPath1.createMergedPath(testPath2);
    }

    private static void setSelfIntersectingPaths() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final List<Edge> selfIntersecting1 = new LinkedList<>(
                Arrays.asList(graph.getEdge(graph.getVertex(0), graph.getVertex(1)),
                              graph.getEdge(graph.getVertex(1), graph.getVertex(7)),
                              graph.getEdge(graph.getVertex(7), graph.getVertex(0)))
        );
        final List<Edge> selfIntersecting2 = new LinkedList<>(
                Arrays.asList(graph.getEdge(graph.getVertex(0), graph.getVertex(1)),
                              graph.getEdge(graph.getVertex(1), graph.getVertex(2)),
                              graph.getEdge(graph.getVertex(2), graph.getVertex(3)),
                              graph.getEdge(graph.getVertex(3), graph.getVertex(29)),
                              graph.getEdge(graph.getVertex(29), graph.getVertex(28)),
                              graph.getEdge(graph.getVertex(28), graph.getVertex(1)),
                              graph.getEdge(graph.getVertex(1), graph.getVertex(2)))
        );
        final List<Edge> selfIntersecting3 = new LinkedList<>(
                Arrays.asList(graph.getEdge(graph.getVertex(0), graph.getVertex(1)),
                              graph.getEdge(graph.getVertex(1), graph.getVertex(7)),
                              graph.getEdge(graph.getVertex(7), graph.getVertex(0)),
                              graph.getEdge(graph.getVertex(0), graph.getVertex(1)))
        );

        selfIntersectingPaths = new Path[] {
                new Path(graph, graph.getVertex(0), graph.getVertex(0), selfIntersecting1, 0),
                new Path(graph, graph.getVertex(0), graph.getVertex(2), selfIntersecting2, 0),
                new Path(graph, graph.getVertex(0), graph.getVertex(1), selfIntersecting3, 0)
        };
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

    @Test
    public void selfIntersecting() {
        assertTrue(selfIntersectingPaths[0].isSelfIntersecting());
        assertTrue(selfIntersectingPaths[1].isSelfIntersecting());
        assertTrue(selfIntersectingPaths[2].isSelfIntersecting());
    }

    @Test
    public void notSelfIntersecting() {
        assertFalse(testPath1.isSelfIntersecting());
        assertFalse(testPath2.isSelfIntersecting());
        assertFalse(merged.isSelfIntersecting());
    }

    @Test
    public void pathFound() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Node startAndEndVertex = graph.getVertex(0);
        final Path zeroLengthValidPath = new Path(graph, startAndEndVertex, startAndEndVertex, Collections.emptyList(), 0);

        assertTrue(selfIntersectingPaths[0].isFound());
        assertTrue(selfIntersectingPaths[1].isFound());
        assertTrue(selfIntersectingPaths[2].isFound());
        assertTrue(testPath1.isFound());
        assertTrue(testPath2.isFound());
        assertTrue(merged.isFound());
        assertTrue(zeroLengthValidPath.isFound());
    }

    @Test
    public void pathNotFound() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Node startNode = graph.getVertex(0);
        final Node endNode = graph.getVertex(6);
        final Path unfoundPath = new Path(graph, startNode, endNode, Collections.emptyList(), Double.MAX_VALUE);

        assertFalse(unfoundPath.isFound());
    }
}
