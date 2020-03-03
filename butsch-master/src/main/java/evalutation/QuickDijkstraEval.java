package evalutation;

import data.RoadGraph;
import evalutation.utils.MeasureSuite;
import evalutation.utils.Result;
import routing.DijkstraFactorySimple;
import routing.SimpleRoutingAlgorithmFactory;
import storage.ImportERPGraph;
import storage.Importer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class QuickDijkstraEval {
    private final static int NUM_RUNS = 100;

    public static void main(String[] args) {
        try {
            performanceMeasurement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void performanceMeasurement() throws FileNotFoundException {
        final Importer importer = new ImportERPGraph(Config.GER_PATH);
        final RoadGraph graph = importer.createGraph();

        final SimpleRoutingAlgorithmFactory[] algorithms = new SimpleRoutingAlgorithmFactory[]{new DijkstraFactorySimple(graph)};

        final int[][] startNodes = new int[NUM_RUNS][1];
        final int[][] endNodes = new int[NUM_RUNS][1];
        createRandomStartEndNodes(graph.getNumNodes(), startNodes, endNodes);

        measure(graph, algorithms, startNodes, endNodes);
    }

    private static void createRandomStartEndNodes(int numNodes, int[][] startNodes, int[][] endNodes) {
        final Random randomNodeIDs = new Random(1337);

        for (int i = 0; i < NUM_RUNS; i++) {
            startNodes[i] = new int[] {randomNodeIDs.nextInt(numNodes)};
            endNodes[i] = new int[] {randomNodeIDs.nextInt(numNodes)};
        }
    }

    private static Result[][] measure(final RoadGraph graph, SimpleRoutingAlgorithmFactory[] algorithms, int[][] startNodes,
                                      int[][] endNodes) {
        final MeasureSuite ms = new MeasureSuite(graph, algorithms, startNodes, endNodes);
        ms.measure();
        for (SimpleRoutingAlgorithmFactory algorithm : algorithms) {
            System.out.print(algorithm.getClass().getSimpleName() + ", ");
        }
        System.out.println();
        double[] avgRunningTime = ms.getAverageRunningTimePerAlgorithm();
        System.out.println("Running times in seconds: " + Arrays.toString(avgRunningTime));

        return ms.getResults();
    }
}
