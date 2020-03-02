package routing;

import data.RoadGraph;

public class DijkstraFactorySimple extends SimpleRoutingAlgorithmFactory {
    public DijkstraFactorySimple(final RoadGraph graph) {
        super(graph);
    }

    @Override
    public RoutingAlgorithm createRoutingAlgorithm() {
        return new Dijkstra(graph);
    }
}
