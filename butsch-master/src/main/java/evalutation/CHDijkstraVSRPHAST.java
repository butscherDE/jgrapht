package evalutation;

import data.CHPreprocessing;
import data.RoadCH;
import data.RoadGraph;
import evalutation.utils.MeasureSuite;
import evalutation.utils.Result;
import routing.DijkstraCHFactory;
import routing.DijkstraFactorySimple;
import routing.RPHASTFactory;
import routing.RoutingAlgorithmFactory;
import storage.ImportERPGraph;
import storage.Importer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class CHDijkstraVSRPHAST {
    private final static int NUM_RUNS = 1;
    private final static int NUM_SOURCES = 5;
    private final static int NUM_TARGETS = 5;
    private static RoutingAlgorithmFactory[] algorithms;
    private static int[][] startNodes;
    private static int[][] endNodes;

    public static void main(String[] args) {
        try {
            performanceMeasurement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void performanceMeasurement() throws FileNotFoundException {
        final Importer importer = new ImportERPGraph(Config.ERP_PATH);
        final RoadGraph graph = importer.createGraph();
        final RoadCH ch = new CHPreprocessing(graph).createCHGraph();

        algorithms = new RoutingAlgorithmFactory[]{new DijkstraFactorySimple(graph), new DijkstraCHFactory(ch), new RPHASTFactory(ch)};

        startNodes = new int[NUM_RUNS][NUM_SOURCES];
        endNodes = new int[NUM_RUNS][NUM_TARGETS];
        createRandomStartEndNodes(graph.getNumNodes());

        measure(graph);
    }

    private static void createRandomStartEndNodes(int numNodes) {
        final Random randomNodeIDs = new Random(1337);

        for (int i = 0; i < NUM_RUNS; i++) {
            for (int j = 0; j < NUM_SOURCES; j++) {
                startNodes[i][j] = randomNodeIDs.nextInt(numNodes);
            }
            for (int j = 0; j < NUM_TARGETS; j++) {
                endNodes[i][j] = randomNodeIDs.nextInt(numNodes);
            }
        }
    }

    private static Result[][] measure(final RoadGraph graph) {
        final MeasureSuite ms = new MeasureSuite(graph, algorithms, startNodes, endNodes);
        ms.measure();
        for (RoutingAlgorithmFactory algorithm : algorithms) {
            System.out.print(algorithm.getClass().getSimpleName() + ", ");
        }
        System.out.println();
        double[] avgRunningTime = ms.getAverageRunningTimePerAlgorithm();
        System.out.println("Running times in seconds: " + Arrays.toString(avgRunningTime));

        return ms.getResults();
    }
}
