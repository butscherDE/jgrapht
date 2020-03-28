package evalutation.measurements;

import data.CHPreprocessing;
import data.RoadCH;
import data.RoadGraph;
import evalutation.Config;
import routing.DijkstraCHFactory;
import routing.RPHASTFactory;
import routing.RoutingAlgorithmFactory;
import storage.GraphImporter;
import storage.ImportERPGraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class CHDijkstraVSRPHAST extends RoutingAlgorithmEvalStarter {
    private final static int NUM_RUNS = 1;
    private final static int NUM_SOURCES = 10000;
    private final static int NUM_TARGETS = 10000;

    public static void main(String[] args) {
        try {

            performanceMeasurement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTestParameters() {
        System.out.println("Starting measurement with " + NUM_RUNS + " runs, " + NUM_SOURCES + " sources, " + NUM_TARGETS + " targets.");
        System.out.println("Algorithms: " + Arrays.toString(algorithms));
    }

    private static void performanceMeasurement() throws FileNotFoundException {
        final GraphImporter graphImporter = new ImportERPGraph(Config.ERP_PATH);
        final RoadGraph graph = graphImporter.createGraph();
        final RoadCH ch = new CHPreprocessing(graph).createCHGraph();

        algorithms = new RoutingAlgorithmFactory[]{new DijkstraCHFactory(ch, false), new RPHASTFactory(ch, false)};

        createRandomStartEndNodes(graph.getNumNodes());

        printTestParameters();

        measure(graph);
    }

    private static void createRandomStartEndNodes(int numNodes) {
        final Random randomNodeIDs = new Random(1337);
        startNodes = new int[NUM_RUNS][NUM_SOURCES];
        endNodes = new int[NUM_RUNS][NUM_TARGETS];

        for (int i = 0; i < NUM_RUNS; i++) {
            for (int j = 0; j < NUM_SOURCES; j++) {
                startNodes[i][j] = randomNodeIDs.nextInt(numNodes);
            }
            for (int j = 0; j < NUM_TARGETS; j++) {
                endNodes[i][j] = randomNodeIDs.nextInt(numNodes);
            }
        }
    }
}
