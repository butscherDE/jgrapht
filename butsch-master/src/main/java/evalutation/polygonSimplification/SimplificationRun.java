package evalutation.polygonSimplification;

import data.Node;
import data.NodeRelation;
import data.RegionOfInterest;
import data.RoadGraph;
import evalutation.Config;
import evalutation.DataInstance;
import geometry.CLPolygonGeneratorFactory;
import geometry.PolygonGenerator;
import geometry.StarPolygonGeneratorFactory;
import geometry.TwoOptPolygonGenerator;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.*;
import storage.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimplificationRun {
    private final static String[] DUMP_HEADER = new String[]{"id", "algo", "relationId", "time", "contractions", "before", "after"};
    private final static String RESULT_PATH = Config.POLY_SIMPLIFICATION + LocalDateTime.now()
            .toString()
            .replaceAll(":", "_") + ".csv";
    private final static char DELIMITER = ',';
    private final PolygonSimplifier simple;
    private final PolygonSimplifier extended;
    private final PolygonSimplifier full;
    private final List<TestEntity> relations;
    private List<List<Object>> results;

    public SimplificationRun(String dataPath, int maxPolySize) {
        failOnNonExistingPath();

        final DataInstance instance = DataInstance.createFromImporter(new ImportPBF(dataPath));

        simple = new PolygonSimplifierSimpleGreedy(instance.index);
        extended = new PolygonSimplifierExtendedGreedy(instance.index);
        full = new PolygonSimplifierFullGreedy(instance.index);

        final RegionSubGraphBuilder regionSubGraphBuilder = new RegionSubGraphBuilder();
        relations = getRelations(maxPolySize, instance, regionSubGraphBuilder);
    }

    private List<TestEntity> getRelations(int maxPolySize, DataInstance instance, RegionSubGraphBuilder regionSubGraphBuilder) {
        final Stream<TestEntity> realEntities = getRealDataStream(instance, regionSubGraphBuilder);

        try {
            final Stream<TestEntity> starEntities = getStarStream();
            final Stream<TestEntity> clEntities = getClStream();
            final Stream<TestEntity> twoOptEntities = getTwoOptStream();

            final List<TestEntity> testEntities = concatenateAndCollect(maxPolySize, realEntities, starEntities, clEntities, twoOptEntities);

            return testEntities;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not import data");
        }
    }

    private Stream<TestEntity> getRealDataStream(DataInstance instance, RegionSubGraphBuilder regionSubGraphBuilder) {
        return instance.relations.stream()
                    .filter(r -> {
                        final RegionOfInterest roi = new RegionOfInterest(r.toPolygon());
                        final Set<Node> entryExitNodes = new EntryExitPointExtractor(roi, instance.index).extract();
                        final RoadGraph subGraph = regionSubGraphBuilder.getSubGraph(instance.graph, roi, entryExitNodes);
                        return subGraph.vertexSet()
                                .size() > 1;
                    })
                    .map(r -> new TestEntity(r.id, r.toPolygon()));
    }

    private Stream<TestEntity> getStarStream() throws IOException {
        final CircularPolygonImporter starImporter = new CircularPolygonImporter(Config.POLYGON_PATH + "300_200_StarPolygonGeneratorFactory.txt");
        return starImporter.importPolygons()
                .stream()
                .map(p -> new TestEntity(-1, p));
    }

    private Stream<TestEntity> getClStream() throws IOException {
        final CircularPolygonImporter clImporter = new CircularPolygonImporter(Config.POLYGON_PATH + "300_200_CLPolygonGeneratorFactory.txt");
        return clImporter.importPolygons()
                .stream()
                .map(p -> new TestEntity(-2, p));
    }

    private Stream<TestEntity> getTwoOptStream() throws IOException {
        final CircularPolygonImporter twoOptImporter = new CircularPolygonImporter(Config.POLYGON_PATH + "300_200_TwoOptPolygonGeneratorFactory.txt");
        return twoOptImporter.importPolygons()
                .stream()
                .map(p -> new TestEntity(-3, p));
    }

    private List<TestEntity> concatenateAndCollect(int maxPolySize, Stream<TestEntity> realEntities, Stream<TestEntity> starEntities, Stream<TestEntity> clEntities, Stream<TestEntity> twoOptEntities) {
        final Stream<TestEntity> concat = Stream.concat(Stream.concat(Stream.concat(realEntities, starEntities), clEntities), twoOptEntities);
        return concat.filter(e -> e.polygon.getCoordinates().length - 1 <= maxPolySize)
                .collect(Collectors.toList());
    }

    private void failOnNonExistingPath() {
        final String parent = new File(RESULT_PATH).getParent();
        File directory = new File(parent);
        if (!directory.exists()) {
            new FileNotFoundException(directory.toString()).printStackTrace();
            System.exit(-1);
        }
    }

    public void run() {
        final List<List<Object>> results = measure();

        final CsvDumper csvDump = new CsvColumnDumper(RESULT_PATH, DUMP_HEADER, results, DELIMITER);
        try {
            csvDump.dump();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private List<List<Object>> measure() {
        prepareResults();
        execute();
        return results;
    }

    private void prepareResults() {
        results = new ArrayList<>(7);
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
    }

    private void execute() {
        int id = 0;
        for (TestEntity relation : relations) {
            System.out.println(LocalDateTime.now() + ": Run# " + (id / 3 + 1) + "/" + relations.size() +
                    ", relation-id: " + relation.id + ", size: " + (relation.polygon.getCoordinates().length -  1));
            final Polygon polygon = relation.polygon;

            final Result[] result = new Result[3];
            result[0] = measure(simple, polygon);
            result[1] = measure(extended, polygon);
            result[2] = measure(full, polygon);

            id = addId(id);
            addAlgorithmName();
            addRelationId(relation.id);
            addSimplificationtime(result);
            addNumContractions(result);
            addPolygonSizeBeforeSimplificiation(polygon);
            addPolygonSizeAfterSimplification(result);
        }
    }

    private int addId(int id) {
        results.get(0)
                .add(id++);
        results.get(0)
                .add(id++);
        results.get(0)
                .add(id++);
        return id;
    }

    private void addAlgorithmName() {
        results.get(1)
                .add("simple");
        results.get(1)
                .add("extended");
        results.get(1)
                .add("full");
    }

    private void addRelationId(Long relationId) {
        results.get(2)
                .add(relationId);
        results.get(2)
                .add(relationId);
        results.get(2)
                .add(relationId);
    }

    private void addSimplificationtime(Result[] result) {
        for (Result res : result) {
            results.get(3)
                    .add(getNanosAsSeconds(res));
        }
    }

    private double getNanosAsSeconds(final Result res) {
        return res.nanos / 1e9;
    }

    private void addNumContractions(Result[] result) {
        for (Result res : result) {
            results.get(4)
                    .add(res.contractions);
        }
    }

    private void addPolygonSizeBeforeSimplificiation(Polygon polygon) {
        final int polygonSizeBefore = getPolygonSize(polygon);
        results.get(5)
                .add(polygonSizeBefore);
        results.get(5)
                .add(polygonSizeBefore);
        results.get(5)
                .add(polygonSizeBefore);
    }

    private void addPolygonSizeAfterSimplification(Result[] result) {
        for (Result res : result) {
            results.get(6)
                    .add(getPolygonSize(res.polygon));
        }
    }

    private int getPolygonSize(Polygon polygon) {
        return polygon.getCoordinates().length - 1;
    }

    private Result measure(final PolygonSimplifier simplifier, final Polygon polygon) {
        final long start = System.nanoTime();
        final Polygon simplePolygon = simplifier.simplify(polygon);
        final long end = System.nanoTime();
        final int contractions = simplifier.getContractions();

        return new Result(end - start, simplePolygon, contractions);
    }

    private class TestEntity {
        public final long id;
        public final Polygon polygon;

        public TestEntity(long id, Polygon polygon) {
            this.id = id;
            this.polygon = polygon;
        }
    }

    private class Result {
        public final long nanos;
        public final Polygon polygon;
        public final int contractions;

        public Result(long nanos, Polygon polygon, final int contractions) {
            this.nanos = nanos;
            this.polygon = polygon;
            this.contractions = contractions;
        }
    }

}
