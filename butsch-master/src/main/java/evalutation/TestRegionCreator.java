package evalutation;

import geometry.BoundingBox;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import storage.CircularPolygonImporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestRegionCreator {
    private final DataInstance instance;
    private final int numRelationsPerType;
    private final Function<Polygon, Boolean> relationFilter;

    public TestRegionCreator(final DataInstance instance, final int numRelationsPerType,
                             final Function<Polygon, Boolean> relationFilter) {
        this.instance = instance;
        this.numRelationsPerType = numRelationsPerType;
        this.relationFilter = relationFilter;
    }

    public List<TestRegion> getTestEntities() {
        final List<TestRegion> entities = getRealDataStream();

        try {
            addArtificialEntities(entities);
            return entities;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not import data");
        }
    }

    private List<TestRegion> getRealDataStream() {
        final List<TestRegion> realRegions = new ArrayList<>();
        instance.relations.stream()
                          .filter(r -> relationFilter.apply(r.toPolygon()))
                          .map(r -> new TestRegion(r.id, r.toPolygon()))
                          .collect(Collectors.toCollection(() -> realRegions));

        Collections.shuffle(realRegions);
        return realRegions.subList(0, numRelationsPerType);
    }

    private void addArtificialEntities(List<TestRegion> entities) throws IOException {
        final Stream<Polygon> starEntities = getStarStream();
        final Stream<Polygon> clEntities = getClStream();
        final Stream<Polygon> twoOptEntities = getTwoOptStream();

        final BoundingBox graphBounds = BoundingBox.createFrom(instance.graph);
        addTransformedEntities(-1, entities, starEntities, graphBounds);
        addTransformedEntities(-2, entities, clEntities, graphBounds);
        addTransformedEntities(-3, entities, twoOptEntities, graphBounds);
    }

    private Stream<Polygon> getStarStream() throws IOException {
        final CircularPolygonImporter starImporter = new CircularPolygonImporter(Config.POLYGON_PATH + "300_200_StarPolygonGeneratorFactory.txt");
        return starImporter.importPolygons()
                           .stream();
    }

    private Stream<Polygon> getClStream() throws IOException {
        final CircularPolygonImporter clImporter = new CircularPolygonImporter(Config.POLYGON_PATH + "300_200_CLPolygonGeneratorFactory.txt");
        return clImporter.importPolygons()
                         .stream();
    }

    private Stream<Polygon> getTwoOptStream() throws IOException {
        final CircularPolygonImporter twoOptImporter = new CircularPolygonImporter(Config.POLYGON_PATH + "300_200_TwoOptPolygonGeneratorFactory.txt");
        return twoOptImporter.importPolygons()
                             .stream();
    }

    private List<TestRegion> addTransformedEntities(final int id, List<TestRegion> entities, Stream<Polygon> polygonStream, BoundingBox graphBounds) {
        return polygonStream.map(p -> scaleAndTranslate(p, graphBounds))
                            .filter(p -> relationFilter.apply(p))
                            .limit(numRelationsPerType)
                            .map(p -> new TestRegion(id, p))
                            .collect(Collectors.toCollection(() -> entities));
    }

    public static Polygon scaleAndTranslate(Polygon p, BoundingBox graphBounds) {
        final Coordinate[] coordinates = p.getCoordinates();
        final double width = graphBounds.maxLongitude - graphBounds.minLongitude;
        final double height = graphBounds.maxLatitude - graphBounds.minLatitude;

        final double polygonWidth = Math.random() * width;
        final double polygonHeight = Math.random() * height;

        final double polygonLongitudeTranslation = Math.random() * (width - polygonWidth) + graphBounds.minLongitude;
        final double polygonLatitudeTranslation = Math.random() * (height - polygonHeight) + graphBounds.minLatitude;

        for (Coordinate coordinate : coordinates) {
            coordinate.x = coordinate.x * polygonWidth + polygonLongitudeTranslation;
            coordinate.y = coordinate.y * polygonHeight + polygonLatitudeTranslation;
        }

        return p;
    }

}
