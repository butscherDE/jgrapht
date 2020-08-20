package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

public class PolygonSimplifierCHTest extends PolygonSimplifierTest {
    @Override
    Polygon getSimplifiedPolygon(final Polygon polygon, final GridIndex gridIndex) {
        final PolygonSimplifierCH polygonSimplifierCH = new PolygonSimplifierCH(gridIndex);
        return polygonSimplifierCH.simplify(polygon);
    }
}
