package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

public class PolygonSimplifierSimpleGreedyTest extends PolygonSimplifierTest {
    @Override
    Polygon getSimplifiedPolygon(final Polygon polygon, final GridIndex gridIndex) {
        final PolygonSimplifierSimpleGreedy ps = new PolygonSimplifierSimpleGreedy(gridIndex);
        return ps.simplify(polygon);
    }
}
