package data;

import org.jgrapht.util.VisitedManager;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

public class VisibilityCell {
    public final List<LineSegment> lineSegments;

    private VisibilityCell(final List<LineSegment> lineSegments) {
        this.lineSegments = lineSegments;
    }

    public static VisibilityCell create(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        return create(coordinates);
    }

    public static VisibilityCell create(final Coordinate[] coordinates) {
        final List<LineSegment> lineSegments = new ArrayList<>(coordinates.length - 1);

        for (int i = 0; i < coordinates.length - 1; i++) {
            lineSegments.add(new LineSegment(coordinates[i], coordinates[i + 1]));
        }

        return new VisibilityCell(lineSegments);
    }
}
