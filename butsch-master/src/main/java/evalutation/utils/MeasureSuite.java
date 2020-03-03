package evalutation.utils;

import data.Node;
import data.RoadGraph;
import org.jgrapht.util.StopWatch;
import routing.RoutingAlgorithm;
import routing.RoutingAlgorithmFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class MeasureSuite {
    private final RoadGraph graph;
    private final RoutingAlgorithmFactory[] routingAlgorithms;
    private final int[][] startNodes;
    private final int[][] endNodes;
    private final Result[][] results;

    public MeasureSuite(final RoadGraph graph, RoutingAlgorithmFactory[] routingAlgorithms, int[][] startNodes,
                        int[][] endNodes) {
        this.graph = graph;
        this.routingAlgorithms = routingAlgorithms;
        this.startNodes = startNodes;
        this.endNodes = endNodes;
        results = new Result[routingAlgorithms.length][startNodes.length];
    }

    public void measure() {
        final StopWatch swMeasure = new StopWatch("Measure " + startNodes.length +
                                                  " runs with " + startNodes[0].length +
                                                  " sources and " + endNodes[0].length).start();
        createAndStartAllTasks();
        System.out.println(swMeasure.stop().toString());
    }

    private void createAndStartAllTasks() {
        startAllTasks();
    }

    private void startAllTasks() {
        for (int i = 0; i < routingAlgorithms.length; i++) {
            for (int j = 0; j < startNodes.length; j++) {
                final Task task = createTask(i, j);

                task.run();
            }
        }
    }

    private Task createTask(int algorithmId, int vertexPairId) {
        final RoutingAlgorithm routingAlgorithm = routingAlgorithms[algorithmId].createRoutingAlgorithm();

        final int[] startNodeIds = startNodes[vertexPairId];
        final Set<Node> startNodeSet = new LinkedHashSet<>();
        for (final int startNodeId : startNodeIds) {
            startNodeSet.add(graph.getVertex(startNodeId));
        }

        final int[] endNodeIds = endNodes[vertexPairId];
        final Set<Node> endNodeSet = new LinkedHashSet<>();
        for (final int endNodeId : endNodeIds) {
            endNodeSet.add(graph.getVertex(endNodeId));
        }

        createResultObjectForTask(algorithmId, vertexPairId, routingAlgorithm, startNodeIds, endNodeIds);
        return new Task(startNodeSet, endNodeSet, routingAlgorithm, results[algorithmId][vertexPairId]);
    }

    private void createResultObjectForTask(int i, int j, RoutingAlgorithm routingAlgorithm, int[] startNode,
                                           int[] endNode) {
        final String routingAlgorithmName = routingAlgorithm.getClass().getSimpleName();
        results[i][j] = new Result(routingAlgorithmName, startNode, endNode);
    }

    public Result[][] getResults() {
        return results;
    }

    public double[] getAverageRunningTimePerAlgorithm() {
        final double[] averages = new double[results.length];

        for (int i = 0; i < results.length; i++) {
            final Result[] algorithmsResults = results[i];
            for (Result algorithmsResult : algorithmsResults) {
                averages[i] += algorithmsResult.runningTime;
            }

            averages[i] /= algorithmsResults.length;
        }

        return averages;
    }

    public int[] getAverageSettledNodesPerAlgorithm() {
        final int[] averages = new int[results.length];

        for (int i = 0; i < results.length; i++) {
            final Result[] algorithmResults = results[i];
            for (Result algorithmResult : algorithmResults) {
                averages[i] += algorithmResult.numSettledNodes;
            }

            averages[i] /= algorithmResults.length;
        }

        return averages;
    }

    public int[] getAverageCheckedNeighborsPerAlgorithm() {
        final int[] averages = new int[results.length];

        for (int i = 0; i < results.length; i++) {
            final Result[] algorithmResults = results[i];
            for (Result algorithmResult : algorithmResults) {
                averages[i] += algorithmResult.numCheckedNeighbors;
            }

            averages[i] /= algorithmResults.length;
        }

        return averages;
    }
}
