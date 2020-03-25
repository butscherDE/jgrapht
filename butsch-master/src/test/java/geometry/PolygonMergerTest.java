package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
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
