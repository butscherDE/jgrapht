package routing;

import data.Node;
import data.Path;

import java.util.List;
import java.util.Set;

public interface RoutingAlgorithm {
    Path findPath(Node source, Node target);

    List<Path> findPaths(Set<Node> sources, Set<Node> targets);

    double getWeight(Node source, Node target);
}
