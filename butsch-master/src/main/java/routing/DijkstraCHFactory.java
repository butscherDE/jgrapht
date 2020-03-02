package routing;

import data.RoadCH;

public class DijkstraCHFactory implements RoutingAlgorithmFactory {
    private final RoadCH roadCH;

    public DijkstraCHFactory(final RoadCH roadCH) {
        this.roadCH = roadCH;
    }

    public RoutingAlgorithm createRoutingAlgorithm() {
        return new DijkstraCH(roadCH);
    }
}
