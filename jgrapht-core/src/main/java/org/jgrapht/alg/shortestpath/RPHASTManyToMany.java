package org.jgrapht.alg.shortestpath;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionHierarchy;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation.ContractionVertex;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.StopWatch;
import org.jgrapht.util.VisitedManager;

import java.util.*;

/**
 * Implementation based on RPhast by Daniel Delling, Andrew V. Goldberg, and Renote F. Werneck (Faster Batched Shortest
 * Paths in Road Networks).[ATMOS'11].
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @author Daniel Metzger
 */
public class RPHASTManyToMany<V, E> {
    private final ContractionHierarchy<V, E> ch;
    private final Graph<ContractionVertex<V>, ContractionEdge<E>> chGraph;

    private Set<V> sources;
    private List<ContractionEdge<E>> upwardsGraphEdges = new LinkedList<>();
    private VisitedManager<V> upwardsVisitedManager = new VisitedManager<>();

    private final Set<V> targets;
    private final List<ContractionEdge<E>> downwardsGraphEdges = new LinkedList<>();
    private final VisitedManager<V> downwardsVisitedManager = new VisitedManager<>();

    private Map<ContractionVertex<V>, Double> cost = new HashMap<>();
    private Map<ContractionVertex<V>, ContractionEdge<E>> predecessors = new HashMap<>();
    private Graph<V, E> graph;

    public RPHASTManyToMany(final ContractionHierarchy<V, E> ch, final Set<V> targets) {
        this.ch = ch;
        this.graph = ch.getGraph();
        this.chGraph = ch.getContractionGraph();
        this.targets = targets;

        StopWatch sw = new StopWatch("Downward Edges").start();
        prepareCHEdges(targets, downwardsGraphEdges, downwardsVisitedManager, new DownwardEdgeComparator());
        //        System.out.println(sw.stop());
    }

    private void prepareCHEdges(final Set<V> startSet, final List<ContractionEdge<E>> edges,
                                final VisitedManager visitedManager, final EdgeComparator comparator) {
        StopWatch sw = new StopWatch("exploration").start();
        final Stack<V> verticesToExplore = new Stack<>();
        verticesToExplore.addAll(startSet);
        visitedManager.visited(startSet);

        while (!verticesToExplore.empty()) {
            V vertex = verticesToExplore.pop();
            ContractionVertex<V> chVertex = getChVertex(vertex);

            for (final ContractionEdge<E> chEdge : comparator.getIncidentEdges(chVertex)) {
                if (comparator.isEdgeCorrectlyOriented(chEdge)) {
                    edges.add(chEdge);
                    V vertexToAdd = comparator.getNodeToExploreNext(chEdge);
                    if (!visitedManager.isVisited(vertexToAdd)) {
                        verticesToExplore.push(vertexToAdd);
                    }
                }
            }

            visitedManager.visited(vertex);
        }
        System.out.println(sw.stop());

        StopWatch sw2 = new StopWatch("sorting").start();
        Collections.sort(edges, comparator);
        System.out.println(sw2.stop());
    }

    private ContractionVertex<V> getChVertex(final V vertex) {
        return ch.getContractionMapping().get(vertex);
    }

    public GraphPath getPath(V source) {
        return getPaths(Collections.singletonList(source)).get(0);
    }

    public List<GraphPath<V, E>> getPaths(Collection<V> sources) {
        final LinkedList<GraphPath<V, E>> paths = new LinkedList<>();

        for (final V source : sources) {
            findUpwardsEdges(source);
            cost.put(ch.getContractionMapping().get(source), 0d);

            StopWatch sw = new StopWatch("Explore upwards").start();
            exploreGraph(upwardsGraphEdges);
            //            System.out.println(sw.stop());
            StopWatch sw2 = new StopWatch("Explore downwards").start();
            exploreGraph(downwardsGraphEdges);
            //            System.out.println(sw2.stop());

            StopWatch sw3 = new StopWatch("Backtracking").start();
            paths.addAll(backtrackPathForEachTarget(getChVertex(source)));
            //            System.out.println(sw3.stop());

            cost.clear();
            predecessors.clear();
        }

        return paths;
    }

    private void findUpwardsEdges(final V source) {
        Set<V> sourcesSet = new LinkedHashSet<>(Collections.singletonList(source));
        upwardsGraphEdges = new LinkedList<>();
        upwardsVisitedManager = new VisitedManager<>();
        UpwardsEdgeComparator comparator = new UpwardsEdgeComparator();

        StopWatch sw = new StopWatch("upwards Edges").start();
        prepareCHEdges(sourcesSet, upwardsGraphEdges, upwardsVisitedManager, comparator);
        //        System.out.println(sw.stop());
    }

    private void exploreGraph(final List<ContractionEdge<E>> edges) {
        for (final ContractionEdge<E> edge : edges) {
            try {
                updateCostsAndPredecessor(edge);
            } catch (NullPointerException noPredecessor) {
                updateCostsAndPredecessorIfNoPath(edge);
            }
        }
    }

