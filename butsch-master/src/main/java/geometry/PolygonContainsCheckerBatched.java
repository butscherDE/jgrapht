package geometry;

import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        final List<Pair<Integer, Integer>> intersectionIndices = intersector.getIntersectionIndices();
        intersectionIndices.sort(Comparator.comparingInt(a -> a.getSecond()));

        final Boolean[] isIntersected = new Boolean[points.size()];
        IntStream.range(0, isIntersected.length).forEach(i -> isIntersected[i] = false);

        int currentPoint = intersectionIndices.get(0).getSecond();
        int currentPointIntersectionCount = 0;
        for (Pair<Integer, Integer> intersectionIndex : intersectionIndices) {
            final Integer nextPoint = intersectionIndex.getSecond();
            if (currentPoint == nextPoint) {
                currentPointIntersectionCount++;
            } else {
                isIntersected[currentPoint] = currentPointIntersectionCount % 2 == 1;
                currentPointIntersectionCount = 1;
                currentPoint = nextPoint;
            }
        }

        // TODO Tests failing

        return Arrays.asList(isIntersected);
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
