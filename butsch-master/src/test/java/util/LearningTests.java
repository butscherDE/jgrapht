package util;

import data.Edge;
import data.RoadGraph;
import evalutation.Config;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import storage.ImportERPGraph;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.*;

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

    @Test
    public void bunt() {
        final GeometryVisualizer.GeometryDrawCollection collection = new GeometryVisualizer.GeometryDrawCollection();
        final Random random = new Random(42);
        final Color[] colors = new Color[]{Color.BLACK, Color.RED, Color.ORANGE, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GREEN};
        final int numColors = colors.length;

        for (int i = 0; i < 100; i++) {
            if (random.nextBoolean()) {
                // draw a random line
                final LineSegment randomLineSegment = new LineSegment(random.nextDouble(),
                                                                      random.nextDouble(),
                                                                      random.nextDouble(),
                                                                      random.nextDouble());
                collection.addLineSegment(colors[random.nextInt(numColors)], randomLineSegment);
            } else {
                final Coordinate randomCoordinate = new Coordinate(random.nextDouble(),
                                                                   random.nextDouble());
                collection.addCoordinate(colors[random.nextInt(numColors)], randomCoordinate);
            }
        }

        final GeometryVisualizer visualizer = new GeometryVisualizer(collection);
        visualizer.visualizeGraph(100_000);
    }
}
