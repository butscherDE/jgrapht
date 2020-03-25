package util;

import data.Edge;
import data.RoadGraph;
import evalutation.Config;
import org.junit.jupiter.api.Test;
import storage.ImportERPGraph;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

public class LearningTests {
    @Test
    public void noNegativeEdgeWeights() {
        try {
            final RoadGraph graph = new ImportERPGraph(Config.ERP_PATH).createGraph();
            for (final Edge edge : graph.edgeSet()) {
                assertTrue(graph.getEdgeWeight(edge) >= 0);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listIterator() {
        final List<Integer> list = new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5));

        ListIterator<Integer> iterator = list.listIterator(2);
        assertEquals(3, iterator.next());
        assertEquals(4, iterator.next());
        assertEquals(5, iterator.next());
        assertFalse(iterator.hasNext());
    }
}
