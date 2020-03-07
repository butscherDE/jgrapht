package storage;

import evalutation.Config;
import geometry.PolygonGenerator;
import org.junit.Test;
import org.locationtech.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CircularPolygonExporterTest {
    @Test
    public void test() {
        final PolygonGenerator polygonGenerator = new PolygonGenerator(10);
        final Polygon[] polygons = new Polygon[10];
        for (int i = 0; i < polygons.length; i++) {
            polygons[i] = polygonGenerator.createRandomSimplePolygon();
        }

        final List<Polygon> exportData = Arrays.asList(polygons);
        final List<Polygon> reimportedPolygons = exAndReimportPolygons(exportData);

        assertEquals(Arrays.asList(polygons), reimportedPolygons);
    }

    private List<Polygon> exAndReimportPolygons(final List<Polygon> exportData) {
        final String path = exportPolygons(exportData);
        final List<Polygon> polygons = reimportExportedPolygons(path);
        cleanUp(path);
        return polygons;
    }

    private String exportPolygons(final List<Polygon> exportData) {
        String path = Config.POLYGON_PATH + "polygonstest.txt";
        try {
            CircularPolygonExporter circularPolygonExporter = new CircularPolygonExporter(path);
            circularPolygonExporter.export(exportData);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return path;
    }

    private List<Polygon> reimportExportedPolygons(final String path) {
        final List<Polygon> reimportedPolygons;
        try {
            CircularPolygonImporter circularPolygonImporter = new CircularPolygonImporter(path);
            reimportedPolygons = circularPolygonImporter.importPolygons();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            throw new IllegalStateException();
        }
        return reimportedPolygons;
    }

    private void cleanUp(final String path) {
        File file = new File(path);
        assertTrue(file.delete());
    }
}
