package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public abstract class PolygonGenerator {
    int numPoints;
    final Random random;

    public PolygonGenerator(final int numPoints, final Random random) {
        this.random = random;
        if (numPoints < 3) {
            throw new IllegalArgumentException("Cannot create polygons with less than 3 points.");
        }
        this.numPoints = numPoints;
    }

    public abstract Polygon createRandomSimplePolygon();

    final Coordinate[] createRandomCoordinates() {
        final Coordinate[] coordinates = new Coordinate[numPoints + 1];

        fillWithCoordinates(coordinates);
        closeCycle(coordinates);

        return coordinates;
    }

    private void fillWithCoordinates(final Coordinate[] coordinates) {
        for (int i = 0; i < numPoints; i++) {
            coordinates[i] = createRandomCoordinate();
        }
    }

    private Coordinate createRandomCoordinate() {
        return new Coordinate(random.nextDouble(), random.nextDouble());
    }

    private void closeCycle(final Coordinate[] coordinates) {
        coordinates[numPoints] = coordinates[0];
    }

}
