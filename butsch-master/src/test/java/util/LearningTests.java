package util;

import data.Edge;
import data.RoadGraph;
import evalutation.Config;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
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
    public void translatePolygon() {
        final Coordinate[] inCoords = new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(1,0),
                new Coordinate(1,1),
                new Coordinate(0,0)
        };

        final Polygon polygon = new GeometryFactory().createPolygon(inCoords);
        final Coordinate[] coordinates = polygon.getCoordinates();
        for (Coordinate coordinate : coordinates) {
            coordinate.x += 1;
        }

        final Coordinate[] expectedCoords = {
                new Coordinate(1, 0),
                new Coordinate(2, 0),
                new Coordinate(2, 1),
                new Coordinate(1, 0)
        };
        assertArrayEquals(expectedCoords, polygon.getCoordinates());
    }
}
