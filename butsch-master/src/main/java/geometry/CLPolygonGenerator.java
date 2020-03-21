package geometry;

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

        int layerIndex = 0;
        while (layerIndex < cl.layers.length - 1) {
            outerSegments[layerIndex] = getRandomHullLine(cl.layers[layerIndex]);
            innerSegments[layerIndex] = getRandomEndVisibleLineSegment(outerSegments[layerIndex],
                                                                       cl.layers[layerIndex + 1]);

            layerIndex++;
        }

        for (int i = 0; i < cl.layers.length - 1; i++) {
            final LineSegment outerSegment = outerSegments[i];
            final LineSegment innerSegment = innerSegments[i];

            final LineSegment outerInner1 = new LineSegment(outerSegment.p0, innerSegment.p0);
            final LineSegment outerInner2 = new LineSegment(outerSegment.p1, innerSegment.p1);

            final Coordinate[] outerCoordinates = cl.layers[i].getCoordinates();
            final Coordinate[] innerCoordinates = cl.layers[i + 1].getCoordinates();
            final int iOut0 = CoordinateArrays.indexOf(outerSegment.p0, outerCoordinates);
            final int iOut1 = CoordinateArrays.indexOf(outerSegment.p1, outerCoordinates);
            final int iIn0 = CoordinateArrays.indexOf(innerSegment.p0, innerCoordinates);
            final int iIn1 = CoordinateArrays.indexOf(innerSegment.p1, innerCoordinates);

            System.out.println("iOut0 " + iOut0);
            System.out.println("iOut1 " + iOut1);
            System.out.println("iIn0 " + iIn0);
            System.out.println("iIn1 " + iIn1);

            //            final Coordinate[] combinedCoordinates = new Coordinate[outerCoordinates.length + innerCoordinates.length - 1];
            //            System.arraycopy(outerCoordinates, 0, combinedCoordinates, 0, outerCoordinates.length);
            //            System.arraycopy(innerCoordinates.length, outerCoordinates.length, combinedCoordinates, outerCoordinates.length, innerCoordinates.length);
            //            if (isIntersectionProduced(outerInner1, outerInner2)) {
            //                ArrayUtils.reverse(combinedCoordinates, outerCoordinates.length, outerCoordinates.length + innerCoordinates.length);
            //            }
            //            combinedCoordinates[combinedCoordinates.length - 1] = combinedCoordinates[0];
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

    private LineSegment getRandomEndVisibleLineSegment(final LineSegment outerLineSegment, final Geometry convexLayer) {
        final List<LineSegment> endVisibleLineSegments = getEndVisibleLineSegments(outerLineSegment, convexLayer);

        return endVisibleLineSegments.get(random.nextInt(endVisibleLineSegments.size()));
    }

    private List<LineSegment> getEndVisibleLineSegments(final LineSegment outerLineSegment,
                                                        final Geometry convexLayer) {
        final List<LineSegment> endVisibleLineSegments = new ArrayList<>();
        final LineSegment[] layerAsLineSegments = convertToLineSegments(convexLayer);

        for (final LineSegment innerLineSegment : layerAsLineSegments) {
            boolean endVisibleToOuterLine = isEndVisibleToOuterLineSegment(outerLineSegment, innerLineSegment,
                                                                           layerAsLineSegments);

            if (endVisibleToOuterLine) {
                endVisibleLineSegments.add(innerLineSegment);
            }
        }

        try {
            Thread.sleep(100_000);
        } catch (Exception e) {

        }

        return endVisibleLineSegments;
    }

    private boolean isEndVisibleToOuterLineSegment(final LineSegment outerLineSegment,
                                                   final LineSegment innerLineSegment,
                                                   final LineSegment[] layerAsLineSegments) {

        final GeometryVisualizer.GeometryDrawCollection collection = new GeometryVisualizer.GeometryDrawCollection();
        collection.addLineSegments(Color.BLACK, Arrays.asList(layerAsLineSegments));
        collection.addLineSegment(Color.BLACK, outerLineSegment);
        collection.addLineSegment(Color.RED, innerLineSegment);
        GeometryVisualizer geometryVisualizer = new GeometryVisualizer(collection);
        geometryVisualizer.visualizeGraph();


        final LineSegment[] endVisibilityCheckLines = getEndVisibilityCheckLines(outerLineSegment, innerLineSegment);
        boolean endVisibleToOuterLine = true;
        for (final LineSegment otherLineSegment : layerAsLineSegments) {
            for (final LineSegment endVisibilityCheckLine : endVisibilityCheckLines) {
                endVisibleToOuterLine &= isIntersectionProduced(otherLineSegment, endVisibilityCheckLine,
                                                                innerLineSegment);
            }
        }

        return endVisibleToOuterLine;
    }

    private boolean isIntersectionProduced(final LineSegment lineSegment1, final LineSegment lineSegment2,
                                           final LineSegment notToIntersect) {
        final boolean isIntersecting = lineSegment1.intersection(lineSegment2) == null;
        final boolean isntIntersecting = lineSegment2.intersection(notToIntersect) != null;
        return isIntersecting && isntIntersecting;
    }

    private LineSegment[] getEndVisibilityCheckLines(final LineSegment outerLineSegment,
                                                     final LineSegment innerLineSegment) {
        return new LineSegment[]{new LineSegment(outerLineSegment.p0, innerLineSegment.p0), new LineSegment(
                outerLineSegment.p0, innerLineSegment.p1), new LineSegment(outerLineSegment.p1,
                                                                           innerLineSegment.p0), new LineSegment(
                outerLineSegment.p1, innerLineSegment.p1)};
    }

    private LineSegment[] convertToLineSegments(final Geometry convexLayer) {
        final LineSegment[] lineSegments = new LineSegment[convexLayer.getNumPoints()];
        final Coordinate[] coordinates = convexLayer.getCoordinates();

        for (int i = 0; i < lineSegments.length - 1; i++) {
            lineSegments[i] = new LineSegment(coordinates[i], coordinates[i + 1]);
        }
        lineSegments[lineSegments.length - 1] = new LineSegment(coordinates[coordinates.length - 1], coordinates[0]);

        return lineSegments;
    }
}
