package geometry;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ConvexLayers {
    final Geometry[] layers;

    public ConvexLayers(final Geometry pointGeometry) {
        layers = createConvexLayers(pointGeometry);
    }

    private Geometry[] createConvexLayers(final Geometry points) {
        Coordinate[] randomSortedCoordinates = points.getCoordinates();
        Arrays.sort(randomSortedCoordinates);

        final ArrayList<ConvexHull> chLayers = computeLayers(randomSortedCoordinates);

        return convertToGeometryArray(chLayers);
    }

    private ArrayList<ConvexHull> computeLayers(Coordinate[] randomSortedCoordinates) {
        final GeometryFactory geometryFactory = new GeometryFactory();
        final ArrayList<ConvexHull> chLayers = new ArrayList<>();

        while (randomSortedCoordinates.length > 0) {
            final MultiPoint multiCoordinate = geometryFactory.createMultiPointFromCoords(randomSortedCoordinates);
            final ConvexHull hull = createAndAddCurrentHull(chLayers, multiCoordinate);
            randomSortedCoordinates = updateCoordinatesForNextHull(randomSortedCoordinates, hull);
        }
        return chLayers;
    }

    private ConvexHull createAndAddCurrentHull(final ArrayList<ConvexHull> chLayers, final MultiPoint multiCoordinate) {
        final ConvexHull hull = new ConvexHull(multiCoordinate);
        chLayers.add(hull);
        return hull;
    }

    private Coordinate[] updateCoordinatesForNextHull(Coordinate[] coordinates, final ConvexHull hull) {
        coordinates = removeCoordinatesOnHull(coordinates, hull);
        return coordinates;
    }

    private Coordinate[] removeCoordinatesOnHull(final Coordinate[] coordinates, final ConvexHull hull) {
        final Coordinate[] hullCoordinates = getHullCoordinatesSorted(hull);

        return getCoordinatesNotContainedInHull(coordinates, hullCoordinates);
    }

    private Coordinate[] getHullCoordinatesSorted(final ConvexHull hull) {
        final Coordinate[] hullCoordinates = hull.getConvexHull().getCoordinates();
        Arrays.sort(hullCoordinates);
        return hullCoordinates;
    }

    private Coordinate[] getCoordinatesNotContainedInHull(final Coordinate[] coordinates,
                                                          final Coordinate[] hullCoordinates) {
        final ArrayList<Coordinate> filteredCoordinates = new ArrayList<>(
                coordinates.length - (hullCoordinates.length - 1));
        int j = 0;
        for (int i = 0; i < coordinates.length && j < hullCoordinates.length; ) {
            final Coordinate coordinate = coordinates[i];
            final Coordinate hullCoordinate = hullCoordinates[j];

            if (coordinate.compareTo(hullCoordinate) < 0) {
                filteredCoordinates.add(coordinate);
                i++;
            } else if (coordinate.compareTo(hullCoordinate) > 0) {
                j++;
            } else {
                i++;
            }
        }

        return filteredCoordinates.toArray(new Coordinate[filteredCoordinates.size()]);
    }

    private Geometry[] convertToGeometryArray(final ArrayList<ConvexHull> chLayers) {
        Geometry[] layers = new Geometry[chLayers.size()];
        for (int i = 0; i < chLayers.size(); i++) {
            layers[i] = chLayers.get(i).getConvexHull();
        }
        return layers;
    }

    public List<LineSegment> getLayerAsLineSegments(final int index) {
        if (index >= layers.length) {
            throw new IllegalArgumentException(index + " out of bounds");
        }

        final Geometry layer = layers[index];

        return getLineSegments(layer);
    }

    public static List<LineSegment> getLineSegments(final Collection<Coordinate> coordinates) {
        final Coordinate[] coordinatesArr = coordinates.toArray(new Coordinate[coordinates.size() + 1]);
        coordinatesArr[coordinatesArr.length - 1] = coordinatesArr[0];
        final Geometry geometry = new GeometryFactory().createMultiPointFromCoords(coordinatesArr);
        return getLineSegments(geometry);
    }

    public static List<LineSegment> getLineSegments(final Geometry layer) {
        final LineSegment[] lineSegments = new LineSegment[layer.getNumPoints() - 1];
        final Coordinate[] coordinates = layer.getCoordinates();

        for (int i = 0; i < lineSegments.length; i++) {
            lineSegments[i] = new LineSegment(coordinates[i], coordinates[i + 1]);
        }
        return Arrays.asList(lineSegments);
    }

    public List<List<LineSegment>> getLayersAsLineSegments() {
        final List<List<LineSegment>> layersAsLS = new ArrayList<>(layers.length);

        for (int i = 0; i < layers.length; i++) {
            layersAsLS.add(getLayerAsLineSegments(i));
        }

        return layersAsLS;
    }
}
