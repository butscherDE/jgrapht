package routing.regionAware.util;

import data.*;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import routing.RPHAST;
import util.PolygonRoutingTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RouteCandidateTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    private static Map<Pair<Node, Node>, Path> allPaths;
    private static RouteCandidate smallerSimpleCandidate;
    private static RouteCandidate greaterSimpleCandidate;
    private static RouteCandidate selfIntersectingTour;

    @BeforeAll
    public static void allToAllPaths() {
        final RoadCH ch = GRAPH_MOCKER.ch;
        final Set<Node> nodes = GRAPH_MOCKER.graph.vertexSet();

        final RPHAST rphast = new RPHAST(ch, true);
        final Set<Node> allSourceNodes = nodes;
        final Set<Node> allTargetNodes = nodes;

        allPaths = rphast.findPathsAsMap(allSourceNodes, allTargetNodes);
    }

    @BeforeEach
    public void setSmallerSimpleCandidate() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        smallerSimpleCandidate = new RouteCandidate(graph.getVertex(0), graph.getVertex(4), graph.getVertex(28),
                                                    graph.getVertex(29), allPaths);
    }

    @BeforeEach
    public void setGreaterSimpleCandidate() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        greaterSimpleCandidate = new RouteCandidate(graph.getVertex(0), graph.getVertex(4), graph.getVertex(28),
                                                    graph.getVertex(9), allPaths);
    }


    @BeforeEach
    public void setSelfIntersectingTour() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        selfIntersectingTour = new RouteCandidate(graph.getVertex(0), graph.getVertex(4), graph.getVertex(29),
                                                     graph.getVertex(28), allPaths);
    }

    @Test
    public void validateMergedPath() {
        final RoadGraph graph = GRAPH_MOCKER.graph;

        final List<Node> expectedVertexList = new LinkedList<>(
                Arrays.asList(graph.getVertex(0), graph.getVertex(1), graph.getVertex(28), graph.getVertex(29),
                              graph.getVertex(3), graph.getVertex(4)));
        final List<Node> vertexList = smallerSimpleCandidate.getMergedPath().getVertexList();

        assertEquals(expectedVertexList, vertexList);
    }

    @Test
    public void isSelfIntersecting() {
        assertTrue(selfIntersectingTour.isDetourSelfIntersecting());
    }

    @Test
    public void isNotSelfIntersecting() {
        assertFalse(smallerSimpleCandidate.isDetourSelfIntersecting());
    }

    @Test
    public void timeInRoi() {
        assertEquals(4, smallerSimpleCandidate.getTimeInROI());
    }

    @Test
    public void getTime() {

        final double expectedTime = getSummedEdgeCost(smallerSimpleCandidate.getMergedPath());

        assertEquals(expectedTime, smallerSimpleCandidate.getTime());
    }

    public double getSummedEdgeCost(final Path path) {
        final RoadGraph graph = GRAPH_MOCKER.graph;

        double edgeCostSum = 0;
        for (final Edge edge : path.getEdgeList()) {
            edgeCostSum += graph.getEdgeWeight(edge);
        }
        return edgeCostSum;
    }

    @Test
    public void getGain() {
        final double expectedMergedTime = getSummedEdgeCost(smallerSimpleCandidate.getMergedPath());
        final double expectedDirectTime = getSummedEdgeCost(smallerSimpleCandidate.directRouteStartEnd);
        final double expectedROITime = getSummedEdgeCost(smallerSimpleCandidate.regionEntryToRegionExit);
        final double expectedDetourTime = expectedMergedTime - expectedDirectTime;

        final double expectedGain = expectedROITime / (expectedDetourTime + 1);

        assertEquals(expectedGain, smallerSimpleCandidate.getGain());
    }

    @Test
    public void getDetourTime() {
        final double expectedMergedTime = getSummedEdgeCost(smallerSimpleCandidate.getMergedPath());
        final double expectedDirectTime = getSummedEdgeCost(smallerSimpleCandidate.directRouteStartEnd);

        final double expectedDetourTime = expectedMergedTime - expectedDirectTime;

        assertEquals(expectedDetourTime, smallerSimpleCandidate.getDetourTime());
    }

    @Test
    public void compareToSmaller() {
        final double expectedDifference = -1;
        final double actualDifference = smallerSimpleCandidate.compareTo(greaterSimpleCandidate);
        assertEquals(expectedDifference, actualDifference, 0);
    }

    @Test
    public void compareToEqual() {
        final double expectedDifference = 0;
        final double actualDifference = smallerSimpleCandidate.compareTo(smallerSimpleCandidate);
        assertEquals(expectedDifference, actualDifference, 0);
    }

    @Test
    public void compareToGreater() {
        final double expectedDifference = 1;
        final double actualDifference = greaterSimpleCandidate.compareTo(smallerSimpleCandidate);
        assertEquals(expectedDifference, actualDifference, 0);
    }

    @Test
    public void isLegalCandidate() {
        assertTrue(smallerSimpleCandidate.isLegalCandidate());
    }

    @Test
    public void isNotLegalCandidate() {
        final RoadGraph graph = GRAPH_MOCKER.graph;

        final Node vertex0 = graph.getVertex(0);
        final Node vertex28 = graph.getVertex(28);
        final Node vertex29 = graph.getVertex(29);
        final Node vertex4 = graph.getVertex(4);
        final HashMap<Pair<Node, Node>, Path> customPaths = getAllPathsWith0to28InvalidPath(graph, vertex0, vertex28);

        final RouteCandidate routeCandidate = new RouteCandidate(vertex0, vertex4, vertex28, vertex29, customPaths);
        assertFalse(routeCandidate.isLegalCandidate());
    }

    public HashMap<Pair<Node, Node>, Path> getAllPathsWith0to28InvalidPath(final RoadGraph graph, final Node vertex0,
                                                                           final Node vertex28) {
        final HashMap<Pair<Node, Node>, Path> customPaths = new HashMap<>(allPaths);
        final Pair<Node, Node> nodePair0and28 = new Pair<>(vertex0, vertex28);
        final Path unfoundPath = new Path(graph, vertex0, vertex28, Collections.emptyList(), Double.MAX_VALUE);
        customPaths.put(nodePair0and28, unfoundPath);
        return customPaths;
    }
}