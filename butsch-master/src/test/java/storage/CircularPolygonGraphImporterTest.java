package storage;

import evalutation.Config;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CircularPolygonGraphImporterTest {
    @Test
    public void testImport() {
        String path = Config.POLYGON_PATH + "importtest.txt";

        final GeometryFactory geometryFactory = new GeometryFactory();

        final Coordinate[] coordinates0 = new Coordinate[] {new Coordinate(0.7275636800328681, 0.6832234717598454),
                                                            new Coordinate(0.6655489517945736, 0.9033722646721782),
                                                            new Coordinate(0.46365357580915334, 0.7829017787900358),
                                                            new Coordinate(0.7275636800328681, 0.6832234717598454)};
        final Polygon polygon0 = geometryFactory.createPolygon(coordinates0);

        final Coordinate[] coordinate1 = new Coordinate[] {new Coordinate(0.7512804067674601, 0.5710403484148672),
                                                           new Coordinate(0.9498601346594666, 0.8204918233863466),
                                                           new Coordinate(0.9740356814958814, 0.7134062578232291),
                                                           new Coordinate(0.7512804067674601, 0.5710403484148672)};
        final Polygon polygon1 = geometryFactory.createPolygon(coordinate1);

        try {
            final List<Polygon> polygons = new CircularPolygonImporter(path).importPolygons();

            assertEquals(polygon0, polygons.get(0));
            assertEquals(polygon1, polygons.get(1));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
