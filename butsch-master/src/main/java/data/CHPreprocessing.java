package data;

import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionHierarchy;

public class CHPreprocessing {
    final ContractionHierarchyPrecomputation<Node, Edge> chPreprocessor;

    public CHPreprocessing(final RoadGraph graph) {
        final int numCores = Runtime.getRuntime().availableProcessors();
        final int numCoresToUse = numCores - 2;

        chPreprocessor = new ContractionHierarchyPrecomputation<>(graph, numCoresToUse);
    }

    public RoadCH createCHGraph() {
        final ContractionHierarchy<Node, Edge> ch = chPreprocessor.computeContractionHierarchy();

        return new RoadCH(ch);
    }
}
