package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.*;

public class PolygonContainsChecker {
    final List<LineSegment> polygon;
    final BoundingBox boundingBox;

    public PolygonContainsChecker(final Polygon polygon) {
        this.polygon = getSortedLineSegments(polygon);
        this.boundingBox = BoundingBox.createFrom(polygon);
    }

    private List<LineSegment> getSortedLineSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();

        final List<LineSegment> lineSegments = createLineSegments(coordinates);
        Collections.sort(lineSegments, Comparator.comparingDouble(a -> a.getCoordinate(1).x));

        return lineSegments;
    }

    private List<LineSegment> createLineSegments(final Coordinate[] coordinates) {
        final List<LineSegment> lineSegments = new ArrayList<>(coordinates.length - 1);
        for (int i = 0; i < coordinates.length - 1; i++) {
            final LineSegment lineSegment = createLineSegment(coordinates, i);
            lineSegments.add(lineSegment);
        }
        return lineSegments;
    }

    private LineSegment createLineSegment(final Coordinate[] coordinates, final int i) {
        final Coordinate startCoordinate = coordinates[i];
        final Coordinate endCoordinate = coordinates[i + 1];

        if (startCoordinate.x < endCoordinate.x) {
            return createLineSegmentForward(startCoordinate, endCoordinate);
        } else {
            return createLineSegmentBackward(startCoordinate, endCoordinate);
        }
    }

    private LineSegment createLineSegmentForward(final Coordinate startCoordinate, final Coordinate endCoordinate) {
        return new LineSegment(startCoordinate, endCoordinate);
    }

    private LineSegment createLineSegmentBackward(final Coordinate startCoordinate, final Coordinate endCoordinate) {
        return new LineSegment(endCoordinate, startCoordinate);
    }

    public boolean contains(final Point point) {
        if (!boundingBox.contains(point)) {
            return false;
        } else {
            return contains(point.getCoordinate());
        }
    }

    private boolean contains(final Coordinate coordinate) {
        final int startIndex = getLeftMostPossiblyIntersectingLineSegment(coordinate);
        if (startIndex >= polygon.size()) {
            return false;
        } else {
            final int intersectionCount = getIntersectionCount(coordinate, startIndex);
            final boolean containedByRayCast = intersectionCount % 2 == 1;

            return containedByRayCast;
        }
    }

    private LineSegment getRay(final Coordinate coordinate) {
        return new LineSegment(coordinate, new Coordinate(boundingBox.maxLongitude + 1, coordinate.y + 1E-10));
    }

    private int getLeftMostPossiblyIntersectingLineSegment(final Coordinate coordinate) {
        final LineSegment searchKey = new LineSegment(coordinate, coordinate);
        final Comparator<LineSegment> p1XComparator = Comparator.comparingDouble(a -> a.getCoordinate(1).x);
        final int index = Collections.binarySearch(polygon, searchKey, p1XComparator);
        final int nonNegativeIndex = index < 0 ? (index + 1) * (-1) : index;

        final int spooledIndex = spoolBackToReallyFindFirstOccurence(searchKey, nonNegativeIndex);

        return spooledIndex;
    }

    private int spoolBackToReallyFindFirstOccurence(LineSegment searchKey, int nonNegativeIndex) {
        final ListIterator<LineSegment> polygonListIterator = polygon.listIterator(nonNegativeIndex);
        while (polygonListIterator.hasPrevious() && polygonListIterator.previous().p1.x == searchKey.p1.x) {
            nonNegativeIndex--;
        }
        return nonNegativeIndex;
    }

    private int getIntersectionCount(final Coordinate coordinate, final int startIndex) {
        final LineSegment ray = getRay(coordinate);
        int intersectionCount = 0;
        final ListIterator<LineSegment> lineSegments = polygon.listIterator(startIndex);
        while (lineSegments.hasNext()) {
            final LineSegment lineSegment = lineSegments.next();

            if (isPointOnPolygonBorder(coordinate, lineSegment)) {
                return 1;
            }

            intersectionCount = countIntersection(ray, intersectionCount, lineSegment);
        }

        return intersectionCount;
    }

    private boolean isPointOnPolygonBorder(final Coordinate coordinate, final LineSegment lineSegment) {
        return lineSegment.distance(coordinate) == 0;
    }

    private int countIntersection(final LineSegment ray, int intersectionCount,
                                 final LineSegment lineSegment) {
        final Coordinate intersection = ray.intersection(lineSegment);
        final boolean intersecting = isIntersecting(intersection);

        intersectionCount = intersecting ? intersectionCount + 1 : intersectionCount;
        return intersectionCount;
    }

    private boolean isIntersecting(final Coordinate intersection) {
        return intersection != null;
    }
}
