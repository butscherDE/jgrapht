package routing;

import data.RoadCH;

public class RPHASTFactory implements RoutingAlgorithmFactory {
    final RoadCH ch;

    public RPHASTFactory(final RoadCH ch) {
        this.ch = ch;
    }

    @Override
    public RoutingAlgorithm createRoutingAlgorithm() {
        return new RPHAST(ch);
    }
}
