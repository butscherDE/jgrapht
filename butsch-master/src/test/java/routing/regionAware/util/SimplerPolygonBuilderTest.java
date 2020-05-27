package routing.regionAware.util;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimplerPolygonBuilderTest {
    @Test
    public void enlargeForwardSize() {
        final Polygon polygon = createDefaultPolygon();

        final SimplerPolygonBuilder spb = new SimplerPolygonBuilder(polygon, createDefaultStartCoordinate());
        assertTrue(spb.isEnlargeable());
        spb.removeForward();
        assertTrue(spb.isEnlargeable());
        spb.removeForward();
        assertTrue(spb.isEnlargeable());
        spb.removeForward();
        assertTrue(spb.isEnlargeable());
        spb.removeForward();
        assertTrue(spb.isEnlargeable());
        spb.removeForward();
        assertFalse(spb.isEnlargeable());
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
}
