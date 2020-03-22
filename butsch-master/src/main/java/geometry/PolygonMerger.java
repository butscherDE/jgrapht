package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;

import java.util.*;

public class PolygonMerger {
    private final Coordinate[] outerCoordinates;
    private final List<Coordinate> innerCoordinates;
    private Coordinate[] outerMergeLine;
    private Coordinate[] innerMergeLine;

    public PolygonMerger(final Coordinate[] outerCoordinates, final CircularList<Coordinate> innerCoordinates) {
        this.outerCoordinates = outerCoordinates;
        if (innerCoordinates.get(0).equals(innerCoordinates.get(innerCoordinates.size() - 1))) {
            this.innerCoordinates = innerCoordinates.subList(0, innerCoordinates.size() - 1);
        } else {
            this.innerCoordinates = innerCoordinates;
        }
    }

    public PolygonMerger(final Coordinate[] outerCoordinates, final Coordinate[] innerCoordinates) {
        this(outerCoordinates, new CircularList<>(Arrays.asList(innerCoordinates)));
    }

    public PolygonMerger(final Polygon outerPolygon, final Polygon innerPolygon) {
        this(outerPolygon.getCoordinates(), innerPolygon.getCoordinates());
    }

    public Coordinate[] mergePolygons(final LineSegment outerChosen, final LineSegment innerChosen) {
        final Coordinate[] mergedCoordinates = new Coordinate[outerCoordinates.length + innerCoordinates.size()];

        int m = 0;
        for (int i = 0; i < outerCoordinates.length - 1; i++) {
            mergedCoordinates[m++] = outerCoordinates[i];
            if (outerCoordinates[i].equals(outerChosen.p0) && outerCoordinates[i + 1].equals(outerChosen.p1)) {
                final int indexOfInnerChosen = innerCoordinates.indexOf(innerChosen.p0);
                final int innerStartIndex = indexOfInnerChosen + 1;
                final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(innerStartIndex);
                while (innerCircularIterator.hasNext()) {
                    final Coordinate next = innerCircularIterator.next();
                    mergedCoordinates[m++] = next;
                }

                final Coordinate lastCordAlreadyMerged = outerCoordinates[i];
                final double distanceStart = lastCordAlreadyMerged.distance(mergedCoordinates[i + 1]);
                final double distanceEnd = lastCordAlreadyMerged.distance(mergedCoordinates[m - 1]);
                if (distanceStart > distanceEnd) {
                    ArrayUtils.reverse(mergedCoordinates, i + 1, m);
                }
            }
        }
        mergedCoordinates[m] = outerCoordinates[outerCoordinates.length - 1];

        return mergedCoordinates;
    }
}
