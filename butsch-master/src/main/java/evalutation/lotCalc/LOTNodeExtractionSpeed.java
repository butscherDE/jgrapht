package evalutation.lotCalc;

import data.*;
import evalutation.Config;
import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Polygon;
import routing.DijkstraCHFactory;
import routing.RPHASTFactory;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class LOTNodeExtractionSpeed {
    private final static String PBF = Config.PBF_ANDORRA;
    private final static long numSources = 10;
    private final static long numTargets = 10;
    private final static long numPolygonsEachSource = 100;
    private final static Random random = new Random(1337);

    private final static String[] DUMP_HEADER = new String[]{"id", "relationId", "naiveTime", "rphastTime"};
    private final static String RESULT_PATH = Config.LOT + LocalDateTime.now().toString().replaceAll(":", "_") + ".csv";
    private final static char DELIMITER = ',';

    private static DijkstraCHFactory dijkstraCHFactory;
    private static RPHASTFactory rphastFactory;

    private static RoadGraph graph;
    private static RoadCH roadCh;
    private static List<Polygon> polygons;
    private static List<Node> vertices;
    private static Set<Node> sources;
    private static Set<Node> targets;


    public static void main(String[] args) {

    }

    private static void init() {
        final ImportPBF importPBF = assignGraph();
        assignCh();
        createDijkstraFactory();
        createRphastFactory();
    }

    private static ImportPBF assignGraph() {
        final ImportPBF importPBF = new ImportPBF(PBF);
        try {
            graph = importPBF.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        vertices = new ArrayList<>(graph.vertexSet());

        return importPBF;
    }

    private static void assignCh() {
        roadCh = new CHPreprocessing(graph).createCHGraph();
    }

    private static void assignPolygons(final ImportPBF importPBF) {
        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations();
        final int numNodeRelations = nodeRelations.size();
        final Stream<NodeRelation> realPolygons = LongStream
                .rangeClosed(1, numPolygonsEachSource)
                .mapToObj(i -> nodeRelations.get(random.nextInt(numNodeRelations)));

//        Impor
    }

    private static void createDijkstraFactory() {
        dijkstraCHFactory = new DijkstraCHFactory(roadCh, false);
    }

    private static void createRphastFactory() {
        rphastFactory = new RPHASTFactory(roadCh, false);
    }

    private static void sampleSourceNodes() {
        for (long i = 0; i < numSources; i++) {
            sources.add(vertices.get(random.nextInt(vertices.size())));
        }
    }

    private static void sampleTargetNodes() {
        for (long i = 0; i < numTargets; i++) {
            targets.add(vertices.get(random.nextInt(vertices.size())));
        }
    }

    private static Map<Pair<Node, Node>, Path> getAllPathsNaive(final Set<Node> entryExitNodes) {
    return null;
    }

    private static Map<Pair<Node, Node>, Path> getAllPathsRphast(final Set<Node> entryExitNodes) {
        final Set<Node> rphastSources = new HashSet<>(sources);
        rphastSources.addAll(entryExitNodes);

        final Set<Node> rphastTargets = new HashSet<>(entryExitNodes);
        rphastTargets.addAll(targets);

//        new RPHAST()
        return null;
    }
}
