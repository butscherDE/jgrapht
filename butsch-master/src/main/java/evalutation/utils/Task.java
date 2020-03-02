package evalutation.utils;

import data.Node;
import data.Path;
import routing.RoutingAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Task {
    private final Set<Node> startNodes;
    private final Set<Node> endNodes;
    private final RoutingAlgorithm routingAlgorithm;
    private final Result result;

    private double runningTime = -1;
    private List<Double> weights;

    public Task(Set<Node> startNode, Set<Node> endNode, RoutingAlgorithm routingAlgorithm, Result result) {
        this.startNodes = startNode;
        this.endNodes = endNode;
        this.routingAlgorithm = routingAlgorithm;
        this.result = result;
    }

    public void run() {
        measure();
        buildResult();
    }

    private void measure() {
        final long startTime = System.nanoTime();
        List<Path> paths = routingAlgorithm.findPaths(startNodes, endNodes);
        final long endTime = System.nanoTime();

        weights = new ArrayList<>(paths.size());
        for (final Path path : paths) {
            weights.add(path.getWeight());
        }

        runningTime = (endTime - startTime) / 1_000_000_000.0;
    }

    private void buildResult() {
        result.saveResult(runningTime, weights);
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
        return routingAlgorithm.getClass().getSimpleName() + ", " + startNodes + ":" + endNodes;
    }
}
