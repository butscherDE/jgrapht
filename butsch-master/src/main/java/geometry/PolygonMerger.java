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
        addAllInnerCoordinates(outerChosen, innerChosen);
//        reverseInnerInMergedPolygonIfNecessary(outerChosen, innerChosen);
    }

    private void processWhenInnerIsJustAPoint(final LineSegment outerChosen) {
        mergedCoordinates[m++] = innerCoordinates.get(0);
    }

    private boolean areNextTwoCoordsChosenByLineSegment(final LineSegment outerChosen) {
        return outerCoordinates[i].equals(outerChosen.p0) && outerCoordinates[i + 1].equals(outerChosen.p1);
    }

    private void addAllInnerCoordinates(final LineSegment outerChosen, final LineSegment innerChosen) {
        final List<LineSegment> innerLayerAsLineSegments = ConvexLayers.getLineSegments(innerCoordinates);
        final LineSegment[] endVisibilityBridgeLines = getEndVisibilityBridgeLines(outerChosen, innerChosen,
                                                                                   innerLayerAsLineSegments);
        final Coordinate innerEntryPoint = endVisibilityBridgeLines[0].p1;
        final Coordinate innerExitPoint = endVisibilityBridgeLines[1].p1;
        System.out.println("innerEntryPoint: " + innerEntryPoint);
        System.out.println("innerExitPoint: " + innerExitPoint);


        int newInnerIterationStartIndex = innerCoordinates.indexOf(innerEntryPoint);
        int newInnerIterationExitIndex = innerCoordinates.indexOf(innerExitPoint);
        System.out.println("newInnerIterationStartIndex: " + newInnerIterationStartIndex);
        System.out.println("newInnerIterationExitIndex: " + newInnerIterationExitIndex);
        if (getIndexDistance(newInnerIterationExitIndex, newInnerIterationStartIndex, innerCoordinates.size()) == 1) {
            Collections.reverse(innerCoordinates);
            newInnerIterationStartIndex = innerCoordinates.indexOf(innerEntryPoint);
            newInnerIterationExitIndex = innerCoordinates.indexOf(innerExitPoint);
            System.out.println("newInnerIterationStartIndex2: " + newInnerIterationStartIndex);
            System.out.println("newInnerIterationExitIndex2: " + newInnerIterationExitIndex);

            if (getIndexDistance(newInnerIterationExitIndex, newInnerIterationStartIndex, innerCoordinates.size()) != -1) {
                throw new IllegalStateException("something is very wrong");
            }
        } else if (getIndexDistance(newInnerIterationExitIndex, newInnerIterationStartIndex, innerCoordinates.size()) == -1) {
            // Alles gut
        } else {
            System.out.println(newInnerIterationStartIndex);
            System.out.println(newInnerIterationExitIndex);
            final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
            col.addLineSegmentsFromCoordinates(Color.GRAY, innerCoordinates);
            col.addLineSegment(Color.RED, endVisibilityBridgeLines[0]);
            col.addLineSegment(Color.BLUE, endVisibilityBridgeLines[1]);
            col.addCoordinate(Color.RED, endVisibilityBridgeLines[0].p1);
            col.addCoordinate(Color.BLUE, endVisibilityBridgeLines[1].p1);
            final GeometryVisualizer vis = new GeometryVisualizer(col);
//            vis.visualizeGraph(100_000);
            throw new IllegalStateException("innerChosen not consecutive on inner Polygon");
        }

        System.out.println("Start element from collection: " + innerCoordinates.get(newInnerIterationStartIndex));
        System.out.println("Exit element from collection: " + innerCoordinates.get(newInnerIterationExitIndex));

//        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(innerIterationStartIndex);
        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(newInnerIterationStartIndex);
        while (innerCircularIterator.hasNext()) {
            final Coordinate next = innerCircularIterator.next();
            System.out.println(next);
            mergedCoordinates[m++] = next;
        }
    }

    public int getIndexDistance(final int index1, final int index2, final int sizeOfCollection) {
        final int difference = index1 - index2;
        final int maxIndexOfCollection = sizeOfCollection - 1;
        final int negativeMaxIndexOfCollection = maxIndexOfCollection * -1;
        if (difference == maxIndexOfCollection) {
            return -1;
        } else if (difference == negativeMaxIndexOfCollection) {
            return 1;
        } else {
            return difference;
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
        try {
//            if (truthtable(chosenToOuterRingInversed, chosenToInnerRingNotInversed, isIntersecting, x)) {
//            if (isIntersecting || (!isIntersecting && !isInverseIntersectionProduced() && isInverseShorter())) {
            if (isIntersecting) {
//                System.out.println("inversing");
                ArrayUtils.reverse(this.mergedCoordinates, this.i + 1, m);
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(outerCoordinates));
            System.out.println(innerCoordinates.toString());
//            test(100_000);
            System.exit(-1);
        }
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
            return false;
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
//            return true;
            return false;
        }
        if (outerInversed && innerInversed && !isIntersecting && distanceP0P0Larger) {
            return false;
        }
        if (outerInversed && innerInversed && isIntersecting && !distanceP0P0Larger) {
            return false;
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

//    private boolean isIntersectionProduced(final LineSegment outerChosen, final LineSegment innerChosen) {
//        final LineSegment p0ToP0 = new LineSegment(outerChosen.p0, innerChosen.p0);
//        final LineSegment p1ToP1 = new LineSegment(outerChosen.p1, innerChosen.p1);
//        return isIntersecting(p0ToP0, p1ToP1);
//    }

    private boolean isIntersectionProduced(final LineSegment outerChosen, final LineSegment innerChosen) {
        final LineSegment into = new LineSegment(mergedCoordinates[i], mergedCoordinates[i+1]);
        final LineSegment outOf = new LineSegment(mergedCoordinates[m - 1], outerCoordinates[i +1]);

//        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
//        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(outerCoordinates));
//        col.addLineSegmentsFromCoordinates(Color.GRAY, innerCoordinates);
//        col.addLineSegment(Color.RED, into);
//        col.addLineSegment(Color.BLUE, outOf);
//        col.addLineSegment(Color.ORANGE, outerChosen);
//        col.addLineSegment(Color.CYAN, innerChosen);
//        final GeometryVisualizer vis = new GeometryVisualizer(col);
//        vis.visualizeGraph(0);

        return into.intersection(outOf) != null;
    }

    private boolean isInverseIntersectionProduced() {
        final LineSegment into = new LineSegment(mergedCoordinates[i], outerCoordinates[i +1]);
        final LineSegment outOf = new LineSegment(mergedCoordinates[m - 1], mergedCoordinates[i+1]);

        return into.intersection(outOf) != null;
    }

    private boolean isInverseShorter() {
        final LineSegment into = new LineSegment(mergedCoordinates[i], mergedCoordinates[i+1]);
        final LineSegment outOf = new LineSegment(mergedCoordinates[m - 1], outerCoordinates[i +1]);

        final LineSegment intoInversed = new LineSegment(mergedCoordinates[i], outerCoordinates[i +1]);
        final LineSegment outOfInversed = new LineSegment(mergedCoordinates[m - 1], mergedCoordinates[i+1]);

        return (intoInversed.getLength() +  outOfInversed.getLength()) < (into.getLength() + outOf.getLength());
    }


    private LineSegment[] getEndVisibilityBridgeLines(final LineSegment outerChosen, final LineSegment innerChosen,
                                                                  final List<LineSegment> innerLayerAsLineSegments) {
        final LineSegment[] endVisibilityCheckLines = getEndVisibilityCheckLines(outerChosen, innerChosen);
        final boolean[] isNotIntersected = new boolean[4];

        for (int i = 0; i < isNotIntersected.length; i++) {
            final LineSegment endVisibilityCheckLine = endVisibilityCheckLines[i];
            isNotIntersected[i] = true;
            for (final LineSegment possibleSightBlockingLine : innerLayerAsLineSegments) {
                final boolean isIntersecting = isIntersectionProduced(possibleSightBlockingLine, endVisibilityCheckLine,
                                                                      innerChosen);
                isNotIntersected[i] &= !isIntersecting;
            }
        }

        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(outerCoordinates));
        col.addLineSegments(Color.GRAY, innerLayerAsLineSegments);
        final Color[] colors = new Color[] {Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE};
        for (int i = 0; i < endVisibilityCheckLines.length; i++) {
            col.addLineSegment(colors[i], endVisibilityCheckLines[i]);
        }
        col.addCoordinate(Color.BLUE, new Coordinate(0.5, 0.5));
//        col.addLineSegment(Color.CYAN, outerChosen);
        col.addCoordinate(Color.CYAN, outerChosen.p0);
        col.addCoordinate(Color.CYAN, outerChosen.p1);
//        col.addLineSegment(Color.ORANGE, innerChosen);
        col.addCoordinate(Color.ORANGE, innerChosen.p0);
        col.addCoordinate(Color.ORANGE, innerChosen.p1);
        final GeometryVisualizer vis = new GeometryVisualizer(col);
//        vis.visualizeGraph();


        if (isNotIntersected[0] && isNotIntersected[3] && !isIntersecting(endVisibilityCheckLines[0], endVisibilityCheckLines[3])) {
            System.out.println("yellow-blue: " + endVisibilityCheckLines[0] + ", " + endVisibilityCheckLines[3]);
            return new LineSegment[] {endVisibilityCheckLines[0], endVisibilityCheckLines[3]};
        } else if (isNotIntersected[1] && isNotIntersected[2] && !isIntersecting(endVisibilityCheckLines[1], endVisibilityCheckLines[2])) {
            System.out.println("green-red " + endVisibilityCheckLines[1] + ", " + endVisibilityCheckLines[2]);
            return new LineSegment[] {endVisibilityCheckLines[1], endVisibilityCheckLines[2]};
        } else {
            throw new IllegalStateException("This shall never be happening");
        }
    }

    private boolean isIntersectionProduced(final LineSegment possiblySightBlockingLine, final LineSegment checkLine,
                                           final LineSegment innerLineThatShallNotBeIntersected) {
        final Coordinate intersection = possiblySightBlockingLine.intersection(checkLine);
        if (intersection != null) {
            return !isIntersectionOnLineThatShouldntBeIntersected(innerLineThatShallNotBeIntersected, intersection);
        } else {
            return false;
        }
    }

    private boolean isIntersectionOnLineThatShouldntBeIntersected(final LineSegment innerLineThatShallNotBeIntersected,
                                                                  final Coordinate intersection) {
        return innerLineThatShallNotBeIntersected.distance(intersection) == 0.0;
    }

    private LineSegment[] getEndVisibilityCheckLines(final LineSegment outerLineSegment,
                                                     final LineSegment innerLineSegment) {
        return new LineSegment[]{new LineSegment(outerLineSegment.p0, innerLineSegment.p0),
                                 new LineSegment(outerLineSegment.p0, innerLineSegment.p1),
                                 new LineSegment(outerLineSegment.p1, innerLineSegment.p0),
                                 new LineSegment(outerLineSegment.p1, innerLineSegment.p1)};
    }

    private boolean isIntersecting(final LineSegment p0ToP0, final LineSegment p1ToP1) {
        final Coordinate intersection = p0ToP0.intersection(p1ToP1);
        return intersection != null;
    }
}
