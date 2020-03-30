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
}
