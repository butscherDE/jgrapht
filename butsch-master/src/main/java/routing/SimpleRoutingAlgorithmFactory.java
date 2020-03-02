package routing;

import data.RoadGraph;

public abstract class SimpleRoutingAlgorithmFactory implements RoutingAlgorithmFactory {
    final RoadGraph graph;

    protected SimpleRoutingAlgorithmFactory(final RoadGraph graph) {
        this.graph = graph;
    }
}
