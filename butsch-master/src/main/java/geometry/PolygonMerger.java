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
    private boolean chosenToInnerRingNotInversed;

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
            processWhenInnerIsJustAPoint();
        }
    }

    private void processWhenInnerLargeEnoughToBeAPolygon(final LineSegment outerChosen, final LineSegment innerChosen) {
        addAllInnerCoordinates(outerChosen, innerChosen);
    }

    private void processWhenInnerIsJustAPoint() {
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

        int newInnerIterationStartIndex = innerCoordinates.indexOf(innerEntryPoint);
        int newInnerIterationExitIndex = innerCoordinates.indexOf(innerExitPoint);

        if (getIndexDistance(newInnerIterationExitIndex, newInnerIterationStartIndex, innerCoordinates.size()) == 1) {
            Collections.reverse(innerCoordinates);
            newInnerIterationStartIndex = innerCoordinates.indexOf(innerEntryPoint);
        } else if (getIndexDistance(newInnerIterationExitIndex, newInnerIterationStartIndex, innerCoordinates.size()) == -1) {
            // Alles gut
        } else {
            throw new IllegalStateException("This cannot happen");
        }

        final ListIterator<Coordinate> innerCircularIterator = innerCoordinates.listIterator(newInnerIterationStartIndex);
        while (innerCircularIterator.hasNext()) {
            final Coordinate next = innerCircularIterator.next();
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
    }

    private void nextInnerEqualToChosenP1(final LineSegment innerChosen, final int indexOfInnerChosen) {
        final int indexOfNextToChosen = (indexOfInnerChosen + 1) % innerCoordinates.size();
        final Coordinate nextCoordinateToInnerP0 = innerCoordinates.get(indexOfNextToChosen);
        chosenToInnerRingNotInversed = nextCoordinateToInnerP0.equals(innerChosen.p1);
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


        if (isNotIntersected[0] && isNotIntersected[3] && !isIntersecting(endVisibilityCheckLines[0], endVisibilityCheckLines[3])) {
            return new LineSegment[] {endVisibilityCheckLines[0], endVisibilityCheckLines[3]};
        } else if (isNotIntersected[1] && isNotIntersected[2] && !isIntersecting(endVisibilityCheckLines[1], endVisibilityCheckLines[2])) {
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
