package routing.regionAware.util;

import data.*;
import evalutation.Config;
import geometry.PolygonContainsChecker;
import index.GridIndex;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.RegionAlong;
import routing.regionAware.RegionThrough;
import storage.ImportERPGraph;
import storage.ImportPBF;
import util.PolygonRoutingTestGraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

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
        final ImportPBF importPBF = new ImportPBF(Config.PBF_TUEBINGEN);
        RoadGraph graph = null;
        try {
            final ImportERPGraph importERPGraph = new ImportERPGraph(Config.ERP_LUXEMBOURG);
            graph = importERPGraph.createGraph();

            RoadGraph finalGraph = graph;
            AtomicLong bidirectional = new AtomicLong();
            AtomicLong unidirectional = new AtomicLong();
            graph.edgeSet().forEach(e -> {
                final Node edgeSource = finalGraph.getEdgeSource(e);
                final Node edgeTarget = finalGraph.getEdgeTarget(e);

                if (finalGraph.getEdge(edgeTarget, edgeSource) == null) {
                    unidirectional.getAndIncrement();
                } else {
                    bidirectional.getAndIncrement();
                }
            });
            System.out.println("Edgesummary");
            System.out.println(unidirectional);
            System.out.println(bidirectional);
            System.out.println(graph.edgeSet().size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        final ContractionHierarchyPrecomputation<Node, Edge> chPrecomp = new ContractionHierarchyPrecomputation<>(
                graph);
        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = chPrecomp.computeContractionHierarchy();
        final RoadCH roadCH = new RoadCH(ch);
        System.out.println("ch created");
        final GridIndex gridIndex = new GridIndex(graph, 10, 10);
        System.out.println("index created");
        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations();

        try {Thread.sleep(1000);} catch (Exception e) {};
        final List<Node> nodes = new ArrayList<>(graph.vertexSet());
        final int size = nodes.size();
        final Random random = new Random();
        int i = 0;
        for (final NodeRelation nodeRelation : nodeRelations) {
            System.out.println(i++);
            final RegionOfInterest roi = new RegionOfInterest(nodeRelation.nodes);
            final Node source = nodes.get(random.nextInt(size));
            final Node target = nodes.get(random.nextInt(size));

            final Polygon simplifiedPolygon = getSimplifiedPolygon(roi.getPolygon(), gridIndex);
            final RegionOfInterest simpleRoi = new RegionOfInterest(simplifiedPolygon);

            final RegionThrough rt = new RegionThrough(graph, roadCH, gridIndex, roi);
            final RegionThrough rtSimple = new RegionThrough(graph, roadCH, gridIndex, simpleRoi);
            final RegionAlong ra = new RegionAlong(graph, roadCH, gridIndex, roi);
            final RegionAlong raSimple = new RegionAlong(graph, roadCH, gridIndex, simpleRoi);

            assertEquals(rt.findPath(source, target), rtSimple.findPath(source, target));
            assertEquals(ra.findPath(source, target), raSimple.findPath(source, target));
        }
    }

    abstract Polygon getSimplifiedPolygon(final Polygon polygon, final GridIndex gridIndex);
}
