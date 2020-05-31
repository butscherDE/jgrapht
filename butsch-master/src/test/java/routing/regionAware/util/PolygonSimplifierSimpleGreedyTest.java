package routing.regionAware.util;

import util.PolygonRoutingTestGraph;

public class PolygonSimplifierSimpleGreedyTest extends PolygonSimplifierTest {
    public PolygonSimplifierSimpleGreedyTest() {
        super(new PolygonSimplifierSimpleGreedy(PolygonRoutingTestGraph.DEFAULT_INSTANCE.gridIndex));
    }
}
