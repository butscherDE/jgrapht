package evalutation.utils;

import data.RoadGraph;
import routing.RoutingAlgorithm;
import routing.RoutingAlgorithmFactory;

public class MeasureSuite {
    private final RoadGraph graph;
    private final RoutingAlgorithmFactory[] routingAlgorithms;
    private final int[] startNodes;
    private final int[] endNodes;
    private final Result[][] results;

    public MeasureSuite(final RoadGraph graph, RoutingAlgorithmFactory[] routingAlgorithms, int[] startNodes, int[] endNodes) {
        this.graph = graph;
        this.routingAlgorithms = routingAlgorithms;
        this.startNodes = startNodes;
        this.endNodes = endNodes;
        results = new Result[routingAlgorithms.length][startNodes.length];
    }

    public void measure() {
        createAndStartAllTasks();
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
        final int startNode = startNodes[vertexPairId];
        final int endNode = endNodes[vertexPairId];

        createResultObjectForTask(algorithmId, vertexPairId, routingAlgorithm, startNode, endNode);
        return new Task(graph.getVertex(startNode), graph.getVertex(endNode), routingAlgorithm, results[algorithmId][vertexPairId]);
    }

    private void createResultObjectForTask(int i, int j, RoutingAlgorithm routingAlgorithm, int startNode, int endNode) {
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
                averages [i] += algorithmResult.numSettledNodes;
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
                averages [i] += algorithmResult.numCheckedNeighbors;
            }

            averages[i] /= algorithmResults.length;
        }

        return averages;
    }
}
