package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

public class PolygonSimplifierExtendedGreedyTest extends PolygonSimplifierTest {
    @Override
    Polygon getSimplifiedPolygon(final Polygon polygon, final GridIndex gridIndex) {
        final PolygonSimplifierExtendedGreedy ps = new PolygonSimplifierExtendedGreedy(gridIndex);
        return ps.simplify(polygon);
    }
}
