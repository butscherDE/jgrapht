package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public class PolygonGeneratorTest {
    private final Random random = new Random(42);

    static boolean isSelfIntersecting(final Polygon polygon) {
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

    int getNumPoints(final int numPoints) {
        if (numPoints < 3) {
            throw new IllegalArgumentException("Cannot create such a small number of points");
        }
        return random.nextInt(numPoints - 2) + 3;
    }
}
