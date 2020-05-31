package routing.regionAware.util;

import org.locationtech.jts.geom.Polygon;

public interface PolygonSimplifier {
    Polygon simplify(final Polygon polygon);
}
