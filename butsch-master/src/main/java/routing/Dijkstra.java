package routing;

import data.Edge;
import data.Node;
import data.Path;
import data.RoadGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Dijkstra implements RoutingAlgorithm {
    final RoadGraph graph;
    private final DijkstraShortestPath<Node, Edge> dijkstra;

    public Dijkstra(final RoadGraph graph) {
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<>(graph);
    }

    public Path findPath(final Node source, final Node target) {
        GraphPath<Node, Edge> graphPath = dijkstra.getPath(source, target);

        graphPath = replaceWithEmptyPathIfPathNotFound(source, target, graphPath);

        return new Path(graphPath);
    }

    @Override
    public List<Path> findPaths(final Set<Node> sources, final Set<Node> targets) {
        final List<Path> paths = new ArrayList<>(sources.size() * targets.size());

        for (final Node source : sources) {
            for (final Node target : targets) {
                paths.add(findPath(source, target));
            }
        }

        return paths;
    }

    private GraphPath<Node, Edge> replaceWithEmptyPathIfPathNotFound(final Node source, final Node target,
                                                                     GraphPath<Node, Edge> graphPath) {
        graphPath = graphPath != null ? graphPath : new GraphWalk<>(graph, source, target, Collections.emptyList(),
                                                                    Double.MAX_VALUE);
        return graphPath;
    }

    public double getWeight(final Node source, final Node target) {
        return findPath(source, target).getWeight();
    }
}
