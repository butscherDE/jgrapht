package data;

import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionHierarchy;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionVertex;

import java.util.LinkedList;
import java.util.List;

public class RoadCH {
    final ContractionHierarchy<Node, Edge> ch;

    public RoadCH(final ContractionHierarchy<Node, Edge> ch) {
        this.ch = ch;
    }

    public ContractionHierarchy<Node, Edge> getCh() {
        return ch;
    }

    public RoadGraph getGraph() {
        return (RoadGraph) ch.getGraph();
    }

    public RoadCHGraph getChGraph() {
        return (RoadCHGraph) ch.getContractionGraph();
    }

    public ContractionVertex getContractionVertex(final Node node) {
        return ch.getContractionMapping().get(node);
    }

    public List<Edge> unpack(final ContractionEdge chEdge) {
        final LinkedList<Node> vertices = new LinkedList<>();
        final LinkedList<Edge> edges = new LinkedList<>();

        ch.unpackForward(chEdge, vertices, edges);

        return edges;
    }
}
