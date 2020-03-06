package storage;

import evalutation.Config;
import geometry.PolygonGenerator;
import org.junit.Test;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CircularPolygonExporterTest {
    @Test
    public void test() {
        final PolygonGenerator polygonGenerator = new PolygonGenerator(10);
        final Polygon[] polygons = new Polygon[10];
        for (int i = 0; i < polygons.length; i++) {
            polygons[i] = polygonGenerator.createRandomSimplePolygon();
        }

        final List<Polygon> exportData = Arrays.asList(polygons);

        String path = Config.POLYGON_PATH;
        path = path.substring(0, path.length() - 4) + "test.txt";
        try {
            CircularPolygonExporter circularPolygonExportet = new CircularPolygonExporter(path);
            circularPolygonExportet.export(exportData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
