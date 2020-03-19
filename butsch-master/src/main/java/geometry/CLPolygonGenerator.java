package geometry;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Arrays;

public class CLPolygonGenerator extends PolygonGenerator {

    public CLPolygonGenerator(final int numPoints) {
        super(numPoints);
    }

    @Override
    /**
     * Creates random coordinates from the same convex layers.
     */
    public Polygon createRandomSimplePolygon() {
        return null;
    }
}
