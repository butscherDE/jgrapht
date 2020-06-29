package evalutation.polygonGenerator;

import data.NodeRelation;
import evalutation.Config;
import geometry.*;
import org.locationtech.jts.geom.Polygon;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generate some visualizations of all polygons that were generated and read in, randomly sampled
 */
public class VisualizePolygons {
    private final static int POLY_SIZE = 100;
    private final static int NUM_POLY = 100;
    private final static String PBF_PATH = Config.PBF_ANDORRA;

    public static void main(String[] args) {
        final List<Polygon> twoOptPolygons = generateTwoOpt();
        final List<Polygon> starPolygons = generateStar();
        final List<Polygon> clPolygons = generateCL();
        final List<Polygon> pbfPolygons = read();

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

    private static List<Polygon> generateTwoOpt() {
        return generate(new TwoOptPolygonGeneratorFactory(new Random(42)));
    }

    private static List<Polygon> generateStar() {
        return generate(new StarPolygonGeneratorFactory(new Random(42)));
    }

    private static List<Polygon> generateCL() {
        return generate(new CLPolygonGeneratorFactory(new Random()));
    }

    private static List<Polygon> generate(final PolygonGeneratorFactory polygonGeneratorFactory) {
        final PolygonGenerator polygonGenerator = polygonGeneratorFactory.createPolygonGenerator(POLY_SIZE);
        final List<Polygon> polygons = IntStream.rangeClosed(1, NUM_POLY)
                .mapToObj(i -> polygonGenerator.createRandomSimplePolygon())
                .collect(Collectors.toList());

        return polygons;
    }

    private static List<Polygon> read() {
        final List<NodeRelation> relations = getNodeRelations();
        final List<Polygon> closestPolygons = getNPolygonsClosestToDesiredSize(relations);
        return closestPolygons;
    }

    private static List<NodeRelation> getNodeRelations() {
        final ImportPBF importer = new ImportPBF(PBF_PATH);
        try {
            importer.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return importer.getNodeRelations();
    }

    private static List<Polygon> getNPolygonsClosestToDesiredSize(List<NodeRelation> relations) {
        return relations.stream()
                .map(r -> r.toPolygon())
                .sorted((p, q) -> {
                    final int pDistanceToDesiredSize = Math.abs(Integer.compare(p.getNumPoints(), POLY_SIZE));
                    final int qDistanceToDesiredSize = Math.abs(Integer.compare(q.getNumPoints(), POLY_SIZE));

                    return Integer.compare(pDistanceToDesiredSize, qDistanceToDesiredSize);
                })
                .limit(NUM_POLY)
                .collect(Collectors.toList());
    }
}
