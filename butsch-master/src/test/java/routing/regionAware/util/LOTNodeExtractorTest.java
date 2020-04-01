package routing.regionAware.util;

import data.Node;
import data.Path;
import data.RoadGraph;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import routing.RPHAST;
import util.PolygonRoutingTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LOTNodeExtractorTest {
    private final static PolygonRoutingTestGraph graphMocker = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
    private static LOTNodeExtractor extractor;
    private static RoadGraph graph;

    @BeforeAll
    public static void createDefaultTestCase() {
        graph = graphMocker.graph;
        final Node startNode = getStartNode();
        final Node endNode = getEndNode();
        Set<Node> viaPoints = new LinkedHashSet<>(Arrays.asList(startNode, endNode));
        Set<Node> entryExitPoints = createEntryExitPoints(graph);
        Set<Node> allPoints = new LinkedHashSet<>();
        allPoints.addAll(viaPoints);
        allPoints.addAll(entryExitPoints);
        final RPHAST rphast = new RPHAST(graphMocker.ch, true);

        final Map<Pair<Node, Node>, Path> paths = rphast.findPathsAsMap(allPoints, allPoints);
        extractor = new LOTNodeExtractor(graph, startNode, endNode, entryExitPoints, paths);
    }

    private static Node getStartNode() {
        return graph.getVertex(0);
    }

    private static Node getEndNode() {
        return graph.getVertex(2);
    }

    private static Set<Node> createEntryExitPoints(final RoadGraph graph) {
        final Set<Node> entryExitPoints = new LinkedHashSet<>();

        entryExitPoints.add(graph.getVertex(28));
        entryExitPoints.add(graph.getVertex(29));
        entryExitPoints.add(graph.getVertex(30));
        entryExitPoints.add(graph.getVertex(31));

        entryExitPoints.add(graph.getVertex(43));
        entryExitPoints.add(graph.getVertex(44));
        entryExitPoints.add(graph.getVertex(45));

        return entryExitPoints;
    }

    @Test
    public void correctLotNodesForViaPoint0() {
        final List<Node> expectedLotNodesForViaPoint0 = createLotNodesForViaPoint0();
        final List<Node> lotNodesForViaPoint0 = extractor.getLotNodesFor(graph.getVertex(0));

        assertEquals(expectedLotNodesForViaPoint0, lotNodesForViaPoint0);
    }

    private List<Node> createLotNodesForViaPoint0() {
        final List<Node> lotNodesForViaPoint0 = new ArrayList<>();
        lotNodesForViaPoint0.add(graph.getVertex(28));
        lotNodesForViaPoint0.add(graph.getVertex(44));
        return lotNodesForViaPoint0;
    }

    @Test
    public void correctLotNodesForViaPoint2() {
        final List<Node> lotNodesForViaPoint2 = createLotNodesForViaPoint2();

        assertEquals(lotNodesForViaPoint2, extractor.getLotNodesFor(graph.getVertex(2)));
    }

    private List<Node> createLotNodesForViaPoint2() {
        final List<Node> lotNodesForViaPoint2 = new ArrayList<>();
        lotNodesForViaPoint2.add(graph.getVertex(28));
        lotNodesForViaPoint2.add(graph.getVertex(45));
        return lotNodesForViaPoint2;
    }

    @Test
    public void correctPathsViaPoint0() {
        final List<Node> path0To28 = new ArrayList<>(Arrays.asList(graph.getVertex(0), graph.getVertex(1),
                                                                   graph.getVertex(28)));
        final List<Node> path0To44 = new ArrayList<>(Arrays.asList(graph.getVertex(0), graph.getVertex(7),
                                                                   graph.getVertex(44)));

        assertEquals(path0To28, extractor.getLotNodePathFor(graph.getVertex(0), graph.getVertex(28)).getVertexList());
        assertEquals(path0To44, extractor.getLotNodePathFor(graph.getVertex(0), graph.getVertex(44)).getVertexList());
    }

    @Test
    public void correctPathsViaPoint2() {
        final List<Node> path2To28 = new ArrayList<>(Arrays.asList(graph.getVertex(2), graph.getVertex(28)));
        final List<Node> path2To44 = new ArrayList<>(Arrays.asList(graph.getVertex(2), graph.getVertex(1),
                                                                   graph.getVertex(45)));

        assertEquals(path2To28, extractor.getLotNodePathFor(graph.getVertex(2), graph.getVertex(28)).getVertexList());
        assertEquals(path2To44, extractor.getLotNodePathFor(graph.getVertex(2), graph.getVertex(45)).getVertexList());
    }
}
