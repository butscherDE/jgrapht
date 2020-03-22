package util;

import data.Edge;
import data.RoadGraph;
import evalutation.Config;
import geometry.PolygonMerger;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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

    @Test
    public void polygonMergingFirstLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        GeometryVisualizer.GeometryDrawCollection drawCol = new GeometryVisualizer.GeometryDrawCollection();
        drawCol.addLineSegments(Color.BLACK, coordsToLS(merged));
        GeometryVisualizer geometryVisualizer = new GeometryVisualizer(drawCol);
//        geometryVisualizer.visualizeGraph(100000);



        System.out.println(expectedCoordinates);
        System.out.println(Arrays.toString(merged));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void polygonMergingFirstAndThirdLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(1, 1, 1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void polygonMergingThirdAndFirstLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void polygonMergingThirdAndThirdLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(1, 1, 1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    private PolygonMerger getPolygonMerger() {
        return new PolygonMerger(getOuterCoordinates(), getInnerCoordinates());
    }

    private Coordinate[] getOuterCoordinates() {
        final Coordinate[] outerPolygonCoords = new Coordinate[] {
                new Coordinate(-2,-2),
                new Coordinate(-2,2),
                new Coordinate(2,2),
                new Coordinate(2,-2),
                new Coordinate(-2,-2)
        };
        return outerPolygonCoords;
    }

    private Coordinate[] getInnerCoordinates() {
        final Coordinate[] innerPolygonCoords = new Coordinate[] {
                new Coordinate(-1, -1),
                new Coordinate(-1, 1),
                new Coordinate(1, 1),
                new Coordinate(1, -1),
                new Coordinate(-1, -1)
        };
        return innerPolygonCoords;
    }

    private List<LineSegment> coordsToLS(final Coordinate[] coordinates) {
        return coordsToLS(Arrays.asList(coordinates));
    }

    private List<LineSegment> coordsToLS(final Collection<Coordinate> coordinates) {
        final List<LineSegment> lineSegments = new LinkedList<>();
        final Iterator<Coordinate> coordinateIterator = coordinates.iterator();

        Coordinate lastCoordinate = coordinateIterator.next();
        while (coordinateIterator.hasNext()) {
            final Coordinate nextCoordinate = coordinateIterator.next();
            lineSegments.add(new LineSegment(lastCoordinate, nextCoordinate));
            lastCoordinate = nextCoordinate;
        }

        return lineSegments;
    }
}
