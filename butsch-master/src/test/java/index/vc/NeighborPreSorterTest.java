package index.vc;

import data.Node;
import data.Edge;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeighborPreSorterTest {
    @Test
    public void allNodesExist() {
        final Map<Node, SortedNeighbors> presortedNeighbors = createPresortedNeighbors();

        assertEquals(73, presortedNeighbors.size());
    }

    @Test
    public void correctOrderingExample() {
        final SortedNeighbors node7Neighbors = createPresortedNeighbors().get(7);

        final PolygonRoutingTestGraph defaultInstance = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
        final Edge lastEdge = defaultInstance.getEdge(0, 7);
        final ReflectiveEdge lastEdgeReflective = new ReflectiveEdge(lastEdge, defaultInstance.graph);
        assertEquals(19, node7Neighbors.getMostOrientedEdge(lastEdgeReflective).target);
    }

    private Map<Node, SortedNeighbors> createPresortedNeighbors() {
        final PolygonRoutingTestGraph graphMocker = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

        return new NeighborPreSorter(graphMocker.graph).getAllSortedNeighborsLeft();
    }
}
