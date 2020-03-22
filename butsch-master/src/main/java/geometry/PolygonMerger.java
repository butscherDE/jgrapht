package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;

import java.util.*;

public class PolygonMerger {
    private final Coordinate[] outerCoordinates;
    private final Coordinate[] innerCoordinates;
    private Coordinate[] outerMergeLine;
    private Coordinate[] innerMergeLine;

    public PolygonMerger(final Coordinate[] outerCoordinates, final Coordinate[] innerCoordinates) {
        this.outerCoordinates = outerCoordinates;
        this.innerCoordinates = innerCoordinates;
    }

    public PolygonMerger(final Polygon outerPolygon, final Polygon innerPolygon) {
        outerCoordinates = outerPolygon.getCoordinates();
        innerCoordinates = ArrayUtils.subarray(innerPolygon.getCoordinates(), 0, innerPolygon.getNumPoints() - 1);
    }

    private List<Coordinate> mergePolygons(final LineSegment outerChosen, final LineSegment innerChosen) {
        final List<Coordinate> mergedPolygon = new ArrayList<>(outerPolygon.size() + innerPolygon.size() - 1);

        for (int i = 0; i < outerPolygon.size() - 1; i++) {
            final LineSegment lineSegment = new LineSegment(outerPolygon.get(i), outerPolygon.get(i + 1));
            mergedPolygon.add(lineSegment.p0);
            if (lineSegment.equals(outerChosen)) {
                innerPolygon.remove(innerPolygon.size() - 1);
                final int indexOfInnerChosen = innerPolygon.indexOf(innerChosen.p0);
                LinkedList<Coordinate> innerCoordinates = new LinkedList<>();
                ListIterator<Coordinate> innerCircularIterator = new CircularList<>(innerPolygon).listIterator(indexOfInnerChosen + 1);
                while (innerCircularIterator.hasNext()) {
                    final Coordinate next = innerCircularIterator.next();
                    innerCoordinates.add(next);
                }

                final Coordinate lastCordAlreadyMerged = mergedPolygon.get(mergedPolygon.size() - 1);
                final double distanceStart = lastCordAlreadyMerged.distance(innerCoordinates.getFirst());
                final double distanceEnd = lastCordAlreadyMerged.distance(innerCoordinates.getLast());
                if (distanceStart > distanceEnd) {
                    Collections.reverse(innerCoordinates);
                }

                mergedPolygon.addAll(innerCoordinates);
            }
        }
        mergedPolygon.add(mergedPolygon.get(0));

        return mergedPolygon;
    }
}
