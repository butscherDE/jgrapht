package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ConvexLayersTest {
    @Test
    public void testNestedSquares() {
        final ConvexLayers cl = getConvexLayersNoDuplicates();

        final Geometry outerLayer = cl.layers[0];
        final Geometry innerLayer = cl.layers[1];
        final Coordinate[] outerCoordinates = outerLayer.getCoordinates();
        final Coordinate[] innerCoordinates = innerLayer.getCoordinates();

        final Coordinate[] expectedOuterCoordinates = getExpectedOuterCoordinates();
        final Coordinate[] expectedInnerCoordinates = getExpectedInnerCoordinates();
        assertArrayEquals(expectedOuterCoordinates, outerCoordinates);
        assertArrayEquals(expectedInnerCoordinates, innerCoordinates);
    }

    private ConvexLayers getConvexLayersNoDuplicates() {
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
        return new Coordinate[] {new Coordinate(-1, -1),
                                 new Coordinate(-1, 1),
                                 new Coordinate(1,1),
                                 new Coordinate(1, -1),
                                 new Coordinate(-1,-1)};
    }

    private Coordinate[] getExpectedOuterCoordinates() {
        return new Coordinate[] {new Coordinate(-2, -2),
                                 new Coordinate(-2, 2),
                                 new Coordinate(2, 2),
                                 new Coordinate(2, -2),
                                 new Coordinate(-2,-2)};
    }

    @Test
    public void testNestedSquaresDuplicates() {
        final ConvexLayers cl = getConvexLayersDuplicateEndPoints();

        final Geometry outerLayer = cl.layers[0];
        final Geometry innerLayer = cl.layers[1];
        final Coordinate[] outerCoordinates = outerLayer.getCoordinates();
        final Coordinate[] innerCoordinates = innerLayer.getCoordinates();

        final Coordinate[] expectedOuterCoordinates = getExpectedOuterCoordinates();
        final Coordinate[] expectedInnerCoordinates = getExpectedInnerCoordinates();
        assertArrayEquals(expectedOuterCoordinates, outerCoordinates);
        assertArrayEquals(expectedInnerCoordinates, innerCoordinates);
    }

    private ConvexLayers getConvexLayersDuplicateEndPoints() {
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
}
