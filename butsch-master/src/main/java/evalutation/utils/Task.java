package evalutation.utils;

import data.Node;
import routing.RoutingAlgorithm;

class Task {
    private final Node startNode;
    private final Node endNode;
    private final RoutingAlgorithm routingAlgorithm;
    private final Result result;

    private double runningTime = -1;
    private double weight;

    public Task(Node startNode, Node endNode, RoutingAlgorithm routingAlgorithm, Result result) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.routingAlgorithm = routingAlgorithm;
        this.result = result;
    }

    public void run() {
        measure();
        buildResult();
    }

    private void measure() {
        final long startTime = System.nanoTime();
        weight = routingAlgorithm.getWeight(startNode, endNode);
        final long endTime = System.nanoTime();

        runningTime = (endTime - startTime) / 1_000_000_000.0;
    }

    private void buildResult() {
        result.saveResult(runningTime, weight);
    }

    public Result getResult() {
        if (runningTime == -1) {
            return failBecauseMeasurementWasNotRanFirst();
        } else {
            return this.result;
        }
    }

    private Result failBecauseMeasurementWasNotRanFirst() {
        throw new IllegalStateException("Run the measurement first with run()");
    }

    @Override
    public String toString() {
        return routingAlgorithm.getClass().getSimpleName() + ", " + startNode + ":" + endNode;
    }
}
