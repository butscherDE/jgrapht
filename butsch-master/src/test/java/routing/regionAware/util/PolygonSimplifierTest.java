package routing.regionAware.util;

import data.*;
import evalutation.Config;
import evalutation.StopWatchVerbose;
import geometry.PolygonContainsChecker;
import index.GridIndex;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.RegionAlong;
import routing.regionAware.RegionThrough;
import storage.GeoJsonExporter;
import storage.ImportPBF;
import util.PolygonRoutingTestGraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public abstract class PolygonSimplifierTest {
    final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    private static int MAX_POLYGON_SIZE = 100;
    private static RoadGraph graph;
    private static List<NodeRelation> nodeRelations;
    private static GridIndex gridIndex;
    private static RoadCH roadCH;
    private static int debugCounter = 0;

    @BeforeAll
    public static void initPbfTest() {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_ANDORRA);
        createGraph(importPBF);
        calcRelations(importPBF);
        createIndex();
        calcRoadCh();
    }

    private static void createGraph(ImportPBF importPBF) {
        graph = null;
        try {
            graph = importPBF.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    private static void calcRelations(ImportPBF importPBF) {
        nodeRelations = importPBF.getNodeRelations().stream().filter(a -> a.coordinates.length - 1 <= MAX_POLYGON_SIZE).collect(Collectors.toList());
        System.out.println("Relations.size(): " + nodeRelations.stream().map(a -> a.coordinates.length - 1).collect(Collectors.toList()));
        System.out.println("Num node relations: " + nodeRelations.size());
    }

    private static void createIndex() {
        gridIndex = new GridIndex(graph, 10, 10);
    }

    private static void calcRoadCh() {
        final ContractionHierarchyPrecomputation<Node, Edge> chPrecomp = new ContractionHierarchyPrecomputation<>(
                graph);
        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = chPrecomp.computeContractionHierarchy();
        roadCH = new RoadCH(ch);
        System.out.println("ch created");
    }


    public PolygonSimplifierTest() {

    }

    @Test
    public void simpleTestGraphCorePolygonTest() {
        Polygon polygon = SimplerPolygonContractionSetBuilderTest.getTestPolygonGeneral();

        final Polygon simplified = getSimplifiedPolygon(polygon, GRAPH_MOCKER.gridIndex);
        final PolygonContainsChecker pcc = new PolygonContainsChecker(simplified);

        for (int i = 0; i <= 53; i++) {
            final Node vertexI = GRAPH_MOCKER.graph.getVertex(i);
            final Point vertexLocation = vertexI.getPoint();
            assertFalse(pcc.contains(vertexLocation));
        }

        for (int i = 54; i < 57; i++) {
            final Node vertexI = GRAPH_MOCKER.graph.getVertex(i);
            final Point vertexLocation = vertexI.getPoint();
            assertTrue(pcc.contains(vertexLocation));
        }

        assertTrue(simplified.getNumPoints() < polygon.getNumPoints());
    }

    @Test
    public void luxembourgROIsProduceEqualRoutesOnSimplifiedPolygons() {
        final List<Node> nodes = new ArrayList<>(graph.vertexSet());
        final Random random = new Random();
        int i = 0;
        int skipped = 0;
        for (final NodeRelation nodeRelation : nodeRelations) {
            System.out.println(i++ + ", id: " + nodeRelation.id + ", size: " + (nodeRelation.coordinates.length - 1));
            final RegionOfInterest roi = new RegionOfInterest(nodeRelation.toPolygon());
            final Node source = nodes.get(random.nextInt(nodes.size()));
            final Node target = nodes.get(random.nextInt(nodes.size()));
            final RegionOfInterest simpleRoi = buildSimplifiedRoi(roi);

            try {
                buildPathsAndAssertTheyDoNotChangeBySimplification(roi, source, target, simpleRoi);
            } catch (IllegalArgumentException e) {
                skipped = checkEmptyRegion(skipped, roi, simpleRoi, e);
            }
        }

        assertAndPrintStatistics(i, skipped);
    }

    private RegionOfInterest buildSimplifiedRoi(RegionOfInterest roi) {
        final StopWatchVerbose sw = new StopWatchVerbose("Polygon Simplification");
        final Polygon simplifiedPolygon = getSimplifiedPolygon(roi.getPolygon(), gridIndex);
        sw.printTimingIfVerbose();
        return new RegionOfInterest(simplifiedPolygon);
    }

    private void buildPathsAndAssertTheyDoNotChangeBySimplification(RegionOfInterest roi, Node source, Node target, RegionOfInterest simpleRoi) {
        assertThroughPathUnchanged(roi, source, target, simpleRoi);
        assertAlongPathUnchanged(roi, source, target, simpleRoi);
    }

    private void assertThroughPathUnchanged(RegionOfInterest roi, Node source, Node target, RegionOfInterest simpleRoi) {
        final RegionThrough rt = new RegionThrough(graph, roadCH, gridIndex, roi);
        final RegionThrough rtSimple = new RegionThrough(graph, roadCH, gridIndex, simpleRoi);
        final Path greaterPath = rt.findPath(source, target);
        final Path smallerPath = rtSimple.findPath(source, target);
        assertEquals(greaterPath, smallerPath);
    }

    private void assertAlongPathUnchanged(RegionOfInterest roi, Node source, Node target, RegionOfInterest simpleRoi) {
        final RegionAlong ra = new RegionAlong(graph, roadCH, gridIndex, roi);
        final RegionAlong raSimple = new RegionAlong(graph, roadCH, gridIndex, simpleRoi);
        final Path greaterPath = ra.findPath(source, target);
        final Path smallerPath = raSimple.findPath(source, target);


        if (greaterPath.getWeight() != smallerPath.getWeight()) {
            System.err.println("Not same path costs");
            writeDebugGeoJson(roi, simpleRoi, greaterPath, smallerPath);
        }

        assertEquals(greaterPath, smallerPath);
    }

    private void writeDebugGeoJson(final RegionOfInterest roi, final RegionOfInterest simpleRoi, final Path greaterPath,
                                   final Path smallerPath) {
        final GeoJsonExporter exp = new GeoJsonExporter(Config.RESULTS + "debug" + debugCounter + ".geojson");
        exp.addGraph(graph);
        exp.addPath(greaterPath);
        exp.addPath(smallerPath);
        exp.addPolygon(roi.getPolygon());
        exp.addPolygon(simpleRoi.getPolygon());
        exp.writeJson();

        System.err.println("Wrote debug " + debugCounter++ + " geojson");
    }

    private int checkEmptyRegion(int skipped, RegionOfInterest roi, RegionOfInterest simpleRoi, IllegalArgumentException e) {
        if (e.getMessage().equals("Empty region")) {
            assertAndSkippEmptyRegion(roi, simpleRoi);
            skipped++;
        } else {
            failOnRegionEmptyAfterSimplification(e);
        }
        return skipped;
    }

    private void assertAndSkippEmptyRegion(RegionOfInterest roi, RegionOfInterest simpleRoi) {
        RoadGraph finalGraph = graph;
        assertThrows(IllegalArgumentException.class, () -> new RegionThrough(finalGraph, roadCH, gridIndex, roi));
        assertThrows(IllegalArgumentException.class, () -> new RegionThrough(finalGraph, roadCH, gridIndex, simpleRoi));
    }

    private void failOnRegionEmptyAfterSimplification(IllegalArgumentException e) {
        e.printStackTrace();
        fail();
    }

    public void assertEquals(Path expected, Path actual) {
        if (expected.isFound()) {
            Assertions.assertEquals(expected.getWeight(), actual.getWeight(), 0);
//            assetEdges(expected, actual);
        } else {
            assertFalse(actual.isFound());
        }
    }

    private void assetEdges(Path expected, Path actual) {
        final Iterator<Edge> expectedIt = expected.getEdgeList().iterator();
        final Iterator<Edge> actualIt = actual.getEdgeList().iterator();

        for (int i = 0; expectedIt.hasNext(); i++) {
            final Edge expectedNext = expectedIt.next();
            final Edge actualNext = actualIt.next();

            if (expectedNext.id != actualNext.id) {
                System.err.println(i + ") Wrong result: Edge " + actualNext.id + " should have been edge " + expectedNext.id);
                System.err.println(actual);
                fail();
            }
        }
    }

    abstract Polygon getSimplifiedPolygon(final Polygon polygon, final GridIndex gridIndex);

    private void assertAndPrintStatistics(int i, int skipped) {
        System.out.println("Checked for " + i + " Polygons, where " + skipped + " were skipped due to empty subgraphs");
        assertTrue(skipped < i);
    }
}
