package data;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.*;

public class RegionOfInterest implements PolygonSegmentCollection {
    private final Polygon polygon;
    private final List<LineSegment> segments;

    public RegionOfInterest(final Polygon polygon) {
        this.polygon = polygon;
        this.segments = getSortedSegments(polygon);
    }

    public RegionOfInterest(final List<Node> nodes) {
        this(toPolygon(nodes));
    }

    private static Polygon toPolygon(final List<Node> nodes) {
        final Coordinate[] coordinates = new Coordinate[nodes.size() + 1];
        final Iterator<Node> nodesIt = nodes.iterator();
        for (int i = 0; i < nodes.size(); i++) {
            final Node next = nodesIt.next();
            coordinates[i] = next.getPoint().getCoordinate();
        }
        coordinates[coordinates.length - 1] = coordinates[0];

        return new GeometryFactory().createPolygon(coordinates);
    }

    public List<LineSegment> getSortedSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final List<LineSegment> segments = new ArrayList<>(coordinates.length);

        for (int i = 0; i < coordinates.length - 1; i++) {
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
        return segments;
    }

    @Override
    public Polygon getPolygon() {
        return polygon;
    }
}
