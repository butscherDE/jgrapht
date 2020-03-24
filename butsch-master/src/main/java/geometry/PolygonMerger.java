package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class PolygonMerger {
    private final Coordinate[] outerCoordinates;
    private final List<Coordinate> innerCoordinates;

    private int m;
    private int i;
    private Coordinate[] mergedCoordinates;
    private int innerIterationStartIndex;
    private boolean chosenToInnerRingNotInversed;
    private boolean chosenToOuterRingInversed;

    public PolygonMerger(final Coordinate[] outerCoordinates, final CircularList<Coordinate> innerCoordinates) {
        if (!outerCoordinates[0].equals(outerCoordinates[outerCoordinates.length - 1])) {
            throw new IllegalArgumentException("Outer coordinates do not represent closed line string");
        }

        this.outerCoordinates = outerCoordinates;
        if (isFirstAndLastEqual(innerCoordinates) && isSizeLargerThan1(innerCoordinates)) {
            this.innerCoordinates = innerCoordinates.subList(0, innerCoordinates.size() - 1);
        } else {
            this.innerCoordinates = innerCoordinates;
        }
    }

    private boolean isFirstAndLastEqual(final CircularList<Coordinate> innerCoordinates) {
        return innerCoordinates.get(0).equals(innerCoordinates.get(innerCoordinates.size() - 1));
    }

    private boolean isSizeLargerThan1(final CircularList<Coordinate> innerCoordinates) {
        return innerCoordinates.size() > 1;
    }

    public PolygonMerger(final Coordinate[] outerCoordinates, final Coordinate[] innerCoordinates) {
        this(outerCoordinates, new CircularList<>(Arrays.asList(innerCoordinates)));
    }

    public PolygonMerger(final Polygon outerPolygon, final Polygon innerPolygon) {
        this(outerPolygon.getCoordinates(), innerPolygon.getCoordinates());
    }

    public Coordinate[] mergePolygons(final LineSegment outerChosen, final LineSegment innerChosen) {
        outerChosenOrientationCorrection(outerChosen);
        mergedCoordinates = new Coordinate[outerCoordinates.length + innerCoordinates.size()];

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

    private void outerChosenOrientationCorrection(final LineSegment outerChosen) {
        boolean foundForward = searchForward(outerChosen);

        if (!foundForward) {
            boolean foundReverse = searchAsReverse(outerChosen);

            handleNotFoundCase(foundReverse);
        }

        chosenToOuterRingInversed = !foundForward;
    }

    private boolean searchForward(final LineSegment outerChosen) {
        boolean foundForward = false;
        for (int k = 0; k < outerCoordinates.length - 1; k++) {
            final boolean basePointEqual = outerCoordinates[k].equals(outerChosen.p0);
            final boolean adjPointEqual = outerCoordinates[k + 1].equals(outerChosen.p1);
            foundForward |= basePointEqual && adjPointEqual;
        }
        return foundForward;
    }

    private boolean searchAsReverse(final LineSegment outerChosen) {
        outerChosen.reverse();
        return searchForward(outerChosen);
    }

    private void handleNotFoundCase(final boolean foundReverse) {
        if (!foundReverse) {
            throw new IllegalArgumentException("outerChosen is not on the outer polygon");
        }
    }

    private void processInnerPolygon(final LineSegment outerChosen, final LineSegment innerChosen) {
        if (innerCoordinates.size() > 1) {
            calcAndSetInnerStartIndex(innerChosen);
            processWhenInnerLargeEnoughToBeAPolygon(outerChosen, innerChosen);
        } else {
            processWhenInnerIsJustAPoint(outerChosen);
        }
    }

    private void processWhenInnerLargeEnoughToBeAPolygon(final LineSegment outerChosen, final LineSegment innerChosen) {
        addAllInnerCoordinates();
        reverseInnerInMergedPolygonIfNecessary(outerChosen, innerChosen);
    }

    private void processWhenInnerIsJustAPoint(final LineSegment outerChosen) {
        mergedCoordinates[m++] = innerCoordinates.get(0);
    }

    private boolean areNextTwoCoordsChosenByLineSegment(final LineSegment outerChosen) {
        return outerCoordinates[i].equals(outerChosen.p0) && outerCoordinates[i + 1].equals(outerChosen.p1);
    }

    private void addAllInnerCoordinates() {
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
        chosenToInnerRingNotInversed = nextCoordinateToInnerP0.equals(innerChosen.p1);
    }

    private int getStartIndexOnInnerSafely(final int indexOfInnerChosen) {
        final int startIndex = chosenToInnerRingNotInversed ? indexOfInnerChosen + 1 : indexOfInnerChosen;
        return startIndex % innerCoordinates.size();
    }

    private void reverseInnerInMergedPolygonIfNecessary(final LineSegment outerChosen, final LineSegment innerChosen) {
//        test(0);

        final boolean isIntersecting = isIntersectionProduced(outerChosen, innerChosen);
        final boolean b = !(!isIntersecting ^ chosenToInnerRingNotInversed);
        final boolean x = outerChosen.p0.distance(innerChosen.p0) > outerChosen.p0.distance(innerChosen.p1);
        System.out.println(chosenToOuterRingInversed);
        System.out.println(chosenToInnerRingNotInversed);
        System.out.println(isIntersecting);
        System.out.println(x);
//        System.out.println(b);
        checker.add(new Boolean[] {chosenToOuterRingInversed, chosenToInnerRingNotInversed,
                                   isIntersecting,
                                   x});
        if (truthtable(chosenToOuterRingInversed, chosenToInnerRingNotInversed, isIntersecting, x)) {
            System.out.println("inversing");
            ArrayUtils.reverse(this.mergedCoordinates, this.i + 1, m);
        }

//        test(100_000);
    }

    static List<Boolean[]> checker = new LinkedList<>();

    private boolean truthtable(final boolean outerInversed, final boolean innerInversed, final boolean isIntersecting, final boolean distanceP0P0Larger) {
        if (!outerInversed && !innerInversed && !isIntersecting && !distanceP0P0Larger) {
            return false;
        }
        if (!outerInversed && !innerInversed && !isIntersecting && distanceP0P0Larger) {
            throw new IllegalStateException("Not covered yet: " + outerInversed + ", " + innerInversed + ", " + isIntersecting + ", " + distanceP0P0Larger);
        }
        if (!outerInversed && !innerInversed && isIntersecting && !distanceP0P0Larger) {
            throw new IllegalStateException("Not covered yet: " + outerInversed + ", " + innerInversed + ", " + isIntersecting + ", " + distanceP0P0Larger);
        }
        if (!outerInversed && !innerInversed && isIntersecting && distanceP0P0Larger) {
            return true;
        }
        if (!outerInversed && innerInversed && !isIntersecting && !distanceP0P0Larger) {
            return true;
        }
        if (!outerInversed && innerInversed && !isIntersecting && distanceP0P0Larger) {
            return true;
        }
        if (!outerInversed && innerInversed && isIntersecting && !distanceP0P0Larger) {
            throw new IllegalStateException("Not covered yet: " + outerInversed + ", " + innerInversed + ", " + isIntersecting + ", " + distanceP0P0Larger);
        }
        if (!outerInversed && innerInversed && isIntersecting && distanceP0P0Larger) {
            return false;
        }
        if (outerInversed && !innerInversed && !isIntersecting && !distanceP0P0Larger) {
            return false;
        }
        if (outerInversed && !innerInversed && !isIntersecting && distanceP0P0Larger) {
            throw new IllegalStateException("Not covered yet: " + outerInversed + ", " + innerInversed + ", " + isIntersecting + ", " + distanceP0P0Larger);
        }
        if (outerInversed && !innerInversed && isIntersecting && !distanceP0P0Larger) {
            throw new IllegalStateException("Not covered yet: " + outerInversed + ", " + innerInversed + ", " + isIntersecting + ", " + distanceP0P0Larger);
        }
        if (outerInversed && !innerInversed && isIntersecting && distanceP0P0Larger) {
            return true;
        }
        if (outerInversed && innerInversed && !isIntersecting && !distanceP0P0Larger) {
            return true;
        }
        if (outerInversed && innerInversed && !isIntersecting && distanceP0P0Larger) {
            return false;
//            return true;
        }
        if (outerInversed && innerInversed && isIntersecting && !distanceP0P0Larger) {
            throw new IllegalStateException("Not covered yet: " + outerInversed + ", " + innerInversed + ", " + isIntersecting + ", " + distanceP0P0Larger);
        }
        if (outerInversed && innerInversed && isIntersecting && distanceP0P0Larger) {
            return false;
        }

        throw new IllegalStateException("cannot happen");
    }

    private void test(final int millis) {
        final Coordinate[] coordinatesFromOuter = ArrayUtils.subarray(mergedCoordinates, 0, i + 1);
        final Coordinate[] coordinatesFromInner = ArrayUtils.subarray(mergedCoordinates, i, m);
        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
//        final Coordinate[] mergedCoordinates = this.mergedCoordinates;
//        final int i = ArrayUtils.indexOf(mergedCoordinates, null);
//        final Coordinate[] coordsToDraw = ArrayUtils.subarray(mergedCoordinates, 0, i);
        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(coordinatesFromOuter));
        col.addLineSegmentsFromCoordinates(Color.RED, Arrays.asList(coordinatesFromInner));
        final GeometryVisualizer visualizer = new GeometryVisualizer(col);
        visualizer.visualizeGraph(millis);
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
