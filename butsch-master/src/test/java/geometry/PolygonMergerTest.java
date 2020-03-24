package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class PolygonMergerTest {
    @Test
    public void firstAndFirstLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstAndThirdLines() {
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
    public void thirdAndFirstLines() {
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
    public void thirdAndThirdLines() {
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

    @Test
    public void firstChosenReversedAndFirstLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstChosenReversedAndThirdLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
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
    public void thirdChosenReversedAndFirstLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
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
    public void thirdChosenReversedAndThirdLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
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

    @Test
    public void firstChosenReversedAndFirstLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstChosenReversedAndThirdLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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

        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void thirdChosenReversedAndFirstLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void thirdChosenReversedAndThirdLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void firstChosenReversedAndFirstLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstChosenReversedAndThirdLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void thirdChosenReversedAndFirstLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void thirdChosenReversedAndThirdLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void firstChosenReversedAndFirstLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstChosenReversedAndThirdLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, 2, -2, -2);
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
    public void thirdChosenReversedAndFirstLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
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
    public void thirdChosenReversedAndThirdLines() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, -2, 2, 2);
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

    @Test
    public void firstAndFirstLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, -1, -1, 1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstAndThirdLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

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
    public void thirdAndFirstLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

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
    public void thirdAndThirdLinesInnerReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

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

    @Test
    public void firstAndFirstLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstAndThirdLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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

        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void thirdAndFirstLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void thirdAndThirdLinesInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void firstAndFirstLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2, -2),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstAndThirdLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void thirdAndFirstLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void thirdAndThirdLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
    public void factor34True() {
        final PolygonMerger polygonMerger = getPolygonMerger();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(new Coordinate(-2,-2),
                                                                                    new Coordinate(-1, -1),
                                                                                    new Coordinate(1, -1),
                                                                                    new Coordinate(1, 1),
                                                                                    new Coordinate(-1, 1),
                                                                                    new Coordinate(-2, 2),
                                                                                    new Coordinate(2, 2),
                                                                                    new Coordinate(2, -2),
                                                                                    new Coordinate(-2, -2)));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void realCase1() {
        final Coordinate[] outerPolygon = new Coordinate[] {
                new Coordinate(0.36878291341130565, 0.2757480694417024),
                new Coordinate(0.30871945533265976, 0.27707849007413665),
                new Coordinate(0.17221793768785243, 0.5874273817862956),
                new Coordinate(0.20976756886633208, 0.825965871887821),
                new Coordinate(0.6655489517945736, 0.9033722646721782),
                new Coordinate(0.9193277828687169, 0.43649097442328655),
                new Coordinate(0.7499061812554475, 0.38656687435934867),
                new Coordinate(0.36878291341130565, 0.2757480694417024)
        };
        final Coordinate[] innerPolygon = new Coordinate[] {
                new Coordinate(0.17737847790937833, 0.5943499108896841),
                new Coordinate(0.46365357580915334, 0.7829017787900358),
                new Coordinate(0.7275636800328681, 0.6832234717598454),
                new Coordinate(0.17737847790937833, 0.5943499108896841)
        };
        final PolygonMerger polygonMerger = new PolygonMerger(outerPolygon, innerPolygon);

        final Coordinate outerChosenP0 = new Coordinate(0.30871945533265976, 0.27707849007413665);
        final Coordinate outerChosenP1 = new Coordinate(0.17221793768785243, 0.5874273817862956);
        final LineSegment outerChosen = new LineSegment(outerChosenP0, outerChosenP1);
        final Coordinate innerChosenP0 = new Coordinate(0.7275636800328681, 0.6832234717598454);
        final Coordinate innerChosenP1 = new Coordinate(0.17737847790937833, 0.5943499108896841);
        final LineSegment innerChosen = new LineSegment(innerChosenP0, innerChosenP1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(Arrays.asList(outerPolygon[0],
                                                                                    outerPolygon[1],
                                                                                    innerPolygon[2],
                                                                                    innerPolygon[1],
                                                                                    innerPolygon[0],
                                                                                    outerPolygon[2],
                                                                                    outerPolygon[3],
                                                                                    outerPolygon[4],
                                                                                    outerPolygon[5],
                                                                                    outerPolygon[6],
                                                                                    outerPolygon[0]));
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void realCase2() {
        final Coordinate[] outerPolygon = new Coordinate[] {
                new Coordinate(0.49732689247592055, 0.027314166285965835),
                new Coordinate(0.6351110144563881, 0.12625782329876534),
                new Coordinate(0.940172465685381, 0.3846108439172914),
                new Coordinate(0.8718145959648387, 0.805730942661998),
                new Coordinate(0.6655489517945736, 0.9033722646721782),
                new Coordinate(0.20976756886633208, 0.825965871887821),
                new Coordinate(0.17085973788289754, 0.8053907199213823),
                new Coordinate(0.03648451669024966, 0.48384385495430515),
                new Coordinate(0.195964207423156, 0.17927344087491737),
                new Coordinate(0.13978959528686086, 0.09294681694145557),
                new Coordinate(0.03141823882658079, 0.35791991947712865),
                new Coordinate(0.02702986688213338, 0.5483346917317515),
                new Coordinate(0.096450915880824, 0.7451533062153856),
                new Coordinate(0.15103155452875827, 0.8338662354441657),
                new Coordinate(0.6972487292697295, 0.908614580207571),
                new Coordinate(0.9498601346594666, 0.8204918233863466),
                new Coordinate(0.9740356814958814, 0.7134062578232291),
                new Coordinate(0.9760344716184084, 0.077085112935252),
                new Coordinate(0.49732689247592055, 0.027314166285965835)
        };
        final Coordinate[] innerPolygon = new Coordinate[] {
                new Coordinate(0.36878291341130565, 0.2757480694417024),
                new Coordinate(0.30871945533265976, 0.27707849007413665),
                new Coordinate(0.17221793768785243, 0.5874273817862956),
                new Coordinate(0.19614707188185154, 0.8091248167277394),
                new Coordinate(0.4690225206155686, 0.8273224240149951),
                new Coordinate(0.6462319787976428, 0.770465637773941),
                new Coordinate(0.8940427958184088, 0.5988370371450177),
                new Coordinate(0.9193277828687169, 0.43649097442328655),
                new Coordinate(0.8992053297295577, 0.3738361436205424),
                new Coordinate(0.7751206959271756, 0.2788223024987677),
                new Coordinate(0.36878291341130565, 0.2757480694417024)
        };
        final PolygonMerger polygonMerger = new PolygonMerger(outerPolygon, innerPolygon);

        final Coordinate outerChosenP0 = outerPolygon[4];
        final Coordinate outerChosenP1 = outerPolygon[3];
        final LineSegment outerChosen = new LineSegment(outerChosenP0, outerChosenP1);
        final Coordinate innerChosenP0 = innerPolygon[3];
        final Coordinate innerChosenP1 = innerPolygon[4];
        final LineSegment innerChosen = new LineSegment(innerChosenP0, innerChosenP1);

        final Coordinate[] merged = polygonMerger.mergePolygons(outerChosen, innerChosen);
        final List<Coordinate> expectedCoordinates = new LinkedList<>(
                Arrays.asList(new Coordinate(0.49732689247592055, 0.027314166285965835),
                              new Coordinate(0.6351110144563881, 0.12625782329876534),
                              new Coordinate(0.940172465685381, 0.3846108439172914),
                              new Coordinate(0.8718145959648387, 0.805730942661998),
                              new Coordinate(0.4690225206155686, 0.8273224240149951),
                              new Coordinate(0.6462319787976428, 0.770465637773941),
                              new Coordinate(0.8940427958184088, 0.5988370371450177),
                              new Coordinate(0.9193277828687169, 0.43649097442328655),
                              new Coordinate(0.8992053297295577, 0.3738361436205424),
                              new Coordinate(0.7751206959271756, 0.2788223024987677),
                              new Coordinate(0.36878291341130565, 0.2757480694417024),
                              new Coordinate(0.30871945533265976, 0.27707849007413665),
                              new Coordinate(0.17221793768785243, 0.5874273817862956),
                              new Coordinate(0.19614707188185154, 0.8091248167277394),
                              new Coordinate(0.6655489517945736, 0.9033722646721782),
                              new Coordinate(0.20976756886633208, 0.825965871887821),
                              new Coordinate(0.17085973788289754, 0.8053907199213823),
                              new Coordinate(0.03648451669024966, 0.48384385495430515),
                              new Coordinate(0.195964207423156, 0.17927344087491737),
                              new Coordinate(0.13978959528686086, 0.09294681694145557),
                              new Coordinate(0.03141823882658079, 0.35791991947712865),
                              new Coordinate(0.02702986688213338, 0.5483346917317515),
                              new Coordinate(0.096450915880824, 0.7451533062153856),
                              new Coordinate(0.15103155452875827, 0.8338662354441657),
                              new Coordinate(0.6972487292697295, 0.908614580207571),
                              new Coordinate(0.9498601346594666, 0.8204918233863466),
                              new Coordinate(0.9740356814958814, 0.7134062578232291),
                              new Coordinate(0.9760344716184084, 0.077085112935252),
                              new Coordinate(0.49732689247592055, 0.027314166285965835)));
//        drawForDebugging(merged, expectedCoordinates);
        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void threePolygons() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(-3,-3),
                new Coordinate(-2,-2),
                new Coordinate(-1,-1),
                new Coordinate(3,-3),
                new Coordinate(2,-2),
                new Coordinate(1,-1),
                new Coordinate(-3,3),
                new Coordinate(-2,2),
                new Coordinate(-1,1),
                new Coordinate(3,3),
                new Coordinate(2,2),
                new Coordinate(1,1),
        };
        final ConvexLayers cl = new ConvexLayers(new GeometryFactory().createMultiPointFromCoords(coordinates));

        final Coordinate[] layer1Coordinates = cl.layers[0].getCoordinates();
        final Coordinate[] layer2Coordinates = cl.layers[1].getCoordinates();
        final Coordinate[] layer3Coordinates = cl.layers[2].getCoordinates();

        final PolygonMerger polygonMerger1 = new PolygonMerger(layer1Coordinates, layer2Coordinates);
        final LineSegment outerChosen1 = new LineSegment(-3, -3, -3, 3);
        final LineSegment innerChosen1 = new LineSegment(-2, -2, -2, 2);
        final Coordinate[] mergeStep1 = polygonMerger1.mergePolygons(outerChosen1, innerChosen1);

        final PolygonMerger polygonMerger2 = new PolygonMerger(mergeStep1, layer3Coordinates);
        final LineSegment outerChosen2 = new LineSegment(2, -2, 2, 2);
        final LineSegment innerChosen2 = new LineSegment(1, 1, 1, -1);
        final Coordinate[] mergeStep2 = polygonMerger2.mergePolygons(outerChosen2, innerChosen2);

        final Coordinate[] expectedPolygon = new Coordinate[] {
                new Coordinate(-3, -3),
                new Coordinate(-2, -2),
                new Coordinate(2, -2),
                new Coordinate(1, -1),
                new Coordinate(-1, -1),
                new Coordinate(-1, 1),
                new Coordinate(1, 1),
                new Coordinate(2, 2),
                new Coordinate(-2, 2),
                new Coordinate(-3, 3),
                new Coordinate(3, 3),
                new Coordinate(3, -3),
                new Coordinate(-3, -3)
        };

        assertArrayEquals(expectedPolygon, mergeStep2);
    }

    @Test
    public void threePolygonsInversedOuterChosen() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(-3,-3),
                new Coordinate(-2,-2),
                new Coordinate(-1,-1),
                new Coordinate(3,-3),
                new Coordinate(2,-2),
                new Coordinate(1,-1),
                new Coordinate(-3,3),
                new Coordinate(-2,2),
                new Coordinate(-1,1),
                new Coordinate(3,3),
                new Coordinate(2,2),
                new Coordinate(1,1),
                };
        final ConvexLayers cl = new ConvexLayers(new GeometryFactory().createMultiPointFromCoords(coordinates));

        final Coordinate[] layer1Coordinates = cl.layers[0].getCoordinates();
        final Coordinate[] layer2Coordinates = cl.layers[1].getCoordinates();
        final Coordinate[] layer3Coordinates = cl.layers[2].getCoordinates();

        final PolygonMerger polygonMerger1 = new PolygonMerger(layer1Coordinates, layer2Coordinates);
        final LineSegment outerChosen1 = new LineSegment(-3, -3, -3, 3);
        final LineSegment innerChosen1 = new LineSegment(-2, -2, -2, 2);
        final Coordinate[] mergeStep1 = polygonMerger1.mergePolygons(outerChosen1, innerChosen1);

        final PolygonMerger polygonMerger2 = new PolygonMerger(mergeStep1, layer3Coordinates);
        final LineSegment outerChosen2 = new LineSegment(2, 2, 2, -2);
        final LineSegment innerChosen2 = new LineSegment(1, 1, 1, -1);
        final Coordinate[] mergeStep2 = polygonMerger2.mergePolygons(outerChosen2, innerChosen2);

        final Coordinate[] expectedPolygon = new Coordinate[] {
                new Coordinate(-3, -3),
                new Coordinate(-2, -2),
                new Coordinate(2, -2),
                new Coordinate(1, -1),
                new Coordinate(-1, -1),
                new Coordinate(-1, 1),
                new Coordinate(1, 1),
                new Coordinate(2, 2),
                new Coordinate(-2, 2),
                new Coordinate(-3, 3),
                new Coordinate(3, 3),
                new Coordinate(3, -3),
                new Coordinate(-3, -3)
        };

        assertArrayEquals(expectedPolygon, mergeStep2);
    }

    @Test
    public void innerMostIsLine() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(-2,-2),
                new Coordinate(-2,2),
                new Coordinate(2,2),
                new Coordinate(2,-2),
                new Coordinate(-1,0),
                new Coordinate(1,0)
        };
        final ConvexLayers cl = new ConvexLayers(new GeometryFactory().createMultiPointFromCoords(coordinates));

        final PolygonMerger merger = new PolygonMerger(cl.layers[0].getCoordinates(), cl.layers[1].getCoordinates());
        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 0, 1, 0);
        final Coordinate[] merged = merger.mergePolygons(outerChosen, innerChosen);

        final Coordinate[] expectedCoordinates = new Coordinate[] {
                new Coordinate(-2,-2),
                new Coordinate(-1,0),
                new Coordinate(1,0),
                new Coordinate(-2, 2),
                new Coordinate(2,2),
                new Coordinate(2, -2),
                new Coordinate(-2, -2)
        };

        assertArrayEquals(expectedCoordinates, merged);
    }

    @Test
    public void innerMostIsPoint() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(-2,-2),
                new Coordinate(-2,2),
                new Coordinate(2,2),
                new Coordinate(2,-2),
                new Coordinate(0,0)
        };
        final ConvexLayers cl = new ConvexLayers(new GeometryFactory().createMultiPointFromCoords(coordinates));

        final PolygonMerger merger = new PolygonMerger(cl.layers[0].getCoordinates(), cl.layers[1].getCoordinates());
        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(0, 0, 0, 0);
        final Coordinate[] merged = merger.mergePolygons(outerChosen, innerChosen);

        final Coordinate[] expectedCoordinates = new Coordinate[] {
                new Coordinate(-2,-2),
                new Coordinate(0,0),
                new Coordinate(-2, 2),
                new Coordinate(2,2),
                new Coordinate(2, -2),
                new Coordinate(-2, -2)
        };

        assertArrayEquals(expectedCoordinates, merged);
    }

    @AfterAll
    public static void summary() {
        for (final Boolean[] booleans : PolygonMerger.checker) {
            System.out.println(Arrays.toString(booleans));
        }

        final boolean[][] allBoolCombis = new boolean[][] {
                new boolean[] {false, false, false, false},
                new boolean[] {false, false, false, true},
                new boolean[] {false, false, true, false},
                new boolean[] {false, false, true, true},
                new boolean[] {false, true, false, false},
                new boolean[] {false, true, false, true},
                new boolean[] {false, true, true, false},
                new boolean[] {false, true, true, true},
                new boolean[] {true, false, false, false},
                new boolean[] {true, false, false, true},
                new boolean[] {true, false, true, false},
                new boolean[] {true, false, true, true},
                new boolean[] {true, true, false, false},
                new boolean[] {true, true, false, true},
                new boolean[] {true, true, true, false},
                new boolean[] {true, true, true, true},
        };
//        final boolean[][] allBoolCombis = new boolean[][] {
//                new boolean[] {false, false, false},
//                new boolean[] {false, false, true},
//                new boolean[] {false, true, false},
//                new boolean[] {false, true, true},
//                new boolean[] {true, false, false},
//                new boolean[] {true, false, true},
//                new boolean[] {true, true, false},
//                new boolean[] {true, true, true}
//        };

        for (final boolean[] boolCombi : allBoolCombis) {
            System.out.println(Arrays.toString(boolCombi) + ": " + count(boolCombi));
        }
    }

    private static int count(final boolean[] bools) {
        int count = 0;
        for (final Boolean[] fromTests : PolygonMerger.checker) {
            boolean allEqual = true;
            for (int i = 0; i < bools.length; i++) {
                allEqual &= (bools[i] == fromTests[i]);
            }

            count = allEqual ? count + 1 : count;
        }

        return count;
    }


    private void drawForDebugging(final Coordinate[] merged, final List<Coordinate> expectedCoordinates) {
        System.out.println(expectedCoordinates);
        System.out.println(Arrays.toString(merged));

        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(merged));
        final GeometryVisualizer visualizer = new GeometryVisualizer(col);
        visualizer.visualizeGraph(100_000);
    }

    private PolygonMerger getPolygonMerger() {
        return new PolygonMerger(getOuterCoordinates(), getInnerCoordinates());
    }

    private PolygonMerger getPolygonMergerInnerReversed() {
        final Coordinate[] outerCoordinates = getOuterCoordinates();
        final Coordinate[] innerCoordinates = getInnerCoordinates();
        ArrayUtils.reverse(innerCoordinates);
        return new PolygonMerger(outerCoordinates, innerCoordinates);
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
}
