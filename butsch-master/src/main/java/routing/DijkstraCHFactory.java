package routing;

import data.RoadCH;

public class DijkstraCHFactory implements RoutingAlgorithmFactory {
    private final RoadCH roadCH;
    private final boolean enableBacktrack;

    public DijkstraCHFactory(final RoadCH roadCH, final boolean enableBacktrack) {
        this.roadCH = roadCH;
        this.enableBacktrack = enableBacktrack;
    }

    public RoutingAlgorithm createRoutingAlgorithm() {
        return new DijkstraCH(roadCH, enableBacktrack);
    }
}
