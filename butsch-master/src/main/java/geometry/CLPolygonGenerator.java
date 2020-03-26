package geometry;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.*;
import java.util.List;

public class CLPolygonGenerator extends PolygonGenerator {
    private final Random random;

    public CLPolygonGenerator(final int numPoints, final Random random) {
        super(numPoints, random);
        this.random = random;
    }

    @Override
    /**
     * Creates random coordinates from the same convex layers.
     */
    public Polygon createRandomSimplePolygon() {
        final Coordinate[] randomCoordinates = createRandomCoordinates();
        final GeometryFactory geometryFactory = new GeometryFactory();
        final MultiPoint asPoints = geometryFactory.createMultiPointFromCoords(randomCoordinates);
        final ConvexLayers cl = new ConvexLayers(asPoints);

        Coordinate[] mergedPolygon = cl.layers[0].getCoordinates();
        LineSegment lastInner = new LineSegment();
        for (int layerIndex = 0; layerIndex < cl.layers.length - 1; layerIndex++) {
            final LineSegment outerSegment = getRandomHullLine(cl.layers[layerIndex], lastInner);

            final LineSegment innerSegment;
            if (cl.layers[layerIndex + 1] instanceof Point) {
                final Point point = (Point) cl.layers[layerIndex + 1];
                innerSegment = new LineSegment(point.getX(), point.getY(), point.getX(), point.getY());
            } else {
                innerSegment = getRandomEndVisibleLineSegment(outerSegment, cl.getLayerAsLineSegments(layerIndex + 1));
            }
            lastInner = innerSegment;

            final PolygonMerger polygonMerger = new PolygonMerger(mergedPolygon, cl.layers[layerIndex + 1].getCoordinates());
            mergedPolygon = polygonMerger.mergePolygons(outerSegment, innerSegment);
        }

        return geometryFactory.createPolygon(mergedPolygon);
    }

    private LineSegment getRandomHullLine(final Geometry convexLayer, final LineSegment last) {
        final Coordinate[] coordinates = convexLayer.getCoordinates();

        Coordinate startCoordinate;
        Coordinate endCoordinate;
        do {
            final int randomCoordinateIndex = random.nextInt(coordinates.length - 1);
            startCoordinate = coordinates[randomCoordinateIndex];
            endCoordinate = coordinates[randomCoordinateIndex + 1];
        } while (startCoordinate.equals(last.p0) && endCoordinate.equals(last.p1));

        return new LineSegment(startCoordinate, endCoordinate);
    }

    private LineSegment getRandomEndVisibleLineSegment(final LineSegment outerLineSegment,
                                                       final List<LineSegment> innerLayerAsLineSegments) {
        final List<LineSegment> endVisibleLineSegments = getEndVisibleLineSegments(outerLineSegment,
                                                                                   innerLayerAsLineSegments);

        return endVisibleLineSegments.get(random.nextInt(endVisibleLineSegments.size()));
    }

    private List<LineSegment> getEndVisibleLineSegments(final LineSegment outerLineSegment,
                                                        final List<LineSegment> innerLayerAsLineSegments) {
        final List<LineSegment> endVisibleLineSegments = new ArrayList<>();


        for (final LineSegment innerLineSegment : innerLayerAsLineSegments) {
            if (isEndVisible(outerLineSegment, innerLineSegment, innerLayerAsLineSegments)) {
                endVisibleLineSegments.add(innerLineSegment);
            }
        }

        return endVisibleLineSegments;
    }

    private boolean isEndVisible(final LineSegment outerLineSegment, final LineSegment innerLineSegment,
                                 final List<LineSegment> innerLayerAsLineSegments) {
        final LineSegment[] endVisibilityCheckLines = getEndVisibilityCheckLines(outerLineSegment, innerLineSegment);
        final boolean[] isNotIntersected = new boolean[4];

        for (int i = 0; i < isNotIntersected.length; i++) {
            final LineSegment endVisibilityCheckLine = endVisibilityCheckLines[i];
            isNotIntersected[i] = true;
            for (final LineSegment possibleSightBlockingLine : innerLayerAsLineSegments) {
                final boolean isIntersecting = isIntersectionProduced(possibleSightBlockingLine, endVisibilityCheckLine,
                                                                      innerLineSegment);
                isNotIntersected[i] &= !isIntersecting;
            }
        }

        if (isNotIntersected[0] && isNotIntersected[3] && !isIntersecting(endVisibilityCheckLines[0], endVisibilityCheckLines[3])) {
            return true;
        } else if (isNotIntersected[1] && isNotIntersected[2] && !isIntersecting(endVisibilityCheckLines[1], endVisibilityCheckLines[2])) {
            return true;
        } else {
            return false;
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

    private boolean isIntersecting(final LineSegment ls1, final LineSegment ls2) {
        final Coordinate intersection = ls1.intersection(ls2);
        return intersection != null;
    }
}
