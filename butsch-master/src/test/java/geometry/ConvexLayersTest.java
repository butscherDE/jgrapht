package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Coordinates;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ConvexLayersTest {
    @Test
    public void testNestedSquares() {
        final ConvexLayers cl = getConvexLayers();

        final Geometry outerLayer = cl.layers[0];
        final Geometry innerLayer = cl.layers[1];
        final Coordinate[] outerCoordinates = outerLayer.getCoordinates();
        final Coordinate[] innerCoordinates = innerLayer.getCoordinates();

        final Coordinate[] expectedOuterCoordinates = getExpectedOuterCoordinates();
        final Coordinate[] expectedInnerCoordinates = getExpectedInnerCoordinates();
        assertArrayEquals(expectedOuterCoordinates, outerCoordinates);
        assertArrayEquals(expectedInnerCoordinates, innerCoordinates);
    }

    private ConvexLayers getConvexLayers() {
        final Coordinate[] coordinates = new Coordinate[] {new Coordinate(-1, -1),
                                                           new Coordinate(-1, 1),
                                                           new Coordinate(1,1),
                                                           new Coordinate(1, -1),
                                                           new Coordinate(-2,-2),
                                                           new Coordinate(-2, 2),
                                                           new Coordinate(2, 2),
                                                           new Coordinate(2, -2)};

        final Geometry geometry = new GeometryFactory().createMultiPointFromCoords(coordinates);
        return new ConvexLayers(geometry);
    }

    private Coordinate[] getExpectedInnerCoordinates() {
        final Coordinate[] expectedOuterCoordinates = new Coordinate[] {new Coordinate(-1,-1),
                                                                        new Coordinate(-1, 1),
                                                                        new Coordinate(1,1),
                                                                        new Coordinate(1, -1),
                                                                        new Coordinate(-1,-1)};
        return expectedOuterCoordinates;
    }

    private Coordinate[] getExpectedOuterCoordinates() {
        final Coordinate[] expectedInnerCoordinates = new Coordinate[] {new Coordinate(-2,-2),
                                                                        new Coordinate(-2, 2),
                                                                        new Coordinate(2, 2),
                                                                        new Coordinate(2, -2),
                                                                        new Coordinate(-2,-2)};
        return expectedInnerCoordinates;
    }
}
