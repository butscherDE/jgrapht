package evalutation.polygonGenerator;

import evalutation.Config;
import evalutation.polygonGenerator.utils.PolyognGeneratorHelpers;
import geometry.*;
import org.locationtech.jts.geom.Polygon;

import java.util.Collection;
import java.util.List;

/**
 * Generate some visualizations of all polygons that were generated and read in, randomly sampled
 */
public class VisualizePolygons {
    private final static int POLY_SIZE = 100;
    private final static int NUM_POLY = 100;
    private final static String PBF_PATH = Config.PBF_ANDORRA;

    public static void main(String[] args) {
        final List<Polygon> twoOptPolygons = PolyognGeneratorHelpers.generateTwoOpt(NUM_POLY, POLY_SIZE);
        final List<Polygon> starPolygons = PolyognGeneratorHelpers.generateStar(NUM_POLY, POLY_SIZE);
        final List<Polygon> clPolygons = PolyognGeneratorHelpers.generateCL(NUM_POLY, POLY_SIZE);
        final List<Polygon> pbfPolygons = PolyognGeneratorHelpers.read(PBF_PATH, NUM_POLY, POLY_SIZE);

        savePolygons(twoOptPolygons, "twoOpt");
        savePolygons(starPolygons, "star");
        savePolygons(clPolygons, "cl");
        savePolygons(pbfPolygons, "import");
    }

    public static void savePolygons(final Collection<Polygon> polygons, final String name) {
        int c = 0;
        for (Polygon polygon : polygons) {
            saveImg(polygon, name + "_" + c++);
        }
    }

    public static void saveImg(final Polygon polygon, final String fileName) {
        ShapeDrawer shapeDrawer = new ShapeDrawer(ShapeDrawer.reshapePolygon(polygon, 2000));
        shapeDrawer.save(Config.IMG_PATH2, fileName);
    }
}
