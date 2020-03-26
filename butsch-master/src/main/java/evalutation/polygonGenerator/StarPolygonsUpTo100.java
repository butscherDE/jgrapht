package evalutation.polygonGenerator;

import evalutation.Config;
import evalutation.polygonGenerator.utils.PolygonGeneratorStarter;
import geometry.StarPolygonGeneratorFactory;

import java.io.IOException;
import java.util.Random;

public class StarPolygonsUpTo100 {
    private final static int NUM_POLYGONS = 10000;
    private final static int MAX_POINTS = 100;
    private final static String PATH = Config.POLYGON_PATH;

    public static void main(String[] args) {
        try {
            final Random random = new Random(42);
            final StarPolygonGeneratorFactory polygonGeneratorFactory = new StarPolygonGeneratorFactory(random);
            final PolygonGeneratorStarter polygonGeneratorStarter = new PolygonGeneratorStarter(polygonGeneratorFactory);
            polygonGeneratorStarter.dump(MAX_POINTS, NUM_POLYGONS, PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
