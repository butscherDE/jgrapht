package evalutation;

import data.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import routing.DijkstraCHFactory;
import routing.regionAware.RegionAlong;
import routing.regionAware.RegionThrough;
import storage.GeoJsonExporter;
import storage.ImportPBF;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GenerateRandomRouteImages {
    private final static int NUM_PATHS_PER_RELATION = 10;
    private final static Random RANDOM = new Random(42);
    private final static GeometryFactory gf = new GeometryFactory();

    private final static DataInstance INSTANCE = DataInstance.createFromImporter(new ImportPBF(Config.PBF_ANDORRA));
    private final static List<Node> VERTICES = new ArrayList(INSTANCE.graph.vertexSet());

    private final static String PATH = Config.ROUTE_EXAMPLES;
    private static GeoJsonExporter EXP;

    private static RoadCH roadCh;
    private static DijkstraCHFactory chFactory;

    private static NodeRelation nodeRelation;
    private static RegionThrough regionThrough;
    private static RegionAlong regionAlong;
    private static Path directPath;
    private static Path throughPath;
    private static Path alongPath;

    public static void main(String[] args) {
        final CHPreprocessing chPreprocessing = new CHPreprocessing(INSTANCE.graph);
        roadCh = chPreprocessing.createCHGraph();
        chFactory = new DijkstraCHFactory(roadCh, true);


        final List<NodeRelation> nodeRelations = INSTANCE.relations;

        for (final NodeRelation nodeRelation : nodeRelations) {
            GenerateRandomRouteImages.nodeRelation = nodeRelation;
            final RegionOfInterest roi = new RegionOfInterest(nodeRelation.toPolygon());
            try {
                regionThrough = new RegionThrough(INSTANCE.graph, roadCh, INSTANCE.index, roi);
                regionAlong = new RegionAlong(INSTANCE.graph, roadCh, INSTANCE.index, roi);
            } catch (Exception e) {
                System.err.println(nodeRelation.id + ": " + e.getMessage());
            }
            randomPaths();
        }
    }

    private static void randomPaths() {
        for (int i = 0; i < NUM_PATHS_PER_RELATION; i++) {
            final Node startNode = VERTICES.get(RANDOM.nextInt(VERTICES.size()));
            final Node endNode = VERTICES.get(RANDOM.nextInt(VERTICES.size()));

            try {
                createPaths(startNode, endNode);
                saveVis(i);
                saveGeoJson(i);
            } catch (Exception e) {
                System.err.println(nodeRelation.id + ", " + i + ": " + e.getMessage());
            }
        }
    }

    private static void createPaths(final Node startNode, final Node endNode) {
        directPath = chFactory.createRoutingAlgorithm().findPath(startNode, endNode);
        throughPath = regionThrough.findPath(startNode, endNode);
        alongPath = regionAlong.findPath(startNode, endNode);
    }

    private static void saveVis(final int i) {
        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addPolygon(Color.BLACK, nodeRelation.toPolygon());
        col.addPath(Color.GREEN, directPath, INSTANCE.graph);
        col.addPath(Color.RED, throughPath, INSTANCE.graph);
        col.addPath(Color.ORANGE, alongPath, INSTANCE.graph);
        col.addNode(Color.BLACK, INSTANCE.graph.vertexSet().iterator().next());
        final GeometryVisualizer vis = new GeometryVisualizer(col);
        vis.visualizeGraph(0);
        vis.save(PATH + nodeRelation.id + "_" + i + ".jpg", 1000, 1000);
        vis.hide();
    }

    private static void saveGeoJson(final int i) {
        EXP = new GeoJsonExporter(PATH + nodeRelation.id + "_" + i + ".geojson");
        EXP.addPolygon(nodeRelation.toPolygon());
        EXP.addLineString(toLineString(directPath));
        EXP.addLineString(toLineString(throughPath));
        EXP.addLineString(toLineString(alongPath));
        EXP.writeJson();
    }

    private static LineString toLineString(final Path path) {
        final Coordinate[] coordinates = path
                .getVertexList()
                .stream()
                .map(v -> v.getPoint().getCoordinate())
                .collect(Collectors.toList())
                .toArray(new Coordinate[path.getLength()]);
        return gf.createLineString(coordinates);
    }
}
