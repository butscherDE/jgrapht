package evalutation.measurements.utils;

import java.util.Arrays;
import java.util.List;

public class Result {
    private final String algorithm;
    private final int[] startNode;
    private final int[] endNode;
    public double runningTime;
    private List<Double> weights;
    public int numSettledNodes;
    public int numCheckedNeighbors;

    public Result(String algorithm, int[] startNode, int[] endNode) {
        this.algorithm = algorithm;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public void saveResult(double runningTime, List<Double> weights) {
        saveResult(runningTime, weights, -1, -1);
    }

    public void saveResult(double runningTime, List<Double> weights, int numSettledNodes, int numCheckedNeighbors) {
        this.runningTime = runningTime;
        this.weights = weights;
        this.numSettledNodes = numSettledNodes;
        this.numCheckedNeighbors = numCheckedNeighbors;
    }

    @Override
    public String toString() {
        return algorithm + "," + Arrays.toString(startNode) + "," + Arrays.toString(
                endNode) + "," + runningTime + "," + weights + "," + numSettledNodes;
    }
}
