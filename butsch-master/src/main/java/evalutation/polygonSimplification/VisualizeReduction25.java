package evalutation.polygonSimplification;

import data.RoadGraph;
import evalutation.Config;
import evalutation.TestRegionCreator;
import evalutation.polygonGenerator.utils.PolyognGeneratorHelpers;
import geometry.BoundingBox;
import geometry.ShapeDrawer;
import index.GridIndex;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.PolygonSimplifierFullGreedy;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class VisualizeReduction25 {
    private final static int POLY_SIZE = 25;
    private final static int NUM_POLY = 25;
    private final static String PBF_PATH = Config.PBF_TUEBINGEN;

    public static void main(String[] args) {
        RoadGraph graph = getGraph();
        final BoundingBox graphBounds = BoundingBox.createFrom(graph);
        final PolygonSimplifierFullGreedy polygonSimplifier = getPolygonSimplifier(graph);

        final List<Polygon> twoOptPolygons = scaleAndTranslate(PolyognGeneratorHelpers.generateTwoOpt(NUM_POLY, POLY_SIZE), graphBounds);
        final List<Polygon> starPolygons = scaleAndTranslate(PolyognGeneratorHelpers.generateStar(NUM_POLY, POLY_SIZE), graphBounds);
        final List<Polygon> clPolygons = scaleAndTranslate(PolyognGeneratorHelpers.generateCL(NUM_POLY, POLY_SIZE), graphBounds);
        final List<Polygon> pbfPolygons = PolyognGeneratorHelpers.read(PBF_PATH, NUM_POLY, POLY_SIZE);

        System.out.println(twoOptPolygons.stream().map(p -> p.getNumPoints()).collect(Collectors.toList()));
        System.out.println(starPolygons.stream().map(p -> p.getNumPoints()).collect(Collectors.toList()));
        System.out.println(clPolygons.stream().map(p -> p.getNumPoints()).collect(Collectors.toList()));
        System.out.println(pbfPolygons.stream().map(p -> p.getNumPoints()).collect(Collectors.toList()));

        final List<Polygon> twoOptSimplified = twoOptPolygons.stream()
                .map(p -> simplify(p, polygonSimplifier))
                .collect(Collectors.toList());
        final List<Polygon> starSimplified = starPolygons.stream()
                .map(p -> simplify(p, polygonSimplifier))
                .collect(Collectors.toList());
        final List<Polygon> clSimplified = clPolygons.stream()
                .map(p -> simplify(p, polygonSimplifier))
                .collect(Collectors.toList());
        final List<Polygon> pbfSimplified = pbfPolygons.stream()
                .map(p -> simplify(p, polygonSimplifier))
                .collect(Collectors.toList());

        savePolygons(twoOptPolygons, "twoOpt", "imported");
        savePolygons(starPolygons, "star", "imported");
        savePolygons(clPolygons, "cl", "imported");
        savePolygons(pbfPolygons, "import", "imported");

        System.out.println("lala3");
        savePolygons(twoOptSimplified, "twoOpt", "simplified");
        savePolygons(starSimplified, "star", "simplified");
        savePolygons(clSimplified, "cl", "simplified");
        savePolygons(pbfSimplified, "import", "simplified");
    }

    private static RoadGraph getGraph() {
        final ImportPBF importer = new ImportPBF(PBF_PATH);
        RoadGraph graph = null;
        try {
            graph = importer.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return graph;
    }

    private static PolygonSimplifierFullGreedy getPolygonSimplifier(RoadGraph graph) {
        final GridIndex index = new GridIndex(graph, 10, 10);
        return new PolygonSimplifierFullGreedy(index);
    }

    private static List<Polygon> scaleAndTranslate(final List<Polygon> polygons, final BoundingBox graphBounds) {
        return polygons.stream()
                .map(p -> TestRegionCreator.scaleAndTranslate(p, graphBounds))
                .collect(Collectors.toList());
    }

    static int i = 0;
    public static Polygon simplify(final Polygon polygon, final PolygonSimplifierFullGreedy polygonSimplifier) {
        System.out.println(LocalDateTime.now() + ": " + ++i);
        return polygonSimplifier.simplify(polygon);
    }

    public static void savePolygons(final Collection<Polygon> polygons, final String name, final String source) {
        int c = 0;
        for (Polygon polygon : polygons) {
            saveImg(polygon, name + "_" + c++ + "_" + source);
        }
    }

    public static void saveImg(final Polygon polygon, final String fileName) {
        ShapeDrawer shapeDrawer = new ShapeDrawer(ShapeDrawer.reshapePolygon(polygon, 2000));
        shapeDrawer.save(Config.IMG_PATH3, fileName);
    }

}
