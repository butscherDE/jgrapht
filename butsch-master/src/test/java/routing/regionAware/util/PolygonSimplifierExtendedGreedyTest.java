package routing.regionAware.util;

import util.PolygonRoutingTestGraph;

public class PolygonSimplifierExtendedGreedyTest extends PolygonSimplifierTest {
    public PolygonSimplifierExtendedGreedyTest() {
        super(new PolygonSimplifierExtendedGreedy(PolygonRoutingTestGraph.DEFAULT_INSTANCE.gridIndex));
    }
}
