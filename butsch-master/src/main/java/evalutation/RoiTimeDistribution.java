package evalutation;

import data.*;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import routing.RPHAST;
import routing.RPHASTFactory;
import routing.regionAware.util.EntryExitPointExtractor;
import storage.CsvColumnDumper;
import storage.ImportPBF;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RoiTimeDistribution {
    private final static String PBF = Config.PBF_ANDORRA;
    private final static Random random = new Random(1337);

    private final static String[] DUMP_HEADER = new String[]{"relationId", "numEENodes", "startNode", "endNode", "timeInRoi"};
    private final static String RESULT_PATH = Config.ROI_TIME + LocalDateTime.now().toString().replaceAll(":", "_") + ".csv";
    private final static char DELIMITER = ',';

    private static RPHASTFactory rphastFactory;

    private static DataInstance instance;
    private static RoadCH roadCh;

    private static List<List<Object>> results = new ArrayList<>(DUMP_HEADER.length);

    public static void main(String[] args) {
        init();

        int i = 0;
        for (final NodeRelation relation : instance.relations) {
            System.out.println(LocalDateTime.now() + ": " + ++i + " / " + instance.relations.size());
            final EntryExitPointExtractor eeEx = new EntryExitPointExtractor(
                    new RegionOfInterest(relation.toPolygon()), instance.index);
            final Set<Node> entryExitPoints = eeEx.extract();

            final RPHAST rphast = (RPHAST) rphastFactory.createRoutingAlgorithm();
            final List<Path> paths = rphast.findPaths(entryExitPoints, entryExitPoints);

            results.get(0).addAll(Collections.nCopies(paths.size(), relation.id));
            results.get(1).addAll(Collections.nCopies(paths.size(), entryExitPoints.size()));
            paths.stream().map(p -> p.getStartVertex().id).collect(Collectors.toCollection(() -> results.get(2)));
            paths.stream().map(p -> p.getEndVertex().id).collect(Collectors.toCollection(() -> results.get(3)));
            paths.stream().map(p -> p.getWeight()).collect(Collectors.toCollection(() -> results.get(4)));
        }

        dump();
    }

    private static void init() {
        instance = DataInstance.createFromImporter(new ImportPBF(PBF));
        roadCh = new RoadCH(new ContractionHierarchyPrecomputation(instance.graph).computeContractionHierarchy());
        rphastFactory = new RPHASTFactory(roadCh, false);
        for (int i = 0; i < DUMP_HEADER.length; i++) {
            results.add(new ArrayList<>(instance.relations.size()));
        }
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
}