    private void updateCostsAndPredecessor(final ContractionEdge<E> edge) {
        final ContractionVertex<V> baseVertex = chGraph.getEdgeSource(edge);
        final ContractionVertex<V> adjVertex = chGraph.getEdgeTarget(edge);

        if (cost.get(adjVertex) == null) {
            cost.put(adjVertex, Double.POSITIVE_INFINITY);
        }

        final double currentCostOfAdjVertex = cost.get(adjVertex);
        double costWithCurrentEdge = cost.get(baseVertex) + chGraph.getEdgeWeight(edge);
        if (costWithCurrentEdge < currentCostOfAdjVertex) {
            cost.put(adjVertex, costWithCurrentEdge);
            predecessors.put(adjVertex, edge);
        }
    }

    private void updateCostsAndPredecessorIfNoPath(final ContractionEdge<E> edge) {
        final ContractionVertex<V> adjVertex = chGraph.getEdgeTarget(edge);

        cost.put(adjVertex, Double.POSITIVE_INFINITY);
    }

    private List<GraphPath<V, E>> backtrackPathForEachTarget(final ContractionVertex<V> source) {
        final List<GraphPath<V, E>> paths = new LinkedList<>();

        for (final V target : targets) {
            paths.add(backtrackPath(source, target));
            paths.add(null);
        }

        return paths;
    }

    private GraphPath<V, E> backtrackPath(final ContractionVertex<V> source, final V target) {
        if (noValidPathFound(target)) {
            return null;
        } else {
            return buildPath(source, target);
        }
    }

    private boolean noValidPathFound(final V target) {
        final ContractionVertex<V> chTarget = ch.getContractionMapping().get(target);
        return cost.get(chTarget) == null || cost.get(chTarget).equals(Double.POSITIVE_INFINITY);
    }

    private GraphPath<V, E> buildPath(final ContractionVertex<V> source, final V target) {
        ContractionVertex<V> chTarget = getChVertex(target);
        final LinkedList<V> vertices = new LinkedList<>(Collections.singletonList(target));
        final LinkedList<E> edges = new LinkedList<>();

        ContractionEdge<E> currentPredecessor = predecessors.get(chTarget);
        while (currentPredecessor != null) {
            ch.unpackBackward(currentPredecessor, vertices, edges);
            ContractionVertex<V> chSourceVertex = ch.getContractionMapping().get(vertices.getFirst());
            currentPredecessor = predecessors.get(chSourceVertex);
        }

        boolean isPathLongerThanZero = edges.size() > 0;
        if (isPathLongerThanZero) {
            return new GraphWalk<>(graph, source.vertex, target, edges, cost.get(chTarget));
        } else {
            return new GraphWalk<>(graph, source.vertex, target, vertices, edges, cost.get(chTarget));
        }
    }

    private abstract class EdgeComparator implements Comparator<ContractionEdge<E>> {
        public abstract boolean isEdgeCorrectlyOriented(ContractionEdge<E> edge);

        public abstract V getNodeToExploreNext(ContractionEdge<E> edge);

        public abstract Set<ContractionEdge<E>> getIncidentEdges(final ContractionVertex<V> chVertex);
    }

    private class UpwardsEdgeComparator extends EdgeComparator {
        @Override
        public int compare(final ContractionEdge<E> edge1, final ContractionEdge<E> edge2) {
            ContractionVertex<V> edge1Target = ch.getContractionGraph().getEdgeTarget(edge1);
            ContractionVertex<V> edge2Target = ch.getContractionGraph().getEdgeTarget(edge2);

            return Integer.compare(edge1Target.contractionLevel, edge2Target.contractionLevel);
        }

        @Override
        public boolean isEdgeCorrectlyOriented(ContractionEdge<E> edge) {
            return edge.isUpward;
        }

        @Override
        public V getNodeToExploreNext(final ContractionEdge<E> edge) {
            return chGraph.getEdgeTarget(edge).vertex;
        }

        @Override
        public Set<ContractionEdge<E>> getIncidentEdges(final ContractionVertex<V> chVertex) {
            return chGraph.outgoingEdgesOf(chVertex);
        }
    }

    private class DownwardEdgeComparator extends EdgeComparator {
        final UpwardsEdgeComparator upwardsEdgeComparator = new UpwardsEdgeComparator();

        @Override
        public int compare(final ContractionEdge<E> edge1, final ContractionEdge<E> edge2) {
            int i = upwardsEdgeComparator.compare(edge1, edge2) * -1;
            return i;
        }

        @Override
        public boolean isEdgeCorrectlyOriented(ContractionEdge<E> edge) {
            return !edge.isUpward;
        }

        @Override
        public V getNodeToExploreNext(final ContractionEdge<E> edge) {
            return chGraph.getEdgeSource(edge).vertex;
        }

        @Override
        public Set<ContractionEdge<E>> getIncidentEdges(final ContractionVertex<V> chVertex) {
            return chGraph.incomingEdgesOf(chVertex);
        }
    }
}
