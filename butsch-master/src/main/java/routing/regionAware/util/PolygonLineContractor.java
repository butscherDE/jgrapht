package routing.regionAware.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

        contractForward(numForwards, restartedSPB);

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

    public Polygon getPolygon(final int forwardContractions, final int backwardContractions) {
        final PolygonLineContractor plc = new PolygonLineContractor(polygon, startCoordinateIndex);

        contractForward(forwardContractions, plc);
        contractBackward(backwardContractions, plc);

        return plc.toPolygon();
    }

    private void contractForward(int forwardContractions, PolygonLineContractor plc) {
        for (int i = 0; i < forwardContractions; i++) {
            plc.removeForward();
        }
    }

    private void contractBackward(int backwardContractions, PolygonLineContractor plc) {
        for (int i = 0; i < backwardContractions; i++) {
            plc.removeBackward();
        }
    }

    private Polygon toPolygon() {
        final Coordinate[] coordinates = new Coordinate[segments.size() + 1];

        final Iterator<LineSegment> segmentsIt = segments.iterator();
        for (int i = 0; i < segments.size(); i++) {
            coordinates[i] = segmentsIt.next().p0;
        }

        coordinates[coordinates.length - 1] = coordinates[0];
        return new GeometryFactory().createPolygon(coordinates);
    }

}
