package geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import util.CircularList;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class PolygonMerger {
    private final Coordinate[] outerCoordinates;
    private final List<Coordinate> innerCoordinates;

    private int mergedCoordinatesPointer;
    private int outerCoordinatesPointer;
    private Coordinate[] mergedCoordinates;
    private LineSegment outerChosen;
    private LineSegment innerChosen;

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

    public Coordinate[] mergePolygons(final LineSegment outerChosen, final LineSegment innerChosen) {
        setupDataStructures(outerChosen, innerChosen);

        final int endOfOuterCoordinates = outerCoordinates.length - 1;
        for (outerCoordinatesPointer = 0; outerCoordinatesPointer < endOfOuterCoordinates; outerCoordinatesPointer++) {
            mergedCoordinates[mergedCoordinatesPointer++] = outerCoordinates[outerCoordinatesPointer];

            if (areNextTwoCoordsChosenByLineSegment()) {
                processInnerPolygon();
            }
        }
        mergedCoordinates[mergedCoordinatesPointer] = outerCoordinates[endOfOuterCoordinates];

        return mergedCoordinates;
    }

    public void setupDataStructures(final LineSegment outerChosen, final LineSegment innerChosen) {
        outerChosenOrientationCorrection(outerChosen);
        this.outerChosen = outerChosen;
        this.innerChosen = innerChosen;
        mergedCoordinates = new Coordinate[outerCoordinates.length + innerCoordinates.size()];
        mergedCoordinatesPointer = 0;
    }

    private void outerChosenOrientationCorrection(final LineSegment outerChosen) {
        boolean foundForward = searchForwardOnOuterCoordinates(outerChosen);

        if (!foundForward) {
            outerChosen.reverse();
            boolean foundReverse = searchForwardOnOuterCoordinates(outerChosen);

            handleNotFoundCase(foundReverse);
        }
    }

    private boolean searchForwardOnOuterCoordinates(final LineSegment outerChosen) {
        boolean foundForward = false;
        for (int j = 0; j < outerCoordinates.length - 1; j++) {
            final boolean basePointEqual = outerCoordinates[j].equals(outerChosen.p0);
            final boolean adjPointEqual = outerCoordinates[j + 1].equals(outerChosen.p1);

            foundForward |= basePointEqual && adjPointEqual;
        }
        return foundForward;
    }

    private void handleNotFoundCase(final boolean foundReverse) {
        if (!foundReverse) {
            throw new IllegalArgumentException("outerChosen is not on the outer polygon");
        }
    }

    private void processInnerPolygon() {
        if (innerCoordinates.size() > 1) {
            processWhenInnerLargeEnoughToBeAPolygon();
        } else {
            processWhenInnerIsJustAPoint();
        }
    }

    private void processWhenInnerLargeEnoughToBeAPolygon() {
        addAllInnerCoordinates();
    }

    private void processWhenInnerIsJustAPoint() {
        mergedCoordinates[mergedCoordinatesPointer++] = innerCoordinates.get(0);
    }

    private boolean areNextTwoCoordsChosenByLineSegment() {
        return outerCoordinates[outerCoordinatesPointer].equals(outerChosen.p0) && outerCoordinates[outerCoordinatesPointer + 1].equals(outerChosen.p1);
    }

    private void addAllInnerCoordinates() {
        final List<LineSegment> innerLayerAsLineSegments = ConvexLayers.getLineSegments(innerCoordinates);
        final LineSegment[] linesToConnectInnerAndOuter = getEndVisibilityBridgeLines(innerLayerAsLineSegments);
        int innerIterationStartIndex = getIndexOfEntryPoint(linesToConnectInnerAndOuter[0]);
        int newInnerIterationExitIndex = getIndexOfExitPoint(linesToConnectInnerAndOuter, 1);

        if (isStartBeforeExit(innerIterationStartIndex, newInnerIterationExitIndex)) {
            addInnerCoordinatesBackward(innerIterationStartIndex);
        } else if (isStartAfterExit(innerIterationStartIndex, newInnerIterationExitIndex)) {
            addInnerCoordinatesForward(innerIterationStartIndex);
        } else {
            throw new IllegalStateException("This cannot happen");
        }
    }

    private int getIndexOfEntryPoint(final LineSegment lineSegment) {
        final Coordinate innerEntryPoint = lineSegment.p1;
        return innerCoordinates.indexOf(innerEntryPoint);
    }

    private int getIndexOfExitPoint(final LineSegment[] linesToConnectInnerAndOuter, final int i) {
        final Coordinate innerExitPoint = linesToConnectInnerAndOuter[i].p1;
        return innerCoordinates.indexOf(innerExitPoint);
    }

    private boolean isStartBeforeExit(final int innerIterationStartIndex, final int newInnerIterationExitIndex) {
        return getIndexDistance(newInnerIterationExitIndex, innerIterationStartIndex, innerCoordinates.size()) == 1;
    }

    private void addInnerCoordinatesBackward(final int innerIterationStartIndex) {
        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(innerIterationStartIndex + 1);
        while (innerCircularIterator.hasPrevious()) {
            final Coordinate previous = innerCircularIterator.previous();
            mergedCoordinates[mergedCoordinatesPointer++] = previous;
        }
    }

    private boolean isStartAfterExit(final int innerIterationStartIndex, final int newInnerIterationExitIndex) {
        return getIndexDistance(newInnerIterationExitIndex, innerIterationStartIndex,
                                    innerCoordinates.size()) == -1;
    }

    private void addInnerCoordinatesForward(final int innerIterationStartIndex) {
        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(
                innerIterationStartIndex);
        while (innerCircularIterator.hasNext()) {
            final Coordinate next = innerCircularIterator.next();
            mergedCoordinates[mergedCoordinatesPointer++] = next;
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

    private LineSegment[] getEndVisibilityBridgeLines(final List<LineSegment> innerLayerAsLineSegments) {
        final LineSegment[] endVisibilityCheckLines = getEndVisibilityCheckLines();
        final boolean[] isNotIntersected = new boolean[4];

        for (int i = 0; i < isNotIntersected.length; i++) {
            final LineSegment endVisibilityCheckLine = endVisibilityCheckLines[i];
            isNotIntersected[i] = true;
            for (final LineSegment possibleSightBlockingLine : innerLayerAsLineSegments) {
                final boolean isIntersecting = isIntersectionProduced(possibleSightBlockingLine, endVisibilityCheckLine);
                isNotIntersected[i] &= !isIntersecting;
            }
        }


        if (isNotIntersected[0] && isNotIntersected[3] && !isIntersecting(endVisibilityCheckLines[0],
                                                                          endVisibilityCheckLines[3])) {
            return new LineSegment[]{endVisibilityCheckLines[0], endVisibilityCheckLines[3]};
        } else if (isNotIntersected[1] && isNotIntersected[2] && !isIntersecting(endVisibilityCheckLines[1],
                                                                                 endVisibilityCheckLines[2])) {
            return new LineSegment[]{endVisibilityCheckLines[1], endVisibilityCheckLines[2]};
        } else {
            throw new IllegalStateException("This shall never be happening");
        }
    }

    private boolean isIntersectionProduced(final LineSegment possiblySightBlockingLine, final LineSegment checkLine) {
        final Coordinate intersection = possiblySightBlockingLine.intersection(checkLine);
        if (intersection != null) {
            return !isIntersectionOnLineThatShouldntBeIntersected(innerChosen, intersection);
        } else {
            return false;
        }
    }

    private boolean isIntersectionOnLineThatShouldntBeIntersected(final LineSegment innerLineThatShallNotBeIntersected,
                                                                  final Coordinate intersection) {
        return innerLineThatShallNotBeIntersected.distance(intersection) == 0.0;
    }

    private LineSegment[] getEndVisibilityCheckLines() {
        return new LineSegment[]{new LineSegment(outerChosen.p0, innerChosen.p0), new LineSegment(
                outerChosen.p0, innerChosen.p1), new LineSegment(outerChosen.p1,
                                                                 innerChosen.p0), new LineSegment(
                outerChosen.p1, innerChosen.p1)};
    }

    private boolean isIntersecting(final LineSegment p0ToP0, final LineSegment p1ToP1) {
        final Coordinate intersection = p0ToP0.intersection(p1ToP1);
        return intersection != null;
    }
}
