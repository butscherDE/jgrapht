package routing.regionAware.util;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimplerPolygonBuilderTest {
    @Test
    public void enlargeForwardSize() {
        assertEnlargeLimit(SimplerPolygonBuilder::removeForward);
    }

    @Test
    public void enlargeBackwardSize() {
        assertEnlargeLimit(SimplerPolygonBuilder::removeBackward);
    }

    private void assertEnlargeLimit(Function<SimplerPolygonBuilder, List<LineSegment>> function) {
        final Polygon polygon = createDefaultPolygon();

        final SimplerPolygonBuilder spb = new SimplerPolygonBuilder(polygon, createDefaultStartCoordinate());
        assertTrue(spb.isReducable());
        function.apply(spb);
        assertTrue(spb.isReducable());
        function.apply(spb);
        assertTrue(spb.isReducable());
        function.apply(spb);
        assertTrue(spb.isReducable());
        function.apply(spb);
        assertTrue(spb.isReducable());
        function.apply(spb);
        assertFalse(spb.isReducable());
    }

    @Test
    public void enlargeForward() {
        final Polygon defaultPolygon = createDefaultPolygon();
        final Coordinate[] polygonCoordinates = defaultPolygon.getCoordinates();
        final List<List<LineSegment>> expectedSegments = createForwardExpectedSegments(polygonCoordinates);

        assertCorrectLineSegmentAfterEachRemoval(
                defaultPolygon,
                expectedSegments,
                SimplerPolygonBuilder::removeForward,
                createDefaultStartCoordinate());
    }

    private List<List<LineSegment>> createForwardExpectedSegments(Coordinate[] polygonCoordinates) {
        final List<List<LineSegment>>  expectedSegments = new ArrayList<>(5);

        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        return expectedSegments;
    }

    @Test
    public void enlargeForwardMiddle() {
        final Polygon defaultPolygon = createDefaultPolygon();
        final Coordinate[] polygonCoordinates = defaultPolygon.getCoordinates();
        final List<List<LineSegment>> expectedSegments = createForwardExpectedSegmentsMiddle(polygonCoordinates);

        assertCorrectLineSegmentAfterEachRemoval(
                defaultPolygon,
                expectedSegments,
                SimplerPolygonBuilder::removeForward,
                createMiddleStartCoordinate());
    }

    private List<List<LineSegment>> createForwardExpectedSegmentsMiddle(Coordinate[] polygonCoordinates) {
        final List<List<LineSegment>>  expectedSegments = new ArrayList<>(5);

        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[1])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[2])
        ));
        return expectedSegments;
    }

    @Test
    public void enlargeBackward() {
        final Polygon defaultPolygon = createDefaultPolygon();
        final Coordinate[] polygonCoordinates = defaultPolygon.getCoordinates();
        final List<List<LineSegment>> expectedSegments = createBackwardExpectedSegments(polygonCoordinates);

        assertCorrectLineSegmentAfterEachRemoval(
                defaultPolygon,
                expectedSegments,
                SimplerPolygonBuilder::removeBackward,
                createDefaultStartCoordinate());
    }

    private List<List<LineSegment>> createBackwardExpectedSegments(Coordinate[] polygonCoordinates) {
        final List<List<LineSegment>>  expectedSegments = new ArrayList<>(5);
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[7], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[6], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[5], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4]),
                new LineSegment(polygonCoordinates[4], polygonCoordinates[5])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[4], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[4])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[3], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3])
        ));
        return expectedSegments;
    }

    @Test
    public void enlargeBackwardMiddle() {
        final Polygon defaultPolygon = createDefaultPolygon();
        final Coordinate[] polygonCoordinates = defaultPolygon.getCoordinates();
        final List<List<LineSegment>> expectedSegments = createBackwardExpectedSegmentsMiddle(polygonCoordinates);

        assertCorrectLineSegmentAfterEachRemoval(
                defaultPolygon,
                expectedSegments,
                SimplerPolygonBuilder::removeBackward,
                createMiddleStartCoordinate());
    }

    private List<List<LineSegment>> createBackwardExpectedSegmentsMiddle(Coordinate[] polygonCoordinates) {
        final List<List<LineSegment>>  expectedSegments = new ArrayList<>(5);
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[3]),
                new LineSegment(polygonCoordinates[3], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[7], polygonCoordinates[5]),
                new LineSegment(polygonCoordinates[5], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7])
        ));
        return expectedSegments;
    }

    private void assertCorrectLineSegmentAfterEachRemoval(final Polygon defaultPolygon,
                                                          final List<List<LineSegment>> expectedSegmentsSet,
                                                          final Function<SimplerPolygonBuilder, List<LineSegment>> removalFunction,
                                                          final Coordinate defaultStartCoordinate) {
        SimplerPolygonBuilder spb = new SimplerPolygonBuilder(defaultPolygon, defaultStartCoordinate);
        for (List<LineSegment> expectedSegments : expectedSegmentsSet) {
            assertTrue(spb.isReducable());
            assertEqualsLineSegmentWise(expectedSegments, removalFunction.apply(spb));
        }
        assertFalse(spb.isReducable());
    }

    private void assertEqualsLineSegmentWise(List<LineSegment> expectedSegment, List<LineSegment> actualSegments) {
        final Iterator<LineSegment> expectedIt = expectedSegment.iterator();
        final Iterator<LineSegment> actualIt = actualSegments.iterator();

        while (expectedIt.hasNext()) {
            final LineSegment nextExpected = expectedIt.next();
            final LineSegment nextActual = actualIt.next();

            assertEquals(nextExpected.p0, nextActual.p0);
            assertEquals(nextExpected.p1, nextActual.p1);
        }

        assertFalse(actualIt.hasNext());
    }

    private Polygon createDefaultPolygon() {
        final Coordinate[] polygonCoordinates = new Coordinate[] {
                createDefaultStartCoordinate(),
                new Coordinate(2, 3),
                new Coordinate(3, 2),
                new Coordinate(3, 1),
                new Coordinate(2, 0),
                new Coordinate(1, 0),
                new Coordinate(0, 1),
                new Coordinate(0, 2),
                createDefaultStartCoordinate()
        };
        return new GeometryFactory().createPolygon(polygonCoordinates);
    }

    private Coordinate createDefaultStartCoordinate() {
        return new Coordinate(1,3);
    }

    private Coordinate createMiddleStartCoordinate() {
        return new Coordinate(2, 0);
    }
}
