package routing;

import data.Edge;
import data.Node;
import data.Path;
import data.RoadCH;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.RPHASTManyToMany;
import org.jgrapht.graph.GraphWalk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

        List<Path> pathPaths = getPaths(sources, rphast);

        return pathPaths;
    }

    private List<Path> getPaths(final Set<Node> sources, final RPHASTManyToMany<Node, Edge> rphast) {
        List<GraphPath<Node, Edge>> paths = rphast.getPaths(sources);

        List<Path> pathPaths = convertToPaths(paths);
        return pathPaths;
    }

    private List<Path> convertToPaths(final List<GraphPath<Node, Edge>> paths) {
        List<Path> pathPaths = new ArrayList<>(paths.size());

        for (final GraphPath<Node, Edge> path : paths) {
            GraphPath<Node, Edge> replacedPath = replaceWithEmptyPathIfPathNotFound(path);

            pathPaths.add(new Path(replacedPath));
        }

        return pathPaths;
    }

    private GraphPath<Node, Edge> replaceWithEmptyPathIfPathNotFound(GraphPath<Node, Edge> graphPath) {
        graphPath = graphPath != null ? graphPath : new GraphWalk<>(ch.getGraph(), graphPath.getStartVertex(), graphPath.getEndVertex(), Collections.emptyList(),
                                                                    Double.MAX_VALUE);
        return graphPath;
    }

    @Override
    public double getWeight(final Node source, final Node target) {
        return 0;
    }
}
