package data;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;

import java.util.LinkedList;
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

    public Path createMergedPath(final Path otherPath) {
        if (!getEndVertex().equals(otherPath.getStartVertex())) {
            throw new IllegalArgumentException("end and start vertex must be overlapping");
        }

        final List<Edge> edges = new LinkedList<>(getEdgeList());
        edges.addAll(otherPath.getEdgeList());
        final double combinedWeight = getWeight() + otherPath.getWeight();

        final GraphWalk<Node, Edge> newWalk = new GraphWalk<>(this.path.getGraph(), getStartVertex(), otherPath.getEndVertex(),
                                                              edges, combinedWeight);
        return new Path(newWalk);
    }

    @Override
    public String toString() {
        return "(" + getStartVertex() + "-" + getEndVertex() + ")=" + path.toString();
    }
}
