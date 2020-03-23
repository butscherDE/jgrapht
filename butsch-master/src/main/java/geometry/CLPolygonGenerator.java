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

        Coordinate[] mergedPolygon = cl.layers[0].getCoordinates();
        for (int i = 1; i < cl.layers.length; i++) {
            final PolygonMerger merger = new PolygonMerger(mergedPolygon, cl.layers[i].getCoordinates());
            mergedPolygon = merger.mergePolygons(outerSegments[i - 1], innerSegments[i - 1]);
        }

        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        final Color[] colors = new Color[] {
                Color.BLUE,
                Color.RED,
//                Color.BLUE,
//                Color.ORANGE,
//                Color.CYAN
        };
        for (int i = 0; i < cl.layers.length; i++) {
            col.addLineSegmentsFromCoordinates(colors[i], Arrays.asList(cl.layers[i].getCoordinates()));
        }
        col.addLineSegment(Color.CYAN, outerSegments[0]);
        col.addLineSegment(Color.ORANGE, innerSegments[0]);
        col.addLineSegments(Color.BLACK, Arrays.asList(new LineSegment(outerSegments[0].p0, innerSegments[0].p0), new LineSegment(outerSegments[0].p1, innerSegments[0].p1)));
        final GeometryVisualizer vis = new GeometryVisualizer(col);
        vis.visualizeGraph();

        return new GeometryFactory().createPolygon(mergedPolygon);
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
