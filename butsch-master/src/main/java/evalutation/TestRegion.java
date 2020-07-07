package evalutation;

import org.locationtech.jts.geom.Polygon;

public class TestRegion {
    public final long id;
    public final Polygon polygon;

    public TestRegion(long id, Polygon polygon) {
        this.id = id;
        this.polygon = polygon;
    }
}
