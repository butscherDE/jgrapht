package routing.regionAware.util;

import org.locationtech.jts.geom.Polygon;

public abstract class PolygonSimplifier {
    int contractions = 0;

    public abstract Polygon simplify(final Polygon polygon);

    public int getContractions() {
        return contractions;
    }
}
