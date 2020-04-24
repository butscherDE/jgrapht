package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolygonContainsCheckerTest {
    private final static GeometryFactory GF = new GeometryFactory();
    private final static PolygonContainsChecker square = createSquarePolygon();
    private final static PolygonContainsChecker squareHole = createSquareHolePolygon();
    private final static PolygonContainsChecker smallSquare = createSmallSquarePolygon();
    private final static PolygonContainsChecker smallSquareHole = createSmallSquareHolePolygon();
    private final static PolygonContainsChecker u = createUShapedPolygon();

    @Test
    public void testContainsInSquare() {
        assertTrue(this.square.contains(GF.createPoint(new Coordinate(10, 10))));
        assertTrue(this.square.contains(GF.createPoint(new Coordinate(10, 16))));
        assertFalse(this.square.contains(GF.createPoint(new Coordinate(-20, 10))));
        assertTrue(this.square.contains(GF.createPoint(new Coordinate(0, 10))));
        assertTrue(this.square.contains(GF.createPoint(new Coordinate(20, 10))));
        assertTrue(this.square.contains(GF.createPoint(new Coordinate(16, 10))));
        assertTrue(this.square.contains(GF.createPoint(new Coordinate(20, 20))));
    }

    @Test
    public void testContainsInSquareHole() {
        assertFalse(this.squareHole.contains(GF.createPoint(new Coordinate(10, 10))));
        assertTrue(this.squareHole.contains(GF.createPoint(new Coordinate(10, 16))));
        assertFalse(this.squareHole.contains(GF.createPoint(new Coordinate(-20, 10))));
        assertFalse(this.squareHole.contains(GF.createPoint(new Coordinate(0, 10))));
        assertTrue(this.squareHole.contains(GF.createPoint(new Coordinate(20, 10))));
        assertTrue(this.squareHole.contains(GF.createPoint(new Coordinate(16, 10))));
        assertTrue(this.squareHole.contains(GF.createPoint(new Coordinate(20, 20))));
    }

    @Test
    public void testContainsInSmallSquare() {
        assertTrue(this.smallSquare.contains(GF.createPoint(new Coordinate(1.5,1.5))));
        assertFalse(this.smallSquare.contains(GF.createPoint(new Coordinate(1.5,0.5))));
    }

    @Test
    public void testContainsInSmallSquareHole() {
        assertTrue(this.smallSquareHole.contains(GF.createPoint(new Coordinate(1.1,1.1))));
        assertFalse(this.smallSquareHole.contains(GF.createPoint(new Coordinate(1.5,1.5))));
        assertFalse(this.smallSquareHole.contains(GF.createPoint(new Coordinate(1.5,0.5))));
    }

    @Test
    public void testContainsInU() {
        testUCorners();
        testUEdges();
        testUInner();
        testUOuter();
    }

    private void testUCorners() {
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(0,20))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(20,20))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(20,10))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(40,10))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(40,20))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(60,20))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(60,0))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(40,0))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(20,0))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(0,0))));
    }

    private void testUEdges() {
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(10,20))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(20,15))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(30, 10))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(40, 15))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(50, 20))));
    }

    private void testUInner() {
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(10,10))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(30, 5))));
        assertTrue(this.u.contains(GF.createPoint(new Coordinate(50, 10))));
    }

    private void testUOuter() {
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(-10, -10))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(-10, 0))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(-10, 10))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(-10, 20))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(-10, 30))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(0, 30))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(10, 30))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(20, 30))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(30, 30))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(30, 20))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(30, 11))));
        assertFalse(this.u.contains(GF.createPoint(new Coordinate(40, 30))));
    }

    /*
     * |----|
     * |    |
     * |----|
     */
    private static PolygonContainsChecker createSquarePolygon() {
        final Coordinate[] coordinates = {new Coordinate(0, 0),
                                          new Coordinate(20, 0),
                                          new Coordinate(20, 20),
                                          new Coordinate(0, 20),
                                          new Coordinate(0, 0)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsChecker(polygon);
    }

    /*
     * \-----|
     *   --| |
     *   --| |
     *  /----|
     */
    private static PolygonContainsChecker createSquareHolePolygon() {
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
        return new PolygonContainsChecker(polygon);
    }

    /*
     * |----|
     * |    |
     * |----|
     */
    private static PolygonContainsChecker createSmallSquarePolygon() {
        final Coordinate[] coordinates = {new Coordinate(1, 1),
                                          new Coordinate(2, 1),
                                          new Coordinate(2, 2),
                                          new Coordinate(1, 2),
                                          new Coordinate(1, 1)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsChecker(polygon);
    }

    /*
     * |----|
     * | /\ |
     * |/  \|
     */
    private static PolygonContainsChecker createSmallSquareHolePolygon() {
        final Coordinate[] coordinates = {new Coordinate(1, 1),
                                          new Coordinate(2, 1),
                                          new Coordinate(2, 2),
                                          new Coordinate(1.5, 1.1),
                                          new Coordinate(1, 2),
                                          new Coordinate(1, 1)};
        final Polygon polygon = GF.createPolygon(coordinates);
        return new PolygonContainsChecker(polygon);
    }

    /*
     * |----|    |----|
     * |    |----|    |
     * |--------------|
     */
    private static PolygonContainsChecker createUShapedPolygon() {
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
        return new PolygonContainsChecker(polygon);
    }
}
