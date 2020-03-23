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

    private int m;
    private int i;
    private Coordinate[] mergedCoordinates;

    public PolygonMerger(final Coordinate[] outerCoordinates, final CircularList<Coordinate> innerCoordinates) {
        if (!outerCoordinates[0].equals(outerCoordinates[outerCoordinates.length - 1])) {
            throw new IllegalArgumentException("Outer coordinates do not represent closed line string");
        }

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
        mergedCoordinates = new Coordinate[outerCoordinates.length + innerCoordinates.size()];

        m = 0;
        for (i = 0; i < outerCoordinates.length - 1; i++) {
            mergedCoordinates[m++] = outerCoordinates[i];
            if (areNextTwoCoordsChosenByLineSegment(outerChosen)) {
                processInnerPolygon(innerChosen);
            }
        }
        mergedCoordinates[m] = outerCoordinates[outerCoordinates.length - 1];

        return mergedCoordinates;
    }

    private void processInnerPolygon(final LineSegment innerChosen) {
        addAllInnerCoordinates(innerChosen);
        reverseInnerInMergedPolygonIfNecessary();
    }

    private boolean areNextTwoCoordsChosenByLineSegment(final LineSegment outerChosen) {
        return outerCoordinates[i].equals(outerChosen.p0) && outerCoordinates[i + 1].equals(outerChosen.p1);
    }

    private void addAllInnerCoordinates(final LineSegment innerChosen) {
        final int innerStartIndex = getInnerStartIndex(innerChosen);
        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(innerStartIndex);
        while (innerCircularIterator.hasNext()) {
            final Coordinate next = innerCircularIterator.next();
            mergedCoordinates[m++] = next;
        }
    }

    private int getInnerStartIndex(final LineSegment innerChosen) {
        final int indexOfInnerChosen = innerCoordinates.indexOf(innerChosen.p0);
        final Coordinate nextCoordinateToInnerP0 = innerCoordinates.get(indexOfInnerChosen + 1);
        final boolean nextEqualToInnerChosenP1 = nextCoordinateToInnerP0.equals(innerChosen.p1);
        return nextEqualToInnerChosenP1 ? indexOfInnerChosen + 1 : indexOfInnerChosen;
    }

    private void reverseInnerInMergedPolygonIfNecessary() {
        final Coordinate lastCordFromOuterPolygon = outerCoordinates[i];
        final double distanceStart = lastCordFromOuterPolygon.distance(mergedCoordinates[i + 1]);
        final double distanceEnd = lastCordFromOuterPolygon.distance(mergedCoordinates[m - 1]);
        if (distanceStart > distanceEnd) {
            ArrayUtils.reverse(mergedCoordinates, i + 1, m);
            System.out.println("reversed");
        }
    }
}
