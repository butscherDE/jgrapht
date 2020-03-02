package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionHierarchy;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionVertex;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

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
        final Integer[] vertices = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13};

        for (final Integer vertex : vertices) {
            graph.addVertex(vertex);
        }
    }

    private static void addEdges(final Graph<Integer, DefaultWeightedEdge> graph) {
        DefaultWeightedEdge edge;
        edge = graph.addEdge(0, 1);
        graph.setEdgeWeight(edge, 1);
        edge = graph.addEdge(0, 3);
        graph.setEdgeWeight(edge, 2);
        edge = graph.addEdge(1, 2);
        graph.setEdgeWeight(edge, 3);
        edge = graph.addEdge(1, 4);
        graph.setEdgeWeight(edge, 4);
        edge = graph.addEdge(1, 5);
        graph.setEdgeWeight(edge, 5);
        edge = graph.addEdge(2, 4);
        graph.setEdgeWeight(edge, 6);
        edge = graph.addEdge(3, 4);
        graph.setEdgeWeight(edge, 7);
        edge = graph.addEdge(3, 7);
        graph.setEdgeWeight(edge, 8);
        edge = graph.addEdge(4, 7);
        graph.setEdgeWeight(edge, 9);
        edge = graph.addEdge(5, 7);
        graph.setEdgeWeight(edge, 10);
        edge = graph.addEdge(6, 7);
        graph.setEdgeWeight(edge, 11);
        edge = graph.addEdge(7, 8);
        graph.setEdgeWeight(edge, 12);

        edge = graph.addEdge(10, 11);
        graph.setEdgeWeight(edge, 13);
        edge = graph.addEdge(11, 12);
        graph.setEdgeWeight(edge, 14);
        edge = graph.addEdge(12, 13);
        graph.setEdgeWeight(edge, 15);
        edge = graph.addEdge(13, 10);
        graph.setEdgeWeight(edge, 16);
    }

    @Test
    public void testSingle() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Collections.singletonList(8);
        run(sources, targets);
    }

    @Test
    public void testSingleDisconnected() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Collections.singletonList(13);
        run(sources, targets);
    }

    @Test
    public void multiTargetConnected() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Arrays.asList(3, 5, 8);
        run(sources, targets);
    }

    @Test
    public void multiTargetDisconnected() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Arrays.asList(10, 11, 13);
        run(sources, targets);
    }

    @Test
    public void multiTargetPartiallyDisconnected() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Arrays.asList(3, 5, 8, 10, 11, 13);
        run(sources, targets);
    }

    @Test
    public void multiSourceConnected() {
        List<Integer> sources = Arrays.asList(3, 5, 8);
        List<Integer> targets = Collections.singletonList(0);
        run(sources, targets);
    }

    @Test
    public void multiSourceDisonnected() {
        List<Integer> sources = Arrays.asList(10, 11, 13);
        List<Integer> targets = Collections.singletonList(0);
        run(sources, targets);
    }

    @Test
    public void manyToManyConnected() {
        List<Integer> sources = Arrays.asList(1, 2, 3);
        List<Integer> targets = Arrays.asList(4, 5, 6);
        run(sources, targets);
    }

    @Test
    public void manyToManyDisconnected() {
        List<Integer> sources = Arrays.asList(1, 2, 3);
        List<Integer> targets = Arrays.asList(10, 11, 13);
        run(sources, targets);
    }

    @Test
    public void manyToManyPartiallyDisconnected() {
        List<Integer> sources = Arrays.asList(1, 10);
        List<Integer> targets = Arrays.asList(3, 11);
        run(sources, targets);
    }

    @Test
    public void singleEqualSourceTarget() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Collections.singletonList(0);
        run(sources, targets);
    }

    @Test
    public void multiTargetEqual() {
        List<Integer> sources = Collections.singletonList(0);
        List<Integer> targets = Arrays.asList(0, 3);
        run(sources, targets);
    }

    @Test
    public void multiSourceEqual() {
        List<Integer> sources = Arrays.asList(0, 3);
        List<Integer> targets = Collections.singletonList(0);
        run(sources, targets);
    }

    @Test
    public void sourceDoesntExist() {
        List<Integer> sources = Collections.singletonList(15);
        List<Integer> targets = Arrays.asList(3, 5, 8, 10, 11, 13);
        assertThrows(IllegalArgumentException.class, () -> {run(sources, targets);});
    }

    @Test
    public void targetDoesntExist() {
        List<Integer> sources = Arrays.asList(3, 5, 8, 10, 11, 13);
        List<Integer> targets = Collections.singletonList(15);
        assertThrows(IllegalArgumentException.class, () -> {run(sources, targets);});
    }

    private void run(final List<Integer> sources, final List<Integer> targets) {
        Set<Integer> sourceSet = new LinkedHashSet<>(sources);
        Set<Integer> targetSet = new LinkedHashSet<>(targets);

        List<GraphPath<Integer, DefaultWeightedEdge>> dijkstraPaths = getDijkstraPaths(sources, targets);
        List<GraphPath<Integer, DefaultWeightedEdge>> rphastPath = getRphastPaths(sourceSet, targetSet);
        assertPaths(dijkstraPaths, rphastPath);
    }

    private List<GraphPath<Integer, DefaultWeightedEdge>> getDijkstraPaths(final List<Integer> sources,
                                                                           final List<Integer> targets) {
        final DijkstraShortestPath<Integer, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);

        int numPaths = sources.size() * targets.size();
        List<GraphPath<Integer, DefaultWeightedEdge>> dijkstraPaths = new ArrayList<>(numPaths);
        for (final Integer source : sources) {
            for (final Integer target : targets) {
                dijkstraPaths.add(dijkstra.getPath(source, target));
            }
        }
        return dijkstraPaths;
    }

    private List<GraphPath<Integer, DefaultWeightedEdge>> getRphastPaths(final Set<Integer> sourceSet,
                                                                         final Set<Integer> targetSet) {
        final RPHASTManyToMany<Integer, DefaultWeightedEdge> rphast = new RPHASTManyToMany<>(ch, targetSet);
        return rphast.getPaths(sourceSet);
    }

    public void assertPaths(List<GraphPath<Integer, DefaultWeightedEdge>> expected,
                            List<GraphPath<Integer, DefaultWeightedEdge>> actual) {
        final Iterator<GraphPath<Integer, DefaultWeightedEdge>> expectedIt = expected.iterator();
        final Iterator<GraphPath<Integer, DefaultWeightedEdge>> actualIt = actual.iterator();

        assertEquals(expected.size(), actual.size());

        while (expectedIt.hasNext() && actualIt.hasNext()) {
            final GraphPath<Integer, DefaultWeightedEdge> expectedPath = expectedIt.next();
            final GraphPath<Integer, DefaultWeightedEdge> actualPath = actualIt.next();

            assertPath(expectedPath, actualPath);
        }
    }

    private boolean assertNoPathFound(final GraphPath<Integer, DefaultWeightedEdge> expected,
                                      final GraphPath<Integer, DefaultWeightedEdge> actual) {
        if (expected == null) {
            assertNull(actual);
            return true;
        }
        return false;
    }

    public void assertPath(GraphPath<Integer, DefaultWeightedEdge> expected,
                           GraphPath<Integer, DefaultWeightedEdge> actual) {
        if (assertNoPathFound(expected, actual)) {
            return;
        }
        assertPathLengthAndWeight(expected, actual);

        final Iterator<DefaultWeightedEdge> expectedIt = expected.getEdgeList().iterator();
        final Iterator<DefaultWeightedEdge> actualIt = actual.getEdgeList().iterator();

        while (expectedIt.hasNext() && actualIt.hasNext()) {
            final DefaultWeightedEdge expectedEdge = expectedIt.next();
            final DefaultWeightedEdge actualEdge = actualIt.next();

            assertEdge(expectedEdge, actualEdge);
        }
    }

    private void assertPathLengthAndWeight(final GraphPath<Integer, DefaultWeightedEdge> expected,
                                           final GraphPath<Integer, DefaultWeightedEdge> actual) {
        assertEquals(expected.getEdgeList().size(), actual.getEdgeList().size());
        assertEquals(expected.getWeight(), actual.getWeight(), 0);
    }

    private void assertEdge(final DefaultWeightedEdge expectedEdge, final DefaultWeightedEdge actualEdge) {
        assertEquals(graph.getEdgeSource(expectedEdge), graph.getEdgeSource(actualEdge));
        assertEquals(graph.getEdgeTarget(expectedEdge), graph.getEdgeTarget(actualEdge));
        assertEquals(graph.getEdgeWeight(expectedEdge), graph.getEdgeWeight(actualEdge), 0);
    }

    @Test
    public void learningTestAreThereUpwardsEdges() {
        Graph<ContractionVertex<Integer>, ContractionEdge<DefaultWeightedEdge>> chGraph = ch.getContractionGraph();

        for (final ContractionEdge<DefaultWeightedEdge> edge : chGraph.edgeSet()) {
            System.out.println(edge.edge + ": " + edge.isUpward);
        }

        for (final ContractionVertex<Integer> vertex : chGraph.vertexSet()) {
            for (final ContractionEdge<DefaultWeightedEdge> outEdges : chGraph.outgoingEdgesOf(vertex)) {
                System.out.println(outEdges.edge + ", " + vertex.contractionLevel + "->" + chGraph
                        .getEdgeTarget(outEdges).contractionLevel + ", " + outEdges.isUpward);
            }
        }

        for (final ContractionVertex<Integer> vertex : chGraph.vertexSet()) {
            System.out.println(vertex.vertex + ": " + vertex.contractionLevel);
        }
    }
}
