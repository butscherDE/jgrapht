package geometry;

import evalutation.Config;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public class TwoOptPolygonGenerator extends PolygonGenerator {
    private int numGenerated = 0;
    private final String path;

    public TwoOptPolygonGenerator(final int numPoints, final Random random, final String path) {
        super(numPoints, random);
        this.path = path;
    }

    public TwoOptPolygonGenerator(final int numPoints, final Random random) {
        this(numPoints, random, null);
    }

    public Polygon createRandomSimplePolygon() {
        final Coordinate[] coordinates = createRandomCoordinates();

        Polygon polygon = createSimplePolygon(coordinates);

        numGenerated++;
        return polygon;
    }

    public Polygon createSimplePolygon(final Coordinate[] coordinates) {
        Polygon polygon = createPolygon(coordinates);

        int tries = 0;
        while (!polygon.isSimple()) {
            saveCurrentPolygon(polygon, tries++);
            linearIntersectionSweep(coordinates);
            polygon = createPolygon(coordinates);
        }

        saveCurrentPolygon(polygon, tries);
        return polygon;
    }

    public static void linearIntersectionSweep(final Coordinate[] coordinates) {
        for (int i = 0; i < coordinates.length - 2; i++) {
            for (int j = i + 1; j < coordinates.length - 1; j++) {
                final Coordinate a = coordinates[i];
                final Coordinate b = coordinates[i + 1];
                final Coordinate c = coordinates[j];
                final Coordinate d = coordinates[j + 1];

                final LineSegment ab = new LineSegment(a, b);
                final LineSegment cd = new LineSegment(c, d);

                Coordinate intersection = ab.intersection(cd);
                if ((intersection != null && !intersection.equals(b) && !intersection.equals(c) && !intersection.equals(a))) {
                    coordinates[i] = a;
                    coordinates[i + 1] = c;
                    coordinates[j] = b;
                    coordinates[j + 1] = d;
                }
            }
        }
    }

    private static Polygon createPolygon(final Coordinate[] coordinates) {
        final GeometryFactory geometryFactory = new GeometryFactory();

        return geometryFactory.createPolygon(coordinates);
    }

    private void saveCurrentPolygon(final Polygon polygon, final int x) {
        if (path != null) {
            ShapeDrawer shapeDrawer = new ShapeDrawer(ShapeDrawer.reshapePolygon(polygon, 2000));
            shapeDrawer.save(Config.IMG_PATH, "Polygon" + numGenerated + "_" + "try" + x);
        }
    }
}
