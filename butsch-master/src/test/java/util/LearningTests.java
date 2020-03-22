package util;

import data.Edge;
import data.RoadGraph;
import evalutation.Config;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final List<Coordinate> outerPolygonCoords = getOuterCoordinates();
        final List<Coordinate> innerPolygonCoords = getInnerCoordinates();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final List<Coordinate> merged = mergePolygons(outerPolygonCoords, innerPolygonCoords, outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertEquals(expectedCoordinates, merged);
    }

    @Test
    public void polygonMergingFirstAndThirdLines() {
        final List<Coordinate> outerPolygonCoords = getOuterCoordinates();
        final List<Coordinate> innerPolygonCoords = getInnerCoordinates();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(1, 1, 1, -1);

        final List<Coordinate> merged = mergePolygons(outerPolygonCoords, innerPolygonCoords, outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertEquals(expectedCoordinates, merged);
    }

    @Test
    public void polygonMergingThirdAndFirstLines() {
        final List<Coordinate> outerPolygonCoords = getOuterCoordinates();
        final List<Coordinate> innerPolygonCoords = getInnerCoordinates();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final List<Coordinate> merged = mergePolygons(outerPolygonCoords, innerPolygonCoords, outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertEquals(expectedCoordinates, merged);
    }

    @Test
    public void polygonMergingThirdAndThirdLines() {
        final List<Coordinate> outerPolygonCoords = getOuterCoordinates();
        final List<Coordinate> innerPolygonCoords = getInnerCoordinates();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(1, 1, 1, -1);

        final List<Coordinate> merged = mergePolygons(outerPolygonCoords, innerPolygonCoords, outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertEquals(expectedCoordinates, merged);
    }

    private List<Coordinate> getOuterCoordinates() {
        final List<Coordinate> outerPolygonCoords = new LinkedList<>();
        outerPolygonCoords.add(new Coordinate(-2,-2));
        outerPolygonCoords.add(new Coordinate(-2,2));
        outerPolygonCoords.add(new Coordinate(2,2));
        outerPolygonCoords.add(new Coordinate(2,-2));
        outerPolygonCoords.add(new Coordinate(-2,-2));
        return outerPolygonCoords;
    }

    private List<Coordinate> getInnerCoordinates() {
        final List<Coordinate> innerPolygonCoords = new LinkedList<>();
        innerPolygonCoords.add(new Coordinate(-1, -1));
        innerPolygonCoords.add(new Coordinate(-1, 1));
        innerPolygonCoords.add(new Coordinate(1, 1));
        innerPolygonCoords.add(new Coordinate(1, -1));
        innerPolygonCoords.add(new Coordinate(-1, -1));
        return innerPolygonCoords;
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
