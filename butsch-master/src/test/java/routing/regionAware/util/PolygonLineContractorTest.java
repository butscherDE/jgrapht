package routing.regionAware.util;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class PolygonLineContractorTest {
    @Test
    public void enlargeForwardSize() {
        assertEnlargeLimit(PolygonLineContractor::removeForward);
    }

    @Test
    public void enlargeBackwardSize() {
        assertEnlargeLimit(PolygonLineContractor::removeBackward);
    }

    private void assertEnlargeLimit(Function<PolygonLineContractor, List<LineSegment>> function) {
        final Polygon polygon = createDefaultPolygon();

        final PolygonLineContractor spb = new PolygonLineContractor(polygon, createDefaultStartCoordinate());
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
                PolygonLineContractor::removeForward,
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
                PolygonLineContractor::removeForward,
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
                PolygonLineContractor::removeBackward,
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
                PolygonLineContractor::removeBackward,
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
                                                          final Function<PolygonLineContractor, List<LineSegment>> removalFunction,
                                                          final int defaultStartCoordinate) {
        PolygonLineContractor spb = new PolygonLineContractor(defaultPolygon, defaultStartCoordinate);
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

    @Test
    public void failEnlargeForwardWhenPolygonToSmall() {
        failOnToManyRemoveInvocations(PolygonLineContractor::removeForward);
    }

    @Test
    public void failEnlargeBackwardWhenPolygonToSmall() {
        failOnToManyRemoveInvocations(PolygonLineContractor::removeBackward);
    }

    private void failOnToManyRemoveInvocations(final Function<PolygonLineContractor, List<LineSegment>> removeFunction) {
        final Polygon defaultPolygon = createDefaultPolygon();
        final PolygonLineContractor spb = new PolygonLineContractor(defaultPolygon, createDefaultStartCoordinate());

        removeFunction.apply(spb);
        removeFunction.apply(spb);
        removeFunction.apply(spb);
        removeFunction.apply(spb);
        removeFunction.apply(spb);
        assertThrows(IllegalStateException.class, () -> removeFunction.apply(spb));
    }

    @Test
    public void restartForwardContraction() {
        restartContractionSameDirection(
                PolygonLineContractor::removeForward,
                PolygonLineContractor::removeForward,
                createForwardExpectedSegmentsMiddle(createDefaultPolygon().getCoordinates())
        );
    }

    @Test
    public void restartBackwardContraction() {
        restartContractionSameDirection(
                PolygonLineContractor::removeForward,
                PolygonLineContractor::removeBackward,
                createForThenBackwardExpectedSegmentsMiddle(createDefaultPolygon().getCoordinates())
        );
    }

    private List<List<LineSegment>> createForThenBackwardExpectedSegmentsMiddle(Coordinate[] polygonCoordinates) {
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
                new LineSegment(polygonCoordinates[3], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[2]),
                new LineSegment(polygonCoordinates[2], polygonCoordinates[6]),
                new LineSegment(polygonCoordinates[6], polygonCoordinates[7]),
                new LineSegment(polygonCoordinates[7], polygonCoordinates[8])
        ));
        expectedSegments.add(Arrays.asList(
                new LineSegment(polygonCoordinates[0], polygonCoordinates[1]),
                new LineSegment(polygonCoordinates[1], polygonCoordinates[6]),
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

    private void restartContractionSameDirection(final Function<PolygonLineContractor, CircularList<LineSegment>> removeFunction1,
                                                 final Function<PolygonLineContractor, CircularList<LineSegment>> removeFunction2,
                                                 final List<List<LineSegment>> expectedSegments) {
        final Iterator<List<LineSegment>> expectedIt = expectedSegments.iterator();
        final Polygon defaultPolygon = createDefaultPolygon();
        final PolygonLineContractor spb = new PolygonLineContractor(defaultPolygon, createMiddleStartCoordinate());

        final CircularList<LineSegment> reduced = removeFunction1.apply(spb);
        assertEqualsLineSegmentWise(expectedIt.next(), reduced);

        final PolygonLineContractor spbRestarted = spb.restartAt(1);
        assertTrue(spbRestarted.isReducable());
        assertEqualsLineSegmentWise(expectedIt.next(), removeFunction2.apply(spbRestarted));
        assertTrue(spbRestarted.isReducable());
        assertEqualsLineSegmentWise(expectedIt.next(), removeFunction2.apply(spbRestarted));
        assertTrue(spbRestarted.isReducable());
        assertEqualsLineSegmentWise(expectedIt.next(), removeFunction2.apply(spbRestarted));
        assertTrue(spbRestarted.isReducable());
        assertEqualsLineSegmentWise(expectedIt.next(), removeFunction2.apply(spbRestarted));
        assertFalse(spbRestarted.isReducable());
    }

    private Polygon createDefaultPolygon() {
        final Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(1, 3),
                new Coordinate(2, 3),
                new Coordinate(3, 2),
                new Coordinate(3, 1),
                new Coordinate(2, 0),
                new Coordinate(1, 0),
                new Coordinate(0, 1),
                new Coordinate(0, 2),
                new Coordinate(1,3)
        };
        return new GeometryFactory().createPolygon(polygonCoordinates);
    }

    private int createDefaultStartCoordinate() {
        return 0;
    }

    private int createMiddleStartCoordinate() {
        return 4;
    }
}
