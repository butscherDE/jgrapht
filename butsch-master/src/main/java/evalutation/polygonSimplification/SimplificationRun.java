package evalutation.polygonSimplification;

import data.Node;
import data.RegionOfInterest;
import data.RoadGraph;
import evalutation.Config;
import evalutation.DataInstance;
import evalutation.TestRegion;
import evalutation.TestRegionCreator;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.*;
import storage.CsvColumnDumper;
import storage.CsvDumper;
import storage.ImportPBF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimplificationRun {
    private final static GeometryFactory gf = new GeometryFactory();
    private final static RegionSubGraphBuilder regionSubGraphBuilder = new RegionSubGraphBuilder();
    private final static String[] DUMP_HEADER = new String[]{"id", "algo", "relationId", "time", "contractions", "before", "after"};
    private final static String RESULT_PATH = Config.POLY_SIMPLIFICATION + LocalDateTime.now()
            .toString()
            .replaceAll(":", "_") + ".csv";
    private final static char DELIMITER = ',';
    private final PolygonSimplifier simple;
    private final PolygonSimplifier extended;
    private final PolygonSimplifier full;
    private final List<TestRegion> testentitites;
    private List<List<Object>> results;
    private final DataInstance instance;

    public SimplificationRun(String dataPath, int maxPolySize) {
        failOnNonExistingPath();

        instance = DataInstance.createFromImporter(new ImportPBF(dataPath));

        simple = new PolygonSimplifierSimpleGreedy(instance.index);
        extended = new PolygonSimplifierExtendedGreedy(instance.index);
        full = new PolygonSimplifierFullGreedy(instance.index);

        testentitites = new TestRegionCreator(instance, dataPath, Integer.MAX_VALUE, new Function<Polygon, Boolean>() {
            @Override
            public Boolean apply(final Polygon polygon) {
                final boolean relationSize = polygon.getCoordinates().length - 1 <= maxPolySize;
                final boolean regionSubGraphNotEmpty = isRegionSubGraphNotEmpty(polygon);

                return relationSize && regionSubGraphNotEmpty;
            }
        }).getTestEntities();
        System.out.println(testentitites.stream().map(a -> a.id).collect(Collectors.toList()));
    }

    private boolean isRegionSubGraphNotEmpty(Polygon p) {
        final RegionOfInterest roi = new RegionOfInterest(p);
        final Set<Node> entryExitNodes = new EntryExitPointExtractor(roi, instance.index).extract();
        final RoadGraph subGraph = regionSubGraphBuilder.getSubGraph(instance.graph, roi, entryExitNodes);
        return subGraph.vertexSet()
                .size() > 1;
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
        results.add(new ArrayList<>(testentitites.size() * 3));
        results.add(new ArrayList<>(testentitites.size() * 3));
        results.add(new ArrayList<>(testentitites.size() * 3));
        results.add(new ArrayList<>(testentitites.size() * 3));
        results.add(new ArrayList<>(testentitites.size() * 3));
        results.add(new ArrayList<>(testentitites.size() * 3));
        results.add(new ArrayList<>(testentitites.size() * 3));
    }

    private void execute() {
        int id = 0;
        for (TestRegion relation : testentitites) {
            System.out.println(LocalDateTime.now() + ": Run# " + (id / 3 + 1) + "/" + testentitites.size() +
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
