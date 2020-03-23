package geometry;

import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;

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

        int layerIndex = 0;
        while (layerIndex < cl.layers.length - 1) {
            outerSegments[layerIndex] = getRandomHullLine(cl.layers[layerIndex]);
            innerSegments[layerIndex] = getRandomEndVisibleLineSegment(outerSegments[layerIndex],
                                                                       cl.getLayerAsLineSegments(layerIndex + 1));

            layerIndex++;
        }

        for (int i = 0; i < outerSegmentsIndicesOnHull.length; i++) {
            outerSegmentsIndicesOnHull[i] = ArrayUtils.indexOf(cl.layers[i].getCoordinates(), outerSegments[i]);
            outerSegmentsIndicesOnHull[i] = ArrayUtils.indexOf(cl.layers[i + 1].getCoordinates(), innerSegments[i]);
        }

        final List<LineSegment> randomPolygon = new ArrayList<>(/* TODO we can get this size beforehand */);
        final List<List<LineSegment>> layersLS = cl.getLayersAsLineSegments();
        for (int i = 0; i < cl.layers.length - 1; i++) {
            final List<LineSegment> outerLayer = layersLS.get(i);
            final List<LineSegment> innerLayer = layersLS.get(i + 1);

            for (int j = 0; j < outerLayer.size(); j++) {
                if (j < outerSegmentsIndicesOnHull[i]) {
                    //                    randomPolygon.add()
                } else if (j == outerSegmentsIndicesOnHull[i]) {

                } else {

                }
            }
        }

        return null;
    }

    private List<LineSegment> getPolygonLines(final List<List<LineSegment>> layerLS, final int layerIndex,
                                              final int iterationStart) {
        final List<LineSegment> polygon = new LinkedList<>();
        final ListIterator<LineSegment> outerIterator = layerLS.get(layerIndex).listIterator(iterationStart);

        while (outerIterator.hasNext()) {
            final LineSegment outerLine = outerIterator.next();

            if (outerLine.equals(outerSegments[layerIndex])) {
                final double distanceOutP0ToInP1 = outerLine.p0.distance(innerSegments[layerIndex].p1);
                final double distanceOutP0ToInP0 = outerLine.p0.distance(innerSegments[layerIndex].p0);
                if (distanceOutP0ToInP1 < distanceOutP0ToInP0) {
                    polygon.add(new LineSegment(outerLine.p0, innerSegments[layerIndex].p1));
                    final List<LineSegment> nextInnerLayer = layerLS.get(layerIndex + 1);
                    final int startIndexInNextInnerLayer = nextInnerLayer.indexOf(innerSegments[layerIndex]) + 1;
                    final List<LineSegment> nextInnerSegments = getPolygonLines(layerLS, layerIndex + 1, startIndexInNextInnerLayer);
                    polygon.addAll(nextInnerSegments);
                    polygon.add(new LineSegment(innerSegments[layerIndex].p0, outerLine.p1));
                } else {

                }
            } else {
                polygon.add(outerLine);
            }
        }

        return null;
    }

    private LineSegment getRandomHullLine(final Geometry convexLayer) {
        final Coordinate[] coordinates = convexLayer.getCoordinates();

        final int randomCoordinateIndex = random.nextInt(convexLayer.getNumPoints() - 1);
        final Coordinate startCoordinate = coordinates[randomCoordinateIndex];
        final Coordinate endCoordinate = coordinates[randomCoordinateIndex + 1];

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
