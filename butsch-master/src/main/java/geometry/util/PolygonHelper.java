package geometry.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonHelper {
    public static List<LineSegment> toLineSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final List<LineSegment> segments = new ArrayList<>(coordinates.length);

        for (int i = 0; i < coordinates.length - 1; i++) {
            final LineSegment newSegment = new LineSegment(coordinates[i], coordinates[i + 1]);
            segments.add(newSegment);
        }

        return segments;
    }
}
