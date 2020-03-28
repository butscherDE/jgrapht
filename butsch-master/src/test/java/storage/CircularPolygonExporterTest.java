package storage;

import evalutation.Config;
import geometry.TwoOptPolygonGenerator;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CircularPolygonExporterTest {
    @Test
    public void test() {
        final Random random = new Random(42);
        final TwoOptPolygonGenerator twoOptPolygonGenerator = new TwoOptPolygonGenerator(10, random);
        final Polygon[] polygons = new Polygon[10];
        for (int i = 0; i < polygons.length; i++) {
            polygons[i] = twoOptPolygonGenerator.createRandomSimplePolygon();
        }

        final List<Polygon> exportData = Arrays.asList(polygons);
        final List<Polygon> reImportedPolygons = exAndReimportPolygons(exportData);

        assertEquals(Arrays.asList(polygons), reImportedPolygons);
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
        final List<Polygon> reImportedPolygons;
        try {
            CircularPolygonImporter circularPolygonImporter = new CircularPolygonImporter(path);
            reImportedPolygons = circularPolygonImporter.importPolygons();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            throw new IllegalStateException();
        }
        return reImportedPolygons;
    }

    private void cleanUp(final String path) {
        File file = new File(path);
        assertTrue(file.delete());
    }
}
