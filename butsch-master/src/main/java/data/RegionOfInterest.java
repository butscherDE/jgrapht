package data;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegionOfInterest implements PolygonSegmentCollection {
    private final Polygon polygon;
    private final List<LineSegment> segments;

    public RegionOfInterest(final Polygon polygon) {
        this.polygon = polygon;
        this.segments = getSortedSegments(polygon);
    }

    public List<LineSegment> getSortedSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final List<LineSegment> segments = new ArrayList<>(coordinates.length);

        for (int i = 0; i < coordinates.length; i++) {
            final Coordinate start = coordinates[i];
            final Coordinate end = coordinates[i+1];

            if (start.x <= end.x) {
                segments.add(new LineSegment(start, end));
            } else {
                segments.add(new LineSegment(end, start));
            }
        }

        Collections.sort(segments, Comparator.comparingDouble(a -> a.p1.x));

        return segments;
    }

    @Override
    public List<LineSegment> getSortedLineSegments() {
        return null;
    }

    @Override
    public Polygon getPolygon() {
        return null;
    }
}
