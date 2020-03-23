package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PolygonMerger {
    private final Coordinate[] outerCoordinates;
    private final List<Coordinate> innerCoordinates;

    private int m;
    private int i;
    private Coordinate[] mergedCoordinates;
    private boolean chosenAndInnerRingInversed;
    private int innerIterationStartIndex;

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
        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addLineSegmentsFromCoordinates(Color.RED, Arrays.asList(outerCoordinates));
        col.addLineSegmentsFromCoordinates(Color.BLUE, innerCoordinates);
        col.addLineSegments(Color.GREEN, Arrays.asList(new LineSegment(outerChosen.p0, innerChosen.p0), new LineSegment(outerChosen.p1, innerChosen.p1)));
        final GeometryVisualizer vis = new GeometryVisualizer(col);
//        vis.visualizeGraph(100_000);


        mergedCoordinates = new Coordinate[outerCoordinates.length + innerCoordinates.size()];
        calcAndSetInnerStartIndex(innerChosen);

        m = 0;
        for (i = 0; i < outerCoordinates.length - 1; i++) {
            mergedCoordinates[m++] = outerCoordinates[i];
            if (areNextTwoCoordsChosenByLineSegment(outerChosen)) {
                processInnerPolygon(outerChosen, innerChosen);
            }
        }
        mergedCoordinates[m] = outerCoordinates[outerCoordinates.length - 1];

        return mergedCoordinates;
    }

    private void processInnerPolygon(final LineSegment outerChosen, final LineSegment innerChosen) {
        addAllInnerCoordinates(innerChosen);
        reverseInnerInMergedPolygonIfNecessary(outerChosen, innerChosen);
    }

    private boolean areNextTwoCoordsChosenByLineSegment(final LineSegment outerChosen) {
        return outerCoordinates[i].equals(outerChosen.p0) && outerCoordinates[i + 1].equals(outerChosen.p1);
    }

    private void addAllInnerCoordinates(final LineSegment innerChosen) {
        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(innerIterationStartIndex);
        while (innerCircularIterator.hasNext()) {
            final Coordinate next = innerCircularIterator.next();
            mergedCoordinates[m++] = next;
        }
    }

    private void calcAndSetInnerStartIndex(final LineSegment innerChosen) {
        final int indexOfInnerChosen = innerCoordinates.indexOf(innerChosen.p0);
        nextInnerEqualToChosenP1(innerChosen, indexOfInnerChosen);
        innerIterationStartIndex = getStartIndexOnInnerSafely(indexOfInnerChosen);
    }

    private void nextInnerEqualToChosenP1(final LineSegment innerChosen, final int indexOfInnerChosen) {
        final int indexOfNextToChosen = (indexOfInnerChosen + 1) % innerCoordinates.size();
        final Coordinate nextCoordinateToInnerP0 = innerCoordinates.get(indexOfNextToChosen);
        chosenAndInnerRingInversed = nextCoordinateToInnerP0.equals(innerChosen.p1);
    }

    private int getStartIndexOnInnerSafely(final int indexOfInnerChosen) {
        final int startIndex = chosenAndInnerRingInversed ? indexOfInnerChosen + 1 : indexOfInnerChosen;
        return startIndex % innerCoordinates.size();
    }

    private void reverseInnerInMergedPolygonIfNecessary(final LineSegment outerChosen, final LineSegment innerChosen) {
        final boolean isIntersecting = isIntersectionProduced(outerChosen, innerChosen);
        if (! (!isIntersecting ^ chosenAndInnerRingInversed)) {
            ArrayUtils.reverse(mergedCoordinates, i + 1, m);
        }
    }

    private boolean isIntersectionProduced(final LineSegment outerChosen, final LineSegment innerChosen) {
        final LineSegment p0ToP0 = new LineSegment(outerChosen.p0, innerChosen.p0);
        final LineSegment p1ToP1 = new LineSegment(outerChosen.p1, innerChosen.p1);
        return isIntersecting(p0ToP0, p1ToP1);
    }

    private boolean isIntersecting(final LineSegment p0ToP0, final LineSegment p1ToP1) {
        final Coordinate intersection = p0ToP0.intersection(p1ToP1);
        return intersection != null;
    }
}
