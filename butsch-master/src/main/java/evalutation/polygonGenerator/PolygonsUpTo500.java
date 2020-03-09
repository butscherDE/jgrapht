package evalutation.polygonGenerator;

import evalutation.Config;
import evalutation.polygonGenerator.utils.PolygonGeneratorStarter;

import java.io.IOException;

public class PolygonsUpTo500 {
    private final static int NUM_POLYGONS = 1000;
    private final static int MAX_POINTS = 500;
    private final static String PATH = Config.POLYGON_PATH;

    public static void main(String[] args) {
        try {
            PolygonGeneratorStarter.dump(MAX_POINTS, NUM_POLYGONS, PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
