package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CLPolygonGenerator extends PolygonGenerator {
    private final Random random = new Random(42);
    private LineSegment[] outerSegments;
    private LineSegment[] innerSegments;
    private int[] outerSegmentsIndicesOnHull;
    private int[] innerSegmentsIndicesOnHull;

    public CLPolygonGenerator(final int numPoints) {
        super(numPoints);
    }

    @Override
    /**
     * Creates random coordinates from the same convex layers.
     */ public Polygon createRandomSimplePolygon() {
        final Coordinate[] randomCoordinates = createRandomCoordinates();
        final MultiPoint asPoints = new GeometryFactory().createMultiPointFromCoords(randomCoordinates);
        final ConvexLayers cl = new ConvexLayers(asPoints);
        outerSegments = new LineSegment[cl.layers.length - 1];
        innerSegments = new LineSegment[cl.layers.length - 1];
        outerSegmentsIndicesOnHull = new int[cl.layers.length - 1];
        innerSegmentsIndicesOnHull = new int[cl.layers.length - 1];

        Coordinate[] mergedPolygon = cl.layers[0].getCoordinates();
        LineSegment lastInner = new LineSegment();
        for (int layerIndex = 0; layerIndex < cl.layers.length - 1; layerIndex++) {
            System.out.println(layerIndex);
            final LineSegment outerSegment = getRandomHullLine(cl.layers[layerIndex], lastInner);
            final LineSegment innerSegment = getRandomEndVisibleLineSegment(outerSegment, cl.getLayerAsLineSegments(layerIndex + 1));

            final PolygonMerger polygonMerger = new PolygonMerger(mergedPolygon, cl.layers[layerIndex + 1].getCoordinates());
            mergedPolygon = polygonMerger.mergePolygons(outerSegment, innerSegment);
        }

        return new GeometryFactory().createPolygon(mergedPolygon);
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

        boolean endVisibleToOuterLine = true;
        for (final LineSegment possibleSightBlockingLine : innerLayerAsLineSegments) {
            for (final LineSegment endVisibilityCheckLine : endVisibilityCheckLines) {
                final boolean isIntersecting = isIntersectionProduced(possibleSightBlockingLine, endVisibilityCheckLine,
                                                                      innerLineSegment);
                endVisibleToOuterLine &= !isIntersecting;
            }
        }

        return endVisibleToOuterLine;
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
        return new LineSegment[]{new LineSegment(outerLineSegment.p0, innerLineSegment.p0), new LineSegment(
                outerLineSegment.p0, innerLineSegment.p1), new LineSegment(outerLineSegment.p1,
                                                                           innerLineSegment.p0), new LineSegment(
                outerLineSegment.p1, innerLineSegment.p1)};
    }
}
