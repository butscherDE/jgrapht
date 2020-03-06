package routing;

import data.RoadCH;

public class RPHASTFactory implements RoutingAlgorithmFactory {
    final RoadCH ch;
    private final boolean enableBacktrack;

    public RPHASTFactory(final RoadCH ch, final boolean enableBacktrack) {
        this.ch = ch;
        this.enableBacktrack = enableBacktrack;
    }

    @Override
    public RoutingAlgorithm createRoutingAlgorithm() {
        return new RPHAST(ch, enableBacktrack);
    }
}
