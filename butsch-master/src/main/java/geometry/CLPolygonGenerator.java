package geometry;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Arrays;

public class CLPolygonGenerator extends PolygonGenerator {
    private ConvexHull[] convexLayers;

    public CLPolygonGenerator(final int numPoints) {
        super(numPoints);
        createConvexLayers();
    }

    @Override
    /**
     * Creates random coordinates from the same convex layers.
     */
    public Polygon createRandomSimplePolygon() {
        return null;
    }

    private void createConvexLayers() {
        Coordinate[] randomSortedCoordinates = createRandomCoordinates();
        Arrays.sort(randomSortedCoordinates);

        final ArrayList<ConvexHull> chLayers = computeLayers(randomSortedCoordinates);

        convexLayers = (ConvexHull[]) chLayers.toArray();
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

    private Coordinate[] getCoordinatesNotContainedInHull(final Coordinate[] coordinates, final Coordinate[] hullCoordinates) {
        final ArrayList<Coordinate> filteredCoordinates = new ArrayList<>(coordinates.length - hullCoordinates.length);
        int j = 0;
        for (int i = 0; i < coordinates.length; i++) {
            final Coordinate coordinate = coordinates[i];
            final Coordinate hullCoordinate = hullCoordinates[j];

            if (coordinate.compareTo(hullCoordinate) < 0) {
                filteredCoordinates.add(coordinate);
            } else {
                j++;
            }
        }

        return (Coordinate[]) filteredCoordinates.toArray();
    }
}
