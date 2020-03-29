package data;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;
import util.BinaryHashFunction;


import java.util.LinkedList;
import java.util.List;

public class Path {
    final GraphPath<Node, Edge> path;

    public Path(GraphPath<Node, Edge> path) {
        this.path = path;
    }

    public Path(final RoadGraph graph, final Node startNode, final Node endNode, final List<Edge> edgeList,
                final double weight) {
        this.path = new GraphWalk<>(graph, startNode, endNode, edgeList, weight);
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

        return new Path((RoadGraph) this.path.getGraph(), getStartVertex(), otherPath.getEndVertex(), edges, combinedWeight);
    }

    public boolean isSelfIntersecting() {
        return containsDuplicateVertices();
    }

    public boolean containsDuplicateVertices() {
        final BinaryHashFunction<Node> hashFunction = new BinaryHashFunction();

        for (final Edge edge : path.getEdgeList()) {
            final Node node = getGraph().getEdgeSource(edge);
            if (hashFunction.get(node)) {
                return true;
            } else {
                hashFunction.set(node, true);
            }
        }

        return hashFunction.get(getEndVertex());
    }

    public boolean isFound() {
        if (getStartVertex().equals(getEndVertex())) {
            return true;
        } else {
            return getLength() > 0;
        }
    }

    @Override
    public String toString() {
        return "(" + getStartVertex() + "-" + getEndVertex() + ")=" + path.toString();
    }
}
