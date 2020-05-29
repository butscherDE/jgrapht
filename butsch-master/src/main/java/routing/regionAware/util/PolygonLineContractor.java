package routing.regionAware.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PolygonLineContractor {
    private final Polygon polygon;
    private final int startCoordinateIndex;
    private final CircularList<LineSegment> segments;
    private final ListIterator<LineSegment> segmentsIterator;

    public PolygonLineContractor(final Polygon polygon, final int startCoordinateIndex) {
        this.polygon = polygon;
        this.startCoordinateIndex = startCoordinateIndex;
        this.segments = getSegments(this.polygon);
        segmentsIterator = segments.listIterator(startCoordinateIndex);
        segmentsIterator.next();
    }

    public PolygonLineContractor restartAt(final int numForwards) {
        final PolygonLineContractor restartedSPB = new PolygonLineContractor(polygon, startCoordinateIndex);

        for (int i = 0; i < numForwards; i++) {
            restartedSPB.removeForward();
        }

        return restartedSPB;
    }

    private static CircularList<LineSegment> getSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();

        final CircularList<LineSegment> segments = new CircularList<>(new ArrayList<>(coordinates.length - 1));
        for (int i = 0; i < coordinates.length - 1; i++) {
            segments.add(new LineSegment(coordinates[i], coordinates[i + 1]));
        }

        return segments;
    }

    public CircularList<LineSegment> removeForward() {
        failOnToSmallPolygon();
        reduceForward();

        return segments;
    }

    private void reduceForward() {
        final LineSegment followingSegment = segmentsIterator.next();
        segmentsIterator.remove();
        segmentsIterator.next();
        final LineSegment segmentToEnlarge = segmentsIterator.previous();

        final LineSegment enlargedSegment = new LineSegment(segmentToEnlarge.p0, followingSegment.p1);
        segmentsIterator.set(enlargedSegment);
    }

    public CircularList<LineSegment> removeBackward() {
        failOnToSmallPolygon();
        reduceBackward();

        return segments;
    }

    private void reduceBackward() {
        final LineSegment previousSegment = segmentsIterator.previous();
        segmentsIterator.remove();
        segmentsIterator.previous();
        final LineSegment segmentToEnlarge = segmentsIterator.next();

        final LineSegment enlargedSegment = new LineSegment(previousSegment.p0, segmentToEnlarge.p1);
        segmentsIterator.set(enlargedSegment);
    }

    private void failOnToSmallPolygon() {
        if (!isReducable()) {
            throw new IllegalStateException("Polygon cannot be further reduces");
        }
    }

    public boolean isReducable() {
        return segments.size() > 3;
    }


}
