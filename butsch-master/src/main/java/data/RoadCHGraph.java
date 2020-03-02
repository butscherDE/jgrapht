package data;

import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionVertex;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.function.Supplier;

public class RoadCHGraph extends DefaultDirectedWeightedGraph<ContractionVertex<Node>, ContractionEdge<Edge>> {
    public RoadCHGraph(final Class<? extends ContractionEdge<Edge>> edgeClass) {
        super(edgeClass);
    }

    public RoadCHGraph(final Supplier<ContractionVertex<Node>> vertexSupplier,
                       final Supplier<ContractionEdge<Edge>> edgeSupplier) {
        super(vertexSupplier, edgeSupplier);
    }

    public int getNumNodes() {
        return vertexSet().size();
    }
}
