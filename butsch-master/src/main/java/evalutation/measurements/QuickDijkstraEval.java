package evalutation.measurements;

import data.RoadGraph;
import evalutation.Config;
import evalutation.measurements.utils.MeasureSuite;
import evalutation.measurements.utils.Result;
import routing.DijkstraFactorySimple;
import routing.SimpleRoutingAlgorithmFactory;
import storage.GraphImporter;
import storage.ImportERPGraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class QuickDijkstraEval extends RoutingAlgorithmEvalStarter {
    private final static int NUM_RUNS = 100;

    public static void main(String[] args) {
        try {
            performanceMeasurement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void performanceMeasurement() throws FileNotFoundException {
        final GraphImporter graphImporter = new ImportERPGraph(Config.ERP_PATH);
        final RoadGraph graph = graphImporter.createGraph();

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
        final MeasureSuite ms = execMeasurement(graph, algorithms, startNodes, endNodes);
        printAlgorithms(algorithms);
        printRunningTimes(ms);

        return ms.getResults();
    }

    private static MeasureSuite execMeasurement(final RoadGraph graph, final SimpleRoutingAlgorithmFactory[] algorithms,
                                                final int[][] startNodes, final int[][] endNodes) {
        final MeasureSuite ms = new MeasureSuite(graph, algorithms, startNodes, endNodes);
        ms.measure();
        return ms;
    }

    private static void printAlgorithms(final SimpleRoutingAlgorithmFactory[] algorithms) {
        for (SimpleRoutingAlgorithmFactory algorithm : algorithms) {
            System.out.print(algorithm.getClass().getSimpleName() + ", ");
        }
        System.out.println();
    }

    private static void printRunningTimes(final MeasureSuite ms) {
        double[] avgRunningTime = ms.getAverageRunningTimePerAlgorithm();
        System.out.println("Running times in seconds: " + Arrays.toString(avgRunningTime));
    }
}
