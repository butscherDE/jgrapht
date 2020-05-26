package routing.regionAware.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.stream.IntStream;

public class SimplerPolygonBuilder {
    private final ListIterator<LineSegment> segmentsIterator;

    public SimplerPolygonBuilder(final Polygon polygon, final Coordinate startCoordinate) {
        final CircularList<LineSegment> segments = getSegments(polygon);
        final int indexOfStartCoordinate = indexOf(polygon, startCoordinate);
        segmentsIterator = segments.listIterator(indexOfStartCoordinate);
    }

    private CircularList<LineSegment> getSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();

        final CircularList<LineSegment> segments = new CircularList<>(new ArrayList<>(coordinates.length - 1));
        for (int i = 0; i < coordinates.length - 1; i++) {
            segments.add(new LineSegment(coordinates[i], coordinates[i + 1]));
        }

        return segments;
    }

    public int indexOf(final Polygon polygon, final Coordinate startCoordinate) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final int index = IntStream
                .range(0, coordinates.length)
                .filter(i -> startCoordinate.equals(coordinates[i]))
                .findFirst()
                .orElse(-1);

        return index;
    }

    public List<LineSegment> removeForward() {
        segmentsIterator.next();
        return null;
    }


}
