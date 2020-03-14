package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public abstract class PolygonGenerator {
    int numPoints;
    final Random random = new Random(42);

    public PolygonGenerator(final int numPoints) {
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
