package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

public class PolygonSimplifierFullGreedyTest extends PolygonSimplifierTest {
    @Override
    Polygon getSimplifiedPolygon(final Polygon polygon, final GridIndex gridIndex) {
        final PolygonSimplifierFullGreedy ps = new PolygonSimplifierFullGreedy(gridIndex);
        return ps.simplify(polygon);
    }
}
