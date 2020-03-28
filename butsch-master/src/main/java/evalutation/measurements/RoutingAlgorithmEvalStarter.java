package evalutation.measurements;

import data.RoadGraph;
import evalutation.measurements.utils.MeasureSuite;
import evalutation.measurements.utils.Result;
import routing.RoutingAlgorithmFactory;

import java.util.Arrays;

public class RoutingAlgorithmEvalStarter {
    static RoutingAlgorithmFactory[] algorithms;
    static int[][] startNodes;
    static int[][] endNodes;

    @SuppressWarnings("UnusedReturnValue")
    static Result[][] measure(final RoadGraph graph) {
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
