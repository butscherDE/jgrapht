package evalutation.lotCalc;

import data.*;
import evalutation.Config;
import evalutation.DataInstance;
import evalutation.TestRegion;
import evalutation.TestRegionCreator;
import index.GridIndex;
import org.jgrapht.alg.util.Pair;
import routing.DijkstraCHFactory;
import routing.RPHAST;
import routing.RPHASTFactory;
import routing.RoutingAlgorithm;
import routing.regionAware.util.EntryExitPointExtractor;
import storage.CsvColumnDumper;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class LOTNodeExtractionSpeed {
    private final static String PBF = Config.PBF_ANDORRA;
    private final static int numSourceTarget = 10;
    private final static int numPolygonsEachSource = 100;
    private final static Random random = new Random(1337);

    private final static String[] DUMP_HEADER = new String[]{"relationId", "numEENodes", "naiveTime", "rphastTime"};
    private final static String RESULT_PATH = Config.LOT + LocalDateTime.now().toString().replaceAll(":", "_") + ".csv";
    private final static char DELIMITER = ',';

    private static DijkstraCHFactory dijkstraCHFactory;
    private static RPHASTFactory rphastFactory;

    private static DataInstance instance;
    private static RoadCH roadCh;
    private static List<TestRegion> testRegions;
    private static List<Node> vertices;
    private static Set<Node> sources = new LinkedHashSet<>();
    private static Set<Node> targets = new LinkedHashSet<>();

    private static List<List<Object>> results = new ArrayList<>(DUMP_HEADER.length);

    public static void main(String[] args) {
        init();

        int i = 0;
        for (final TestRegion testRegion : testRegions) {
            System.out.println("Testing " + ++i + "/" + testRegions.size());
            sampleSourceTarget();
            final Set<Node> entryExitPoints = createEntryExitPoints(testRegion);
            measure(testRegion, entryExitPoints);
        }

        dump();
    }

    public static void measure(final TestRegion testRegion, final Set<Node> entryExitPoints) {
        final long durationNaive = getDurationNaive(entryExitPoints);
        final long durationRphast = getDurationRphast(entryExitPoints);
        addResult(testRegion, entryExitPoints, durationNaive, durationRphast);
    }

    public static long getDurationNaive(final Set<Node> entryExitPoints) {
        final long naiveNanos0 = System.nanoTime();
        getAllPathsNaive(entryExitPoints);
        final long naiveNanos1 = System.nanoTime();
        return naiveNanos1 - naiveNanos0;
    }

    public static long getDurationRphast(final Set<Node> entryExitPoints) {
        final long rphastNanos0 = System.nanoTime();
        getAllPathsRphast(entryExitPoints);
        final long rphastNanos1 = System.nanoTime();
        return rphastNanos1 - rphastNanos0;
    }

    public static void addResult(final TestRegion testRegion, final Set<Node> entryExitPoints, final long durationNaive,
                                 final long durationRphast) {
        results.get(0).add(testRegion.id);
        results.get(1).add(entryExitPoints.size());
        results.get(2).add(durationNaive);
        results.get(3).add(durationRphast);
    }

    private static void dump() {
        final CsvColumnDumper dumper = new CsvColumnDumper(RESULT_PATH, DUMP_HEADER, results, DELIMITER);
        try {
            dumper.dump();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void init() {
        assignInstance();
        assignCh();
        assignTestRegions();
        createDijkstraFactory();
        createRphastFactory();

        for (int i = 0; i < DUMP_HEADER.length; i++) {
            results.add(new ArrayList<>(testRegions.size()));
        }
    }

    private static void assignInstance() {
        final ImportPBF importPBF = new ImportPBF(PBF);
        try {
            final RoadGraph graph = importPBF.createGraph();
            final List<NodeRelation> relations = importPBF.getNodeRelations();

            instance = DataInstance.createFromData(graph, new GridIndex(graph, 25, 25), relations);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        vertices = new ArrayList<>(instance.graph.vertexSet());
    }

    private static void assignCh() {
        roadCh = new CHPreprocessing(instance.graph).createCHGraph();
    }

    private static void assignTestRegions() {
        testRegions = new TestRegionCreator(instance, numPolygonsEachSource, polygon -> true).getTestEntities();
    }

    private static void createDijkstraFactory() {
        dijkstraCHFactory = new DijkstraCHFactory(roadCh, false);
    }

    private static void createRphastFactory() {
        rphastFactory = new RPHASTFactory(roadCh, false);
    }

    private static Set<Node> createEntryExitPoints(final TestRegion testRegion) {
        final RegionOfInterest roi = new RegionOfInterest(testRegion.polygon);
        final EntryExitPointExtractor eeExtractor = new EntryExitPointExtractor(roi, instance.index);
        return eeExtractor.extract();
    }

    private static void sampleSourceTarget() {
        sampleNodes(sources);
        sampleNodes(targets);
    }

    private static void sampleNodes(final Set<Node> nodes) {
        Collections.shuffle(vertices);
        nodes.clear();
        nodes.addAll(vertices.subList(0, Math.min(numSourceTarget, vertices.size())));
    }

    private static Map<Pair<Node, Node>, Path> getAllPathsNaive(final Set<Node> entryExitNodes) {
        final Map<Pair<Node, Node>, Path> allPaths = new HashMap<>();
        final RoutingAlgorithm routingAlgorithm = dijkstraCHFactory.createRoutingAlgorithm();

        putAllCrossProductPaths(routingAlgorithm, allPaths, sources, entryExitNodes);
        putAllCrossProductPaths(routingAlgorithm, allPaths, entryExitNodes, targets);

        return allPaths;
    }

    private static void putAllCrossProductPaths(final RoutingAlgorithm routingAlgorithm, final Map<Pair<Node, Node>, Path> allPaths, final Set<Node> sources, final Set<Node> targets) {
        for (final Node source : sources) {
            for (final Node target : targets) {
                allPaths.put(new Pair(source, target), routingAlgorithm.findPath(source, target));
            }
        }
    }

    private static Map<Pair<Node, Node>, Path> getAllPathsRphast(final Set<Node> entryExitNodes) {
        final Set<Node> rphastSources = new HashSet<>(sources);
        rphastSources.addAll(entryExitNodes);

        final Set<Node> rphastTargets = new HashSet<>(entryExitNodes);
        rphastTargets.addAll(targets);

        final RPHAST rphast = (RPHAST) rphastFactory.createRoutingAlgorithm();
        return rphast.findPathsAsMap(rphastSources, rphastTargets);
    }
}
