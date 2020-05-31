package index.vc;

import data.Edge;
import data.Node;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeighborPreSorterTest {

    private final PolygonRoutingTestGraph graphMocker = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    @Test
    public void allNodesExist() {
        final Map<Node, SortedNeighbors> presortedNeighbors = createPresortedNeighbors();

        assertEquals(74, presortedNeighbors.size());
    }

    @Test
    public void correctOrderingExample() {
        final Map<Node, SortedNeighbors> presortedNeighbors = createPresortedNeighbors();
        final SortedNeighbors node7Neighbors = presortedNeighbors.get(graphMocker.graph.getVertex(7L));

        final PolygonRoutingTestGraph defaultInstance = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
        final Edge lastEdge = defaultInstance.getEdge(0, 7);
        final ReflectiveEdge lastEdgeReflective = new ReflectiveEdge(lastEdge, defaultInstance.graph);
        final ReflectiveEdge mostOrientedEdge = node7Neighbors.getMostOrientedEdge(lastEdgeReflective);
        assertEquals(graphMocker.graph.getVertex(19), mostOrientedEdge.target);
    }

    private Map<Node, SortedNeighbors> createPresortedNeighbors() {
        return new NeighborPreSorter(graphMocker.graph).getAllSortedNeighborsLeft();
    }
}
