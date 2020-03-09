package evalutation.polygonGenerator.utils;

import geometry.PolygonGenerator;
import org.jgrapht.util.StopWatchGraphhopper;
import org.locationtech.jts.geom.Polygon;
import storage.CircularPolygonExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PolygonGeneratorStarter {
    public static void dump(final int maxPoints, final int numPolygons, final String path) throws IOException {
        final StopWatchGraphhopper sw = new StopWatchGraphhopper("Generated " + numPolygons + " polygons with up to " + maxPoints + " points in ").start();

        final Random random = new Random(42);
        final List<Polygon> polygons = generatePolygons(maxPoints, numPolygons, random);
        export(path + numPolygons + "_" + maxPoints + ".txt", polygons);

        System.out.println(sw.stop().toString());
    }

    private static List<Polygon> generatePolygons(final int maxPoints, final int numPolygons, final Random random) {
        final List<Polygon> polygons = new ArrayList<>(numPolygons);
        for (int i = 0; i < numPolygons; i++) {
            PolygonGenerator polygonGenerator = new PolygonGenerator(random.nextInt(maxPoints - 3) + 4);
            final Polygon polygon = polygonGenerator.createRandomSimplePolygon();
            polygons.add(polygon);

            System.out.println(i + " / " + numPolygons);
        }
        return polygons;
    }

    private static void export(final String path, final List<Polygon> polygons) throws IOException {
        final CircularPolygonExporter exporter = new CircularPolygonExporter(path);
        exporter.export(polygons);
    }
}
