package routing.regionAware.util;

import data.*;
import evalutation.Config;
import evalutation.StopWatchVerbose;
import geometry.PolygonContainsChecker;
import index.GridIndex;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.RegionAlong;
import routing.regionAware.RegionThrough;
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
//        fail();
        final ImportPBF importPBF = new ImportPBF(Config.PBF_ANDORRA);
        RoadGraph graph = null;
        try {
            graph = importPBF.createGraph();
            System.out.println("stats:");
            System.out.println("nodes: " + graph.vertexSet().size());
            System.out.println("edges: " + graph.edgeSet().size());
            System.out.println("polygon: " + importPBF.getNodeRelations().size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations().stream().filter(a -> a.nodes.size() <= 50).collect(Collectors.toList());
        System.out.println("Relations.size(): " + nodeRelations.stream().map(a -> a.nodes.size()).collect(Collectors.toList()));
        System.out.println("Num node relations: " + nodeRelations.size());
        final GridIndex gridIndex = new GridIndex(graph, 10, 10);
        final ContractionHierarchyPrecomputation<Node, Edge> chPrecomp = new ContractionHierarchyPrecomputation<>(
                graph);
        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = chPrecomp.computeContractionHierarchy();
        final RoadCH roadCH = new RoadCH(ch);
        System.out.println("ch created");

        final List<Node> nodes = new ArrayList<>(graph.vertexSet());
        final int size = nodes.size();
        final Random random = new Random();
        int i = 0;
        int skipped = 0;
        for (final NodeRelation nodeRelation : nodeRelations) {
            System.out.println(i++ + ", id: " + nodeRelation.id + ", size: " + nodeRelation.nodes.size());
            final RegionOfInterest roi = new RegionOfInterest(nodeRelation.nodes);
            final Node source = nodes.get(random.nextInt(size));
            final Node target = nodes.get(random.nextInt(size));

            final StopWatchVerbose sw = new StopWatchVerbose("Polygon Simplification");
            final Polygon simplifiedPolygon = getSimplifiedPolygon(roi.getPolygon(), gridIndex);
            sw.printTimingIfVerbose();
            final RegionOfInterest simpleRoi = new RegionOfInterest(simplifiedPolygon);

            try {
                final RegionThrough rt = new RegionThrough(graph, roadCH, gridIndex, roi);
                final RegionThrough rtSimple = new RegionThrough(graph, roadCH, gridIndex, simpleRoi);
//            final RegionAlong ra = new RegionAlong(graph, roadCH, gridIndex, roi);
//            final RegionAlong raSimple = new RegionAlong(graph, roadCH, gridIndex, simpleRoi);

                final Path path = rt.findPath(source, target);
                final Path path1 = rtSimple.findPath(source, target);
                System.out.println(path);
                System.out.println(path1);
                assertEquals(path, path1);
//            assertEquals(ra.findPath(source, target), raSimple.findPath(source, target));
            } catch (IllegalArgumentException e) {
                if (e.getMessage().equals("Empty region")) {
                    System.out.println("skipped due to empty region");
                    skipped++;
                } else {
                    e.printStackTrace();
                    fail();
                }
            }
        }
        System.out.println("Checked for " + i + " Polygons.");

        assertTrue(skipped < i);
    }

    public void assertEquals(Path expected, Path actual) {
        System.out.println("tkkg");
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
}
