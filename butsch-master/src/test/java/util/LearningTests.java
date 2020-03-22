package util;

import data.Edge;
import data.RoadGraph;
import evalutation.Config;
import geometry.PolygonMerger;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import storage.ImportERPGraph;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertFalse;
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
