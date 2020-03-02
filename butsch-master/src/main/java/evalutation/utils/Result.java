package evalutation.utils;

public class Result {
    private final String algorithm;
    private final int startNode;
    private final int endNode;
    public double runningTime;
    private double weight;
    public int numSettledNodes;
    public int numCheckedNeighbors;

    public Result(String algorithm, int startNode, int endNode) {
        this.algorithm = algorithm;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public void saveResult(double runningTime, double weight) {
        saveResult(runningTime, weight, -1, -1);
    }

    public void saveResult(double runningTime, double weight, int numSettledNodes, int numCheckedNeighbors) {
        this.runningTime = runningTime;
        this.weight = weight;
        this.numSettledNodes = numSettledNodes;
        this.numCheckedNeighbors = numCheckedNeighbors;
    }

    @Override
    public String toString() {
        return algorithm + "," + startNode + "," + endNode + "," + runningTime + "," + weight + "," + numSettledNodes;
    }
}
