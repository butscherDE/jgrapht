package data;


import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    @Test
    void createNode() {
        final Node node = new Node(0, -1.2, 1.7, 5);

        assertEquals(-1.2, node.longitude);
        assertEquals(1.7, node.latitude);
        assertEquals(5, node.elevation);
    }

    @Test
    public void testEqual() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(0, 0.0, 1.0, 2);

        assertTrue(nodeA.equalPosition(nodeB));
    }

    @Test
    public void testUnequalLongitude() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(0, 1.0, 1.0, 2);

        assertFalse(nodeA.equalPosition(nodeB));
    }

    @Test
    public void testUnequalLatitude() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(0, 1.0, 2.0, 2);

        assertFalse(nodeA.equalPosition(nodeB));
    }

    @Test
    public void testUnequalElevation() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(0, 1.0, 1.0, 3);

        assertFalse(nodeA.equalPosition(nodeB));
    }

    @Test
    public void equals() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(0, 1.0, 2.0, 3);

        assertEquals(nodeA, nodeB);
    }

    @Test
    public void notEquals() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(1, 1.0, 2.0, 3);

        assertNotEquals(nodeA, nodeB);
    }

    @Test
    public void distance() {
        final Node nodeA = new Node(0, 0.0, 1.0, 2);
        final Node nodeB = new Node(1, 1.0, 1.0, 2);

        assertEquals(1d, nodeA.euclideanDistance(nodeB), 0);
    }

    @Test
    public void getPoint() {
        final Coordinate expectedCoordinate = new Coordinate(0, 1, 2);
        final Geometry expectedPoint = new GeometryFactory().createPoint(expectedCoordinate);
        final Node nodeA = new Node(0, 0.0, 1.0, 2);

        assertEquals(expectedPoint, nodeA.getPoint());
    }

    @Test
    public void compareSmaller1() {
        final Node nodeSmaller1 = new Node(0, 10, 10, 10);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(-1, nodeSmaller1.compareTo(compareNode));

    }

    @Test
    public void compareSmaller2() {
        final Node nodeSmaller2 = new Node(0, 20, 10, 10);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(-1, nodeSmaller2.compareTo(compareNode));
    }

    @Test
    public void compareSmaller3() {
        final Node nodeSmaller3 = new Node(0, 20, 20, 10);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(-1, nodeSmaller3.compareTo(compareNode));
    }

    @Test
    public void compareEqual() {
        final Node nodeEqual = new Node(0, 20, 20, 20);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(0, nodeEqual.compareTo(compareNode));
    }

    @Test
    public void compareGreater1() {
        final Node nodeGreater1 = new Node(0, 20, 20, 30);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(1, nodeGreater1.compareTo(compareNode));
    }

    @Test
    public void compareGreater2() {
        final Node nodeGreater2 = new Node(0, 20, 30, 30);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(1, nodeGreater2.compareTo(compareNode));
    }

    @Test
    public void compareGreater3() {
        final Node nodeGreater3 = new Node(0, 30, 30, 30);
        final Node compareNode = new Node(0, 20, 20, 20);

        assertEquals(1, nodeGreater3.compareTo(compareNode));
    }
}
