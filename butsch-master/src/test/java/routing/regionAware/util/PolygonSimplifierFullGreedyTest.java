package routing.regionAware.util;

import util.PolygonRoutingTestGraph;

public class PolygonSimplifierFullGreedyTest extends PolygonSimplifierTest {
    public PolygonSimplifierFullGreedyTest() {
        super(new PolygonSimplifierFullGreedy(PolygonRoutingTestGraph.DEFAULT_INSTANCE.gridIndex));
    }
}
