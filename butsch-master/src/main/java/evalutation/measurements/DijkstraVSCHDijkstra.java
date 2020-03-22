package evalutation.measurements;

import data.CHPreprocessing;
import data.RoadCH;
import data.RoadGraph;
import evalutation.Config;
import evalutation.measurements.utils.MeasureSuite;
import evalutation.measurements.utils.Result;
import routing.DijkstraCHFactory;
import routing.DijkstraFactorySimple;
import routing.RoutingAlgorithmFactory;
import storage.GraphImporter;
import storage.ImportERPGraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class DijkstraVSCHDijkstra {
    private final static int NUM_RUNS = 100;
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
        final GraphImporter graphImporter = new ImportERPGraph(Config.ERP_PATH);
        final RoadGraph graph = graphImporter.createGraph();
        final RoadCH ch = new CHPreprocessing(graph).createCHGraph();

        algorithms = new RoutingAlgorithmFactory[]{new DijkstraFactorySimple(graph), new DijkstraCHFactory(ch, true)};

        startNodes = new int[NUM_RUNS][1];
        endNodes = new int[NUM_RUNS][1];
        createRandomStartEndNodes(graph.getNumNodes());

        measure(graph);
    }

    private static void createRandomStartEndNodes(int numNodes) {
        final Random randomNodeIDs = new Random(1337);

        for (int i = 0; i < NUM_RUNS; i++) {
            startNodes[i] = new int[] {randomNodeIDs.nextInt(numNodes)};
            endNodes[i] = new int[] {randomNodeIDs.nextInt(numNodes)};
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