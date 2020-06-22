package evalutation.polygonSimplification;

import data.Node;
import data.NodeRelation;
import data.RegionOfInterest;
import data.RoadGraph;
import evalutation.Config;
import evalutation.DataInstance;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.*;
import storage.CsvColumnDumper;
import storage.CsvDumper;
import storage.ImportPBF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SimplificationRun {
    private final static String[] DUMP_HEADER = new String[] {"id", "algo", "relationId", "time", "before", "after"};
    private final static String RESULT_PATH = Config.POLY_SIMPLIFICATION + LocalDateTime.now().toString().replaceAll(":", "_") + ".csv";
    private final static char DELIMITER = ',';
    private final PolygonSimplifier simple;
    private final PolygonSimplifier extended;
    private final PolygonSimplifier full;
    private final List<NodeRelation> relations;
    private List<List<Object>> results;

    public SimplificationRun(String dataPath, int maxPolySize) {
        failOnNonExistingPath();

        final DataInstance instance = DataInstance.createFromImporter(new ImportPBF(dataPath));

        simple = new PolygonSimplifierSimpleGreedy(instance.index);
        extended = new PolygonSimplifierExtendedGreedy(instance.index);
        full = new PolygonSimplifierFullGreedy(instance.index);

        final RegionSubGraphBuilder regionSubGraphBuilder = new RegionSubGraphBuilder();
        relations = instance.relations.stream()
                .filter(r -> r.nodes.size() <= maxPolySize)
                .filter(r -> {
                    final RegionOfInterest roi = new RegionOfInterest(r.toPolygon());
                    final Set<Node> entryExitNodes = new EntryExitPointExtractor(roi, instance.index).extract();
                    final RoadGraph subGraph = regionSubGraphBuilder.getSubGraph(instance.graph, roi, entryExitNodes);
                    return subGraph.vertexSet().size() > 1;
                })
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
        results = new ArrayList<>(6);
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
        results.add(new ArrayList<>(relations.size() * 3));
    }

    private void execute() {
        int id = 0;
        for (NodeRelation relation : relations) {
            System.out.println("Run# " + id / 3 + "/" + relations.size() + ", relation-id: " + relation.id);
            final Polygon polygon = relation.toPolygon();

            final Result[] result = new Result[3];
            result[0] = measure(simple, polygon);
            result[1] = measure(extended, polygon);
            result[2] = measure(full, polygon);

            id = addId(id);
            addAlgorithmName();
            addRelationId(relation);
            addSimplificationtime(result);
            addPolygonSizeBeforeSimplificiation(polygon);
            addPolygonSizeAfterSimplification(result);
        }
    }

    private int addId(int id) {
        results.get(0).add(id++);
        results.get(0).add(id++);
        results.get(0).add(id++);
        return id;
    }

    private void addAlgorithmName() {
        results.get(1).add("simple");
        results.get(1).add("extended");
        results.get(1).add("full");
    }

    private void addRelationId(NodeRelation relation) {
        results.get(2).add(relation.id);
        results.get(2).add(relation.id);
        results.get(2).add(relation.id);
    }

    private void addSimplificationtime(Result[] result) {
        for (Result res : result) {
            results.get(3).add(res.nanos / 1e9);
        }
    }

    private void addPolygonSizeBeforeSimplificiation(Polygon polygon) {
        final int polygonSizeBefore = getPolygonSize(polygon);
        results.get(4).add(polygonSizeBefore);
        results.get(4).add(polygonSizeBefore);
        results.get(4).add(polygonSizeBefore);
    }

    private void addPolygonSizeAfterSimplification(Result[] result) {
        for (Result res : result) {
            results.get(5).add(getPolygonSize(res.polygon));
        }
    }

    private int getPolygonSize(Polygon polygon) {
        return polygon.getCoordinates().length - 1;
    }

    private Result measure(final PolygonSimplifier simplifier, final Polygon polygon) {
        final long start = System.nanoTime();
        final Polygon simplePolygon = simplifier.simplify(polygon);
        final long end = System.nanoTime();

        return new Result(end - start, simplePolygon);
    }


    private class Result {
        public final long nanos;
        public final Polygon polygon;

        public Result(long nanos, Polygon polygon) {
            this.nanos = nanos;
            this.polygon = polygon;
        }
    }

}
