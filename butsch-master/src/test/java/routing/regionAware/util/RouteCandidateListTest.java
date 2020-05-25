package routing.regionAware.util;

import data.Node;
import data.Path;
import data.RoadCH;
import data.RoadGraph;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import routing.RPHAST;
import util.PolygonRoutingTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteCandidateListTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
    private static RoadGraph GRAPH = GRAPH_MOCKER.graph;
    private static Polygon REGION = GRAPH_MOCKER.polygon;

    private final RouteCandidateList<RouteCandidate> candidateList = new RouteCandidateList<>();
    private static Map<Pair<Node, Node>, Path> allPaths;

    @BeforeAll
    public static void allToAllPaths() {
        final RoadCH ch = GRAPH_MOCKER.ch;
        final Set<Node> nodes = GRAPH_MOCKER.graph.vertexSet();

        final RPHAST rphast = new RPHAST(ch, true);
        final Set<Node> allSourceNodes = nodes;
        final Set<Node> allTargetNodes = nodes;

        allPaths = rphast.findPathsAsMap(allSourceNodes, allTargetNodes);
    }

    private void addTestingCandidates() {
        this.candidateList.clear();

        this.candidateList.add(new RouteCandidateMocker(3, 6, 1, "a", allPaths));
        this.candidateList.add(new RouteCandidateMocker(2, 3, 1, "b", allPaths));
        this.candidateList.add(new RouteCandidateMocker(6, 6, 5, "c", allPaths));
        this.candidateList.add(new RouteCandidateMocker(4, 5, 3, "d", allPaths));
        this.candidateList.add(new RouteCandidateMocker(8, 4, 6, "e", allPaths));
        this.candidateList.add(new RouteCandidateMocker(6, 1, 3, "f", allPaths));
        this.candidateList.add(new RouteCandidateMocker(7, 1, 6, "g", allPaths));
    }

    @Test
    public void assertCorrectListContentAfterPruning() {
        addTestingCandidates();
        this.candidateList.pruneDominatedCandidateRoutes();
        this.candidateList.sortByGainNonAscending();

        assertEquals("b", ((RouteCandidateMocker) this.candidateList.get(0)).name);
        assertEquals("a", ((RouteCandidateMocker) this.candidateList.get(1)).name);
        assertEquals("c", ((RouteCandidateMocker) this.candidateList.get(2)).name);
    }

    @Test
    public void assertCorrectTopThreeRoutes() {
        addTestingCandidates();
        this.candidateList.pruneDominatedCandidateRoutes();
        this.candidateList.sortByGainNonAscending();

        List<RouteCandidate> topCandidates = this.candidateList.getFirstN(3);

        assertEquals("c", ((TestPath) topCandidates.get(0).getMergedPath()).name);
        assertEquals("a", ((TestPath) topCandidates.get(1).getMergedPath()).name);
        assertEquals("b", ((TestPath) topCandidates.get(2).getMergedPath()).name);
        assertEquals(3, topCandidates.size());
    }

    @Test
    public void testIllegalStartToDetourSubpath() {
        addTestingCandidates();

        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Node startNode = graph.getVertex(0);
        final Node endNode = graph.getVertex(4);
        final Node entryNode = graph.getVertex(28);
        final Node exitNode = graph.getVertex(29);

        final Map<Pair<Node, Node>, Path> alteredPaths = getInvalidatedPathMap(graph, startNode, entryNode);
        final RouteCandidate testingCandidate = new RouteCandidate(startNode, endNode, entryNode, exitNode, alteredPaths);

        illegalCandidateNotAdded(testingCandidate);
    }

    @Test
    public void testIllegalDetourEntryToDetourExitSubpath() {
        addTestingCandidates();

        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Node startNode = graph.getVertex(0);
        final Node endNode = graph.getVertex(4);
        final Node entryNode = graph.getVertex(28);
        final Node exitNode = graph.getVertex(29);

        final Map<Pair<Node, Node>, Path> alteredPaths = getInvalidatedPathMap(graph, entryNode, exitNode);
        final RouteCandidate testingCandidate = new RouteCandidate(startNode, endNode, entryNode, exitNode, alteredPaths);

        illegalCandidateNotAdded(testingCandidate);
    }

    @Test
    public void testIllegalDetourExitToEndSubpath() {
        addTestingCandidates();

        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Node startNode = graph.getVertex(0);
        final Node endNode = graph.getVertex(4);
        final Node entryNode = graph.getVertex(28);
        final Node exitNode = graph.getVertex(29);

        final Map<Pair<Node, Node>, Path> alteredPaths = getInvalidatedPathMap(graph, exitNode, endNode);
        final RouteCandidate testingCandidate = new RouteCandidate(startNode, endNode, entryNode, exitNode, alteredPaths);

        illegalCandidateNotAdded(testingCandidate);
    }

    public Map<Pair<Node, Node>, Path> getInvalidatedPathMap(final RoadGraph graph, final Node replaceStartNode, final Node replaceEndNode) {
        final Map<Pair<Node,Node>, Path> alteredPaths = new HashMap<>(allPaths);
        alteredPaths.put(new Pair<>(replaceStartNode, replaceEndNode), new Path(graph, replaceStartNode, replaceEndNode, Collections.emptyList(), Double.MAX_VALUE));
        return alteredPaths;
    }

    private void illegalCandidateNotAdded(RouteCandidate testingCandidate) {
        final int sizeBeforeAdding = this.candidateList.size();
        this.candidateList.add(testingCandidate);
        assertEquals(sizeBeforeAdding, this.candidateList.size());
    }

    @Test
    public void selfintersectingRouteNotAlsoAdded() {
        addTestingCandidates();

        RouteCandidate selfintersectingCandidate = createSelfintersectingRouteCandidate();
        this.candidateList.add(selfintersectingCandidate);

        assertTrue(selfintersectingCandidate.isDetourSelfIntersecting());
        assertEquals(1, this.candidateList.getFirstN(1).size());
    }

    private RouteCandidate createSelfintersectingRouteCandidate() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Node startNode = graph.getVertex(0);
        final Node endNode = graph.getVertex(4);
        final Node entryNode = graph.getVertex(29);
        final Node exitNode = graph.getVertex(28);

        return new RouteCandidate(startNode, endNode, entryNode, exitNode, allPaths);
    }

    @Test
    public void getMaxGainCandidate() {
        addTestingCandidates();

        final double expectedMaxGain = 3.0;

        final RouteCandidate maxGainCandidate = candidateList.getMaxGainCandidate();
        final double actualMaxGain = maxGainCandidate.getGain();

        assertEquals(expectedMaxGain, actualMaxGain, 0);
    }

    class RouteCandidateMocker extends RouteCandidate {
        final double polygonRouteTime;
        final double roiTime;
        final double directTime;
        final String name;

        RouteCandidateMocker(final double polygonRouteTime, final double timeInRoi, final double directTime,
                             final String name, final Map<Pair<Node, Node>, Path> allPaths) {
            super(GRAPH_MOCKER.graph.getVertex(0), GRAPH_MOCKER.graph.getVertex(3), GRAPH_MOCKER.graph.getVertex(1),
                  GRAPH_MOCKER.graph.getVertex(2), allPaths);

            this.polygonRouteTime = polygonRouteTime;
            this.roiTime = timeInRoi;
            this.directTime = directTime;
            this.name = name;
        }

        @Override
        public double getTime() {
            return this.polygonRouteTime;
        }

        @Override
        public double getTimeInROI() {
            return this.roiTime;
        }

        @Override
        public double getDetourTime() {
            return this.getTime() - this.directTime;
        }

        @Override
        public String toString() {

            String sb = super.toString() + ", name: " + name;
            return sb;
        }

        @Override
        public Path getMergedPath() {
            return new TestPath(GRAPH_MOCKER.graph, this.name);
        }
    }

    private static class TestPath extends Path {
        final String name;

        TestPath(RoadGraph graph, final String name) {
            super(graph, GRAPH_MOCKER.graph.getVertex(0), GRAPH_MOCKER.graph.getVertex(0), Collections.emptyList(), 0d);
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
