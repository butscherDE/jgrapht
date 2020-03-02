package data;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
    private final Graph<Node, Edge> graph;

    public Edge() {
        super();
        graph = null;
    }

    public Edge(Graph<Node, Edge> graph) {
        super();
        this.graph = graph;
    }

    public Node getSourceNode() {
        return graph.getEdgeSource(this);
    }

    public Node getTargetNode() {
        return graph.getEdgeTarget(this);
    }

    public double getWeight() {
        return graph.getEdgeWeight(this);
    }
}
