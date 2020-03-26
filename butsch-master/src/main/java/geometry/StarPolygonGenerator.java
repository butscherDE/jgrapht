package geometry;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.math.Vector2D;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class StarPolygonGenerator extends PolygonGenerator {
    final GeometryFactory geometryFactory = new GeometryFactory();

    public StarPolygonGenerator(final int numPoints, final Random random) {
        super(numPoints, random);
    }

    @Override
    public Polygon createRandomSimplePolygon() {
        final Coordinate[] randomCoordinates = createRandomCoordinates();

        final ConvexHull convexHull = new ConvexHull(randomCoordinates, geometryFactory);
        final Coordinate[] convexHullCoordinates = convexHull.getConvexHull().getCoordinates();
        final Coordinate randomCoordinateOnHull = convexHullCoordinates[random.nextInt(convexHullCoordinates.length)];

        final AngularPointSorter sorter = new AngularPointSorter(randomCoordinateOnHull);
        Arrays.sort(randomCoordinates, 0, numPoints, sorter);
        randomCoordinates[numPoints] = randomCoordinates[0];

        return geometryFactory.createPolygon(randomCoordinates);
    }

    private class AngularPointSorter implements Comparator {
        private final Coordinate centerCoordinate;
        private final Vector2D baseVector;

        public AngularPointSorter(final Coordinate centerCoordinate) {
            this.centerCoordinate = centerCoordinate;

            final Coordinate adjBaseLineCoordinate = new Coordinate(centerCoordinate.getX() + 1, centerCoordinate.getY());
            baseVector = new Vector2D(centerCoordinate, adjBaseLineCoordinate);
        }

        @Override
        public int compare(final Object o1, final Object o2) {
            final Coordinate coordinate1 = (Coordinate) o1;
            final Coordinate coordinate2 = (Coordinate) o2;

            final Vector2D vector1 = new Vector2D(centerCoordinate, coordinate1);
            final Vector2D vector2 = new Vector2D(centerCoordinate, coordinate2);

            final double angle1 = baseVector.angleTo(vector1);
            final double angle2 = baseVector.angleTo(vector2);

            return Double.compare(angle1, angle2);
        }
    }
}
