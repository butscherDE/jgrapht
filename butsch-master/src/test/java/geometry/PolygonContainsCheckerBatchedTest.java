package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PolygonContainsCheckerBatchedTest {
    private final static GeometryFactory GF = new GeometryFactory();
    private final static PolygonContainsCheckerBatched square = createSquarePolygon();
    private final static PolygonContainsCheckerBatched squareHole = createSquareHolePolygon();
    private final static PolygonContainsCheckerBatched smallSquare = createSmallSquarePolygon();
    private final static PolygonContainsCheckerBatched smallSquareHole = createSmallSquareHolePolygon();
    private final static PolygonContainsCheckerBatched u = createUShapedPolygon();

    @Test
    public void testContainsInSquare() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(10, 10)),
                GF.createPoint(new Coordinate(10, 16)),
                GF.createPoint(new Coordinate(-20, 10)),
                GF.createPoint(new Coordinate(0, 10)),
                GF.createPoint(new Coordinate(20, 10)),
                GF.createPoint(new Coordinate(16, 10)),
                GF.createPoint(new Coordinate(20, 20))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, true, false, true, true, true, true);

        assertEquals(groundTruth, square.contains(pointsToQuery));
    }

    @Test
    public void testContainsInSquareHole() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(10, 10)),
                GF.createPoint(new Coordinate(10, 16)),
                GF.createPoint(new Coordinate(-20, 10)),
                GF.createPoint(new Coordinate(0, 10)),
                GF.createPoint(new Coordinate(20, 10)),
                GF.createPoint(new Coordinate(16, 10)),
                GF.createPoint(new Coordinate(20, 20))
        );
        final List<Boolean> groundTruth = Arrays.asList(false, true, false, false, true, true, true);

        assertEquals(groundTruth, squareHole.contains(pointsToQuery));
    }

    @Test
    public void testContainsInSmallSquare() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(1.5,1.5)),
                GF.createPoint(new Coordinate(1.5,0.5))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, false);

        assertEquals(groundTruth, smallSquare.contains(pointsToQuery));
    }

    @Test
    public void testContainsInSmallSquareHole() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(1.1,1.1)),
                GF.createPoint(new Coordinate(1.5,1.5)),
                GF.createPoint(new Coordinate(1.5,0.5))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, false, false);

        assertEquals(groundTruth, smallSquareHole.contains(pointsToQuery));
    }

    @Test
    public void testContainsInU() {
        testUCorners();
        testUEdges();
        testUInner();
        testUOuter();
    }

    private void testUCorners() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(0,20)),
                GF.createPoint(new Coordinate(20,20)),
                GF.createPoint(new Coordinate(20,10)),
                GF.createPoint(new Coordinate(40,10)),
                GF.createPoint(new Coordinate(40,20)),
                GF.createPoint(new Coordinate(60,20)),
                GF.createPoint(new Coordinate(60,0)),
                GF.createPoint(new Coordinate(40,0)),
                GF.createPoint(new Coordinate(20,0)),
                GF.createPoint(new Coordinate(0,0))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, true, true, true, true, true, true, true, true, true);

        assertEquals(groundTruth, u.contains(pointsToQuery));
    }

    private void testUEdges() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(10,20)),
                GF.createPoint(new Coordinate(20,15)),
                GF.createPoint(new Coordinate(30, 10)),
                GF.createPoint(new Coordinate(40, 15)),
                GF.createPoint(new Coordinate(50, 20))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, true, true, true, true);

        assertEquals(groundTruth, u.contains(pointsToQuery));
    }

    private void testUInner() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(10,10)),
                GF.createPoint(new Coordinate(30,5)),
                GF.createPoint(new Coordinate(50, 10))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, true, true);

        assertEquals(groundTruth, u.contains(pointsToQuery));
    }

    private void testUOuter() {
        final List<Point> pointsToQuery = Arrays.asList(
                GF.createPoint(new Coordinate(-10, -10)),
                GF.createPoint(new Coordinate(-10, 0)),
                GF.createPoint(new Coordinate(-10, 10)),
                GF.createPoint(new Coordinate(-10, 20)),
                GF.createPoint(new Coordinate(-10, 30)),
                GF.createPoint(new Coordinate(0, 30)),
                GF.createPoint(new Coordinate(10, 30)),
                GF.createPoint(new Coordinate(20, 30)),
                GF.createPoint(new Coordinate(30, 30)),
                GF.createPoint(new Coordinate(30, 20)),
                GF.createPoint(new Coordinate(30, 11)),
                GF.createPoint(new Coordinate(40, 30))
        );
        final List<Boolean> groundTruth = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true);

        assertEquals(groundTruth, u.contains(pointsToQuery));
    }

    /*
     * |----|
     * |    |
     * |----|
     */
    private static PolygonContainsCheckerBatched createSquarePolygon() {
        final Coordinate[] coordinates = {new Coordinate(0, 0),
                                          new Coordinate(20, 0),
                                          new Coordinate(20, 20),
                                          new Coordinate(0, 20),
                                          new Coordinate(0, 0)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsCheckerBatched(polygon);
    }

    /*
     * \-----|
     *   --| |
     *   --| |
     *  /----|
     */
    private static PolygonContainsCheckerBatched createSquareHolePolygon() {
        final Coordinate[] coordinates = {new Coordinate(0, 0),
                                          new Coordinate(20, 0),
                                          new Coordinate(20, 20),
                                          new Coordinate(0, 20),
                                          new Coordinate(5, 15),
                                          new Coordinate(15, 15),
                                          new Coordinate(15, 5),
                                          new Coordinate(5, 5),
                                          new Coordinate(0, 0)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsCheckerBatched(polygon);
    }

    /*
     * |----|
     * |    |
     * |----|
     */
    private static PolygonContainsCheckerBatched createSmallSquarePolygon() {
        final Coordinate[] coordinates = {new Coordinate(1, 1),
                                          new Coordinate(2, 1),
                                          new Coordinate(2, 2),
                                          new Coordinate(1, 2),
                                          new Coordinate(1, 1)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsCheckerBatched(polygon);
    }

    /*
     * |----|
     * | /\ |
     * |/  \|
     */
    private static PolygonContainsCheckerBatched createSmallSquareHolePolygon() {
        final Coordinate[] coordinates = {new Coordinate(1, 1),
                                          new Coordinate(2, 1),
                                          new Coordinate(2, 2),
                                          new Coordinate(1.5, 1.1),
                                          new Coordinate(1, 2),
                                          new Coordinate(1, 1)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsCheckerBatched(polygon);
    }

    /*
     * |----|    |----|
     * |    |----|    |
     * |--------------|
     */
    private static PolygonContainsCheckerBatched createUShapedPolygon() {
        final Coordinate[] coordinates = {new Coordinate(0, 20),
                                          new Coordinate(20, 20),
                                          new Coordinate(20, 10),
                                          new Coordinate(40, 10),
                                          new Coordinate(40, 20),
                                          new Coordinate(60, 20),
                                          new Coordinate(60, 0),
                                          new Coordinate(40, 0),
                                          new Coordinate(20, 0),
                                          new Coordinate(0, 0),
                                          new Coordinate(0, 20)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsCheckerBatched(polygon);
    }
}
