package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
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

        System.out.println(expectedCoordinates);
        System.out.println(Arrays.toString(merged));

        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(merged));
        final GeometryVisualizer visualizer = new GeometryVisualizer(col);
//        visualizer.visualizeGraph(100_000);
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
                                                                                    new Coordinate(1,1),
                                                                                    new Coordinate(-1,1),
                                                                                    new Coordinate(-1,-1),
                                                                                    new Coordinate(1,-1),
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

        System.out.println(expectedCoordinates);
        System.out.println(Arrays.toString(merged));

        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(merged));
        final GeometryVisualizer visualizer = new GeometryVisualizer(col);
        //        visualizer.visualizeGraph(100_000);

        assertArrayEquals(expectedCoordinates.toArray(), merged);
    }

    @Test
    public void firstAndThirdLinesInnerReversedInnerChosenReversed() {
        final PolygonMerger polygonMerger = getPolygonMergerInnerReversed();

        final LineSegment outerChosen = new LineSegment(-2, -2, -2, 2);
        final LineSegment innerChosen = new LineSegment(-1, 1, -1, -1);

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
