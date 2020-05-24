package index.vc;

import data.Node;
import data.RoadGraph;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisitedEdgesHashFunctionTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    @Test
    public void isVisited() {
        final ReflectiveEdge to1Edge = get0to1Edge();
        final ReflectiveEdge to2Edge = get1to2Edge();
        final VisitedEdgesHashFunction visitedEdgesHashFunction = new VisitedEdgesHashFunction();

        visitedEdgesHashFunction.visited(to1Edge);
        assertTrue(visitedEdgesHashFunction.isVisited(to1Edge));
        assertFalse(visitedEdgesHashFunction.isVisited(to1Edge.getReversed()));
        assertFalse(visitedEdgesHashFunction.isVisited(to2Edge));
    }

    private ReflectiveEdge get0to1Edge() {
        RoadGraph graph = GRAPH_MOCKER.graph;
        Node node1 = graph.getVertex(0);
        Node node2 = graph.getVertex(1);
        return new ReflectiveEdge(0, node1, node2, graph);
    }

    private ReflectiveEdge get1to2Edge() {
        RoadGraph graph = GRAPH_MOCKER.graph;
        Node node1 = graph.getVertex(1);
        Node node2 = graph.getVertex(2);
        return new ReflectiveEdge(0, node1, node2, graph);
    }
}
