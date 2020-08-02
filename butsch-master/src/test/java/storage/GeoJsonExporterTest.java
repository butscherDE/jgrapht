package storage;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeoJsonExporterTest {
    @Test
    public void lala() {
        final GeoJsonExporter exp = new GeoJsonExporter("lala");
        final GeometryFactory gf = new GeometryFactory();

        exp.addPoint(gf.createPoint(new Coordinate(-1, 1)));
        exp.addPoint(gf.createPoint(new Coordinate(-1, 2)));

        exp.addLineString(gf.createLineString(new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(1,0),
                new Coordinate(1,1),
                new Coordinate(0,1)
        }));

        exp.addPolygon(gf.createPolygon(new Coordinate[]{
                new Coordinate(2, 2),
                new Coordinate(3, 2),
                new Coordinate(3, 3),
                new Coordinate(2, 3),
                new Coordinate(2, 2)
        }));

        exp.writeJson();
    }
}
