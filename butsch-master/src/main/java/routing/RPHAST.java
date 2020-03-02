package routing;

import data.Edge;
import data.Node;
import data.Path;
import data.RoadCH;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.RPHASTManyToMany;
import org.jgrapht.graph.GraphWalk;

import java.util.*;

public class RPHAST implements RoutingAlgorithm {
    private final RoadCH ch;

    public RPHAST(final RoadCH ch) {
        this.ch = ch;
    }

    @Override
    public Path findPath(final Node source, final Node target) {
        return findPaths(Collections.singleton(source), Collections.singleton(target)).get(0);
    }

    @Override
    public List<Path> findPaths(final Set<Node> sources, final Set<Node> targets) {
        final RPHASTManyToMany<Node, Edge> rphast = new RPHASTManyToMany<Node, Edge>(ch.getCh(), targets);

        List<Path> pathPaths = getPaths(sources, targets, rphast);

        return pathPaths;
    }

    private List<Path> getPaths(final Set<Node> sources, final Set<Node> targets,
                                final RPHASTManyToMany<Node, Edge> rphast) {
        List<GraphPath<Node, Edge>> paths = rphast.getPaths(sources);

        List<Path> pathPaths = convertToPaths(paths, sources, targets);
        return pathPaths;
    }

    private List<Path> convertToPaths(final List<GraphPath<Node, Edge>> paths, final Set<Node> sources,
                                      final Set<Node> targets) {
        List<Path> pathPaths = new ArrayList<>(paths.size());
        final Iterator<GraphPath<Node, Edge>> pathsIterator = paths.iterator();

        for (final Node source : sources) {
            for (final Node target : targets) {
                final GraphPath<Node, Edge> path = pathsIterator.next();
                GraphPath<Node, Edge> replacedPath = replaceWithEmptyPathIfPathNotFound(path, source, target);

                pathPaths.add(new Path(replacedPath));
            }
        }

        return pathPaths;
    }

    private GraphPath<Node, Edge> replaceWithEmptyPathIfPathNotFound(GraphPath<Node, Edge> graphPath,
                                                                     final Node startNode, final Node endNode) {
        graphPath = graphPath != null ? graphPath : new GraphWalk<>(ch.getGraph(), startNode, endNode,
                                                                    Collections.emptyList(), Double.MAX_VALUE);
        return graphPath;
    }

    @Override
    public double getWeight(final Node source, final Node target) {
        return 0;
    }
}
