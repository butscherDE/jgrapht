package routing;

import data.Edge;
import data.Node;
import data.Path;
import data.RoadCH;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.RPHASTManyToMany;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;

import java.util.*;

public class RPHAST implements RoutingAlgorithm {
    private final RoadCH ch;
    private final boolean enableBacktrack;

    public RPHAST(final RoadCH ch, final boolean enableBacktrack) {
        this.ch = ch;
        this.enableBacktrack = enableBacktrack;
    }

    @Override
    public Path findPath(final Node source, final Node target) {
        return findPaths(Collections.singleton(source), Collections.singleton(target)).get(0);
    }

    public Map<Pair<Node, Node>, Path> findPathsAsMap(final Set<Node> sources, final Set<Node> targets) {
        final List<Path> paths = findPaths(sources, targets);

        final Map<Pair<Node, Node>, Path> pathMap = new HashMap<>();
        final Iterator<Path> pathIterator = paths.iterator();
        for (final Node source : sources) {
            for (final Node target : targets) {
                final Pair<Node, Node> keyNodePair = new Pair<>(source, target);
                final Path path = pathIterator.next();
                pathMap.put(keyNodePair, path);
            }
        }

        return pathMap;
    }

    @Override
    public List<Path> findPaths(final Set<Node> sources, final Set<Node> targets) {
        final RPHASTManyToMany<Node, Edge> rphast = new RPHASTManyToMany<>(ch.getCh(), targets, enableBacktrack);

        return getPaths(sources, targets, rphast);
    }

    private List<Path> getPaths(final Set<Node> sources, final Set<Node> targets,
                                final RPHASTManyToMany<Node, Edge> rphast) {
        List<GraphPath<Node, Edge>> paths = rphast.getPaths(sources);

//        if (enableBacktrack) {
            return convertToPaths(paths, sources, targets);
//        } else {
//            return Collections.emptyList();
//        }
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
