package routing;

import data.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyBidirectionalDijkstra;
import org.jgrapht.graph.GraphWalk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DijkstraCH implements RoutingAlgorithm{
    final RoadGraph graph;
    private final ContractionHierarchyBidirectionalDijkstra<Node, Edge> dijkstraCH;
    private final boolean enableBacktrack;

    public DijkstraCH(final RoadCH roadCH, final boolean enableBacktrack) {
        this.graph = roadCH.getGraph();
        this.dijkstraCH = new ContractionHierarchyBidirectionalDijkstra<>(roadCH.getCh(), enableBacktrack);
        this.enableBacktrack = enableBacktrack;
    }


    @Override
    public Path findPath(final Node source, final Node target) {
        GraphPath<Node, Edge> graphPath = dijkstraCH.getPath(source, target);

        graphPath = replaceWithEmptyPathIfPathNotFound(source, target, graphPath);

        return new Path(graphPath);
    }

    @Override
    public List<Path> findPaths(final Set<Node> sources, final Set<Node> targets) {
        final List<Path> paths = new ArrayList<>(sources.size() * targets.size());

        for (final Node source : sources) {
            for (final Node target : targets) {
                Path path = findPath(source, target);
                if (enableBacktrack) {
                    paths.add(path);
                }
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

    @Override
    public double getWeight(final Node source, final Node target) {
        return findPath(source, target).getWeight();
    }
}
