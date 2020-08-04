package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PolygonContainsCheckerBatched {
    private final List<LineSegment> polygon;
    private final double outOfBoundsX;
    private final SweepShapeIntersectionFactory intersectionFactory = new SweepShapeIntersectionFactory();

    public PolygonContainsCheckerBatched(Polygon polygon) {
        this.polygon = toLineSegments(polygon);
        this.outOfBoundsX = BoundingBox.createFrom(polygon).maxLongitude + 1;
    }

    private List<LineSegment> toLineSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final List<LineSegment> segments = new ArrayList<>(coordinates.length - 1);

        for (int i = 0; i < coordinates.length - 1; i++) {
            final LineSegment newSegment = new LineSegment(coordinates[i], coordinates[i + 1]);
            segments.add(newSegment);
        }

        return segments;
    }

    public List<Boolean> contains(final List<Point> points) {
        final SegmentIntersectionAlgorithm intersector = intersectionFactory.createInstance(polygon, toLineSegments(points));
        final List<Coordinate> intersections = intersector.getIntersections();


    }

    private List<LineSegment> toLineSegments(final List<Point> points) {
        return points.stream()
                .map(p -> {
                    final Coordinate startCoordinate = p.getCoordinate();
                    final Coordinate endCoordinate = new Coordinate(outOfBoundsX, p.getY());
                    final LineSegment pRay = new LineSegment(startCoordinate, endCoordinate);
                    return pRay;
                })
                .collect(Collectors.toList());
    }
}
