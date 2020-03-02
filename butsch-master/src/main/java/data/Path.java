package data;

import org.jgrapht.GraphPath;

import java.util.List;

public class Path {
    final GraphPath<Node, Edge> path;

    public Path(final GraphPath<Node, Edge> path) {
        this.path = path;
    }

    public List<Edge> getEdgeList() {
        return path.getEdgeList();
    }

    public Node getEndVertex() {
        return path.getEndVertex();
    }

    public RoadGraph getGraph() {
        return (RoadGraph) path.getGraph();
    }

    public int getLength() {
        return path.getLength();
    }

    public Node getStartVertex() {
        return path.getStartVertex();
    }

    public List<Node> getVertexList() {
        return path.getVertexList();
    }

    public double getWeight() {
        return path.getWeight();
    }
}
