package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionHierarchy;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionVertex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class RPHASTManyToManyTest {
    private final static Graph<Integer, DefaultWeightedEdge> graph = getGraph();
    private final static ContractionHierarchy<Integer, DefaultWeightedEdge> ch = getCh();

    private static ContractionHierarchy<Integer, DefaultWeightedEdge> getCh() {
        final Graph<Integer, DefaultWeightedEdge> graph = getGraph();
        ContractionHierarchyPrecomputation<Integer, DefaultWeightedEdge> chPrecomp = new ContractionHierarchyPrecomputation<>(
                graph, Runtime.getRuntime().availableProcessors() - 2);

        return chPrecomp.computeContractionHierarchy();
    }

    private static Graph<Integer, DefaultWeightedEdge> getGraph() {
        final Graph<Integer, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        addVertices(graph);
        addEdges(graph);

        return graph;
    }

    private static void addVertices(final Graph<Integer, DefaultWeightedEdge> graph) {
        final Integer[] vertices = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8};

        for (final Integer vertex : vertices) {
            graph.addVertex(vertex);
        }
    }

    private static void addEdges(final Graph<Integer, DefaultWeightedEdge> graph) {
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 4);
        graph.addEdge(1, 5);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 7);
        graph.addEdge(4, 7);
        graph.addEdge(5, 7);
        graph.addEdge(6, 7);
        graph.addEdge(7, 8);
    }

    @Test
    public void testSingle() {
        int source = 0;
        int target = 8;
        Set<Integer> targetSet = new LinkedHashSet<>(Collections.singletonList(target));

        final RPHASTManyToMany<Integer, DefaultWeightedEdge> rphast = new RPHASTManyToMany<>(ch, targetSet);
        final DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);

        GraphPath<Integer, DefaultWeightedEdge> dijkstraPath = dijkstra.getPath(source, target);
        GraphPath rphastPath = rphast.getPath(source);
        assertEquals(dijkstraPath, rphastPath);
    }

    @Test
    public void learningTestAreThereUpwardsEdges() {
        Graph<ContractionVertex<Integer>, ContractionEdge<DefaultWeightedEdge>> chGraph = ch.getContractionGraph();

        for (final ContractionEdge<DefaultWeightedEdge> edge : chGraph.edgeSet()) {
            System.out.println(edge.edge + ": " + edge.isUpward);
        }

        for (final ContractionVertex<Integer> vertex : chGraph.vertexSet()) {
            for (final ContractionEdge<DefaultWeightedEdge> outEdges : chGraph.outgoingEdgesOf(vertex)) {
                System.out.println(outEdges.edge +
                                   ", " +
                                   vertex.contractionLevel +
                                   "->" +
                                   chGraph.getEdgeTarget(outEdges).contractionLevel +
                                   ", " +
                                   outEdges.isUpward);
            }
        }

        for (final ContractionVertex<Integer> vertex : chGraph.vertexSet()) {
            System.out.println(vertex.vertex + ": " + vertex.contractionLevel);
        }
    }
}
