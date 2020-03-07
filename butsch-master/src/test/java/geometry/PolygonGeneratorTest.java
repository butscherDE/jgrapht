package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

import static org.junit.Assert.assertFalse;

public class PolygonGeneratorTest {
    private Random random  = new Random(42);

    @Test
    public void testSimplePolygon() {
        final Coordinate[] coordinatesInNonSimpleOrder = new Coordinate[] {new Coordinate(0,0),
                                                                           new Coordinate(100, 100),
                                                                           new Coordinate(0, 100),
                                                                           new Coordinate(100,0),
                                                                           new Coordinate(0,0)};

        final Polygon simplifiedPolygon = new PolygonGenerator(10).createSimplePolygon(coordinatesInNonSimpleOrder);
        assertFalse(isSelfIntersecting(simplifiedPolygon));
    }


    @Test
    public void generateRandomPolygonsAndTestIfTheyAreReallySimple() {
        final PolygonGenerator polygonGenerator = new PolygonGenerator(getNumPoints(100));
        for (int i = 0; i < 1; i++) {
            final Polygon polygon = polygonGenerator.createRandomSimplePolygon();

            assertFalse(isSelfIntersecting(polygon));
        }
    }

    private int getNumPoints(final int numPoints) {
        if (numPoints < 3) {
            throw new IllegalArgumentException("Cannot create such a small number of points");
        }
        return random.nextInt(numPoints - 2) + 3;
    }

    private boolean isSelfIntersecting(final Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();

        for (int i = 0; i < coordinates.length - 2; i++) {
            for (int j = i + 1; j < coordinates.length - 2; j++) {
                LineSegment lineI = new LineSegment(coordinates[i], coordinates[i + 1]);
                LineSegment lineJ = new LineSegment(coordinates[j], coordinates[j + 1]);

                Coordinate intersection = lineI.intersection(lineJ);
                if (intersection != null && !intersection.equals(coordinates[i + 1]) && !intersection.equals(coordinates[j])) {
                    return true;
                }
            }
        }

        return false;
    }
}
