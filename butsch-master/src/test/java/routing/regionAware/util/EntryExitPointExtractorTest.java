package routing.regionAware.util;

import data.Node;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntryExitPointExtractorTest {
    private static PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();

    @Test
    public void entryExitNodes() {
        final List<Node> expectedEntryExitNodes = getExpectedEntryExitNodes();
        final List<Node> actualEntryExitNodes = getEntryExitNodes();
        sortResults(expectedEntryExitNodes, actualEntryExitNodes);

        assertEquals(expectedEntryExitNodes, actualEntryExitNodes);
    }

    public List<Node> getExpectedEntryExitNodes() {
        final List<Node> expectedEntryExitNodes = new LinkedList<>();
        expectedEntryExitNodes.add(new Node(28, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(29, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(30, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(31, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(32, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(33, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(34, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(35, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(36, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(37, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(38, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(39, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(40, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(41, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(42, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(43, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(44, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(45, 0, 0, 0));
        return expectedEntryExitNodes;
    }

    public List<Node> getEntryExitNodes() {
        final EntryExitPointExtractor entryExitPointExtractor = new EntryExitPointExtractor(GRAPH_MOCKER.polygon,
                                                                                            GRAPH_MOCKER.graph,
                                                                                            GRAPH_MOCKER.gridIndex);
        return entryExitPointExtractor.extract();
    }

    public void sortResults(final List<Node> expectedEntryExitNodes, final List<Node> actualEntryExitNodes) {
        Collections.sort(expectedEntryExitNodes, Comparator.comparingLong(n -> n.id));
        Collections.sort(actualEntryExitNodes, Comparator.comparingLong(n -> n.id));
    }
}
