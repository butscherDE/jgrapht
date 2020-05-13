package index.vc;

import data.Edge;
import data.RoadGraph;
import data.Node;
import data.VisibilityCell;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.BinaryHashFunction;
import util.PolygonRoutingTestGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CellRunnerTest {
    private static PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();
    private static RoadGraph graph = GRAPH_MOCKER.graph;
    private static GeometryFactory gf = new GeometryFactory();

    @Test
    public void simpleCell17to26Left() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(38, 7),
                new Coordinate(33, 3),
                new Coordinate(32, 7),
                new Coordinate(38, 7)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);


        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 17, 26);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);

        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);

        visibilityManagerAsserts17to26Left(cti);
    }

    @Test
    public void simpleCell17to26LeftInverseEdge() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(38, 7),
                new Coordinate(33, 3),
                new Coordinate(32, 7),
                new Coordinate(38, 7)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 26, 17);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);

        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);

        visibilityManagerAsserts17to26Left(cti);
    }

    private void visibilityManagerAsserts17to26Left(CellRunnerTestInputs cti) {
        assertWalkedEdgesMarkedAsVisited17to26Left(cti);
        assertExploredButNotWalkedEdgesNotVisited17to26Left(cti);
//        assertNoViewedEdgeSettledForRightRun17to26Left(cti);
    }

    private void assertWalkedEdgesMarkedAsVisited17to26Left(CellRunnerTestInputs cti) {
        assertTrue(cti.visitedManagerDual.get(new AscendingEdge(cti.startingEdge, graph)));
        assertTrue(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(26, 18), graph)));
        assertTrue(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(18, 17), graph)));
    }

    private void assertExploredButNotWalkedEdgesNotVisited17to26Left(CellRunnerTestInputs cti) {
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(26, 35), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(18, 14), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(18, 15), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(18, 27), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(18, 100), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(18, 108), graph)));
    }

//    private void assertNoViewedEdgeSettledForRightRun17to26Left(CellRunnerTestInputs cti) {
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.startingEdge));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(26, 18)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(18, 17)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(26, 35)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(18, 14)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(18, 15)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(18, 27)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(18, 100)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledRight(cti.getEdge(18, 108)));
//    }

    @Test
    public void simpleCell17to26Right() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(32, 7),
                new Coordinate(33, 3),
                new Coordinate(25, 6),
                new Coordinate(32, 7)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 17, 26);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);

        visibilityManagerAsserts17to26Right(cti);
    }

    @Test
    public void simpleCell17to26RightInverseEdge() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(32, 7),
                new Coordinate(33, 3),
                new Coordinate(25, 6),
                new Coordinate(32, 7)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 26, 17);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);

        visibilityManagerAsserts17to26Right(cti);
    }

    private void visibilityManagerAsserts17to26Right(CellRunnerTestInputs cti) {
        assertWalkedEdgesMarkedAsVisited17to26Right(cti);
        assertExploredButNotWalkedEdgesNotVisited17to26Right(cti);
//        assertNoViewedEdgeSettledForRightRun17to26Right(cti);
    }

    private void assertWalkedEdgesMarkedAsVisited17to26Right(CellRunnerTestInputs cti) {
        assertTrue(cti.visitedManagerDual.get(new AscendingEdge(cti.startingEdge, graph)));
        assertTrue(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(26, 35), graph)));
        assertTrue(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(35, 17), graph)));
    }

    private void assertExploredButNotWalkedEdgesNotVisited17to26Right(CellRunnerTestInputs cti) {
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(17, 18), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(17, 15), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(17, 34), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(26, 18), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(35, 25), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(35, 34), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(35, 36), graph)));
        assertFalse(cti.visitedManagerDual.get(new AscendingEdge(cti.getEdge(35, 50), graph)));
    }

    private void assertNoViewedEdgeSettledForRightRun17to26Right(CellRunnerTestInputs cti) {
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.startingEdge));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(26, 35)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(35, 17)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(17, 18)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(17, 15)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(17, 34)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(26, 18)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(35, 25)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(35, 34)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(35, 36)));
//        assertFalse(cti.visitedManagerDual.isEdgeSettledLeft(cti.getEdge(35, 50)));
    }

    @Test
    public void duplicateCoordinatesTriangleStartedOnNonDuplicatedCoordinates() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(43, 15),
                new Coordinate(47, 10),
                new Coordinate(43, 11),
                new Coordinate(43, 15)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 14, 106);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);
    }

    @Test
    public void duplicateCoordinatesTriangleStartedOnAdjNodeHasDuplicate() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(47, 10),
                new Coordinate(43, 11),
                new Coordinate(43, 15),
                new Coordinate(47, 10)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 106, 110);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);
    }

    @Test
    public void duplicateCoordinatesTriangleStartedOnBothNodesHasDuplicateLeft() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(43, 15),
                new Coordinate(47, 10),
                new Coordinate(43, 11),
                new Coordinate(41, 9),
                new Coordinate(43, 11),
                new Coordinate(43, 11),
                new Coordinate(43, 15)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 109, 110);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);

        assertThrows(IllegalArgumentException.class, () -> cr.extractVisibilityCell());
        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);
    }

    @Test
    public void duplicateCoordinatesTriangleStartedOnBothNodesHasDuplicateRight() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(43, 11),
                new Coordinate(43, 11),
                new Coordinate(41, 9),
                new Coordinate(43, 11),
                new Coordinate(47, 10),
                new Coordinate(47, 3),
                new Coordinate(41, 3),
                new Coordinate(38, 7),
                new Coordinate(42, 7),
                new Coordinate(44, 5),
                new Coordinate(42, 7),
                new Coordinate(44, 9),
                new Coordinate(42, 7),
                new Coordinate(38, 7),
                new Coordinate(43, 15),
                new Coordinate(43, 11)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(GRAPH_MOCKER, 109, 110);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        assertThrows(IllegalArgumentException.class, () -> cr.extractVisibilityCell());
        final VisibilityCell vc = cr.extractVisibilityCell();
        assertEquals(expectedVc, vc);
    }

    @Test
    public void collinearEdges() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(4, 0),
                new Coordinate(3, 0),
                new Coordinate(3, -1),
                new Coordinate(5, -1),
                new Coordinate(5, 0),
                new Coordinate(4, 0),
                new Coordinate(2, 0),
                new Coordinate(4, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);


        final PolygonRoutingTestGraph customTestGraph = createCustomTestGraphToTryTrapTheAlgorithmInEndlessLoop();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 1);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);
        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph createCustomTestGraphToTryTrapTheAlgorithmInEndlessLoop() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 2, 0), //s
                new Node(1, 0, 4, 0), //v
                new Node(2, 0, 5, 0), //w
                new Node(3, -1, 5, 0),
                new Node(4, -1, 3, 0),
                new Node(5, 0, 3, 0)  //u
        };

        List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        edges.add(new Pair(3,4));
        edges.add(new Pair(4,5));
        edges.add(new Pair(5,1));

        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void startOnImpasse() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, 0),
                new Coordinate(2, 0),
                new Coordinate(1, 0),
                new Coordinate(0, 0),
                new Coordinate(1, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final PolygonRoutingTestGraph customTestGraph = createSimpleImpasseTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 1);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);
        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph createSimpleImpasseTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 0, 0),
                new Node(1, Double.MIN_VALUE, 1, 0),
                new Node(2, 0, 2, 0)
        };
        List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void startOnImpasse2() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(3, 0),
                new Coordinate(3, -1),
                new Coordinate(5, -1),
                new Coordinate(5, 0),
                new Coordinate(4, 0),
                new Coordinate(3, 0),
                new Coordinate(2, 0),
                new Coordinate(3, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final PolygonRoutingTestGraph customTestGraph = createAdvancedImpasseTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 5);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);
        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph createAdvancedImpasseTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 2, 0),
                new Node(1, 0, 4, 0),
                new Node(2, 0, 5, 0),
                new Node(3, -1, 5, 0),
                new Node(4, -1, 3, 0),
                new Node(5, 0, 3, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,5));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        edges.add(new Pair(3,4));
        edges.add(new Pair(4,5));
        edges.add(new Pair(5,1));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void twoLength1ImpassesInARow() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, 0),
                new Coordinate(2, 0),
                new Coordinate(2, -1),
                new Coordinate(2, 0),
                new Coordinate(3, 0),
                new Coordinate(2, 0),
                new Coordinate(1, 0),
                new Coordinate(0, 0),
                new Coordinate(1, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final PolygonRoutingTestGraph customTestGraph = twoImpassesInARowGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 1);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);
        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph twoImpassesInARowGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 2, 0),
                new Node(3, -1, 2, 0),
                new Node(100, -1, 2, 0),
                new Node(4, 0, 3, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        edges.add(new Pair(2,4));
        edges.add(new Pair(3,100));
        edges.add(new Pair(100,2));

        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void twoNodesSameCoordinatesButNoEdgeBetween() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, 0),
                new Coordinate(1, -1),
                new Coordinate(1, 0),
                new Coordinate(1, -1),
                new Coordinate(1, 0),
                new Coordinate(0, 0),
                new Coordinate(1, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final PolygonRoutingTestGraph customTestGraph = createTwoNodesSameCoordinatesNoEdgeTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 1);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);
        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph createTwoNodesSameCoordinatesNoEdgeTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 0, 0),
                new Node(1, 0, 1, 0),
                new Node(2, 0, 1, 0),
                new Node(3, -1, 1, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,3));
        edges.add(new Pair(2,3));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void collinearEdgeWhereNextNodeHintShallNotBeTaken() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, 0),
                new Coordinate(0, -1),
                new Coordinate(0, -2),
                new Coordinate(0, -3),
                new Coordinate(0, -2),
                new Coordinate(0, -1),
                new Coordinate(-1, 0),
                new Coordinate(1, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);

        final PolygonRoutingTestGraph customTestGraph = collinearEdgeWhereNextNodeHintShallNotBeTakenGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 2);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);

        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph collinearEdgeWhereNextNodeHintShallNotBeTakenGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, -1, 0),
                new Node(1, 0, 1, 0),
                new Node(2, -1, 0, 0),
                new Node(3, -1, 0, 0),
                new Node(4, -2, 0, 0),
                new Node(5, -3, 0, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,2));
        edges.add(new Pair(2,4));
        edges.add(new Pair(4,5));
        edges.add(new Pair(4,3));
        edges.add(new Pair(3,1));
        edges.add(new Pair(1,0));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void collinearityStackOverFlowLeft() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, -1),
                new Coordinate(0, -3),
                new Coordinate(1, -4),
                new Coordinate(1, -2),
                new Coordinate(1, -1),
                new Coordinate(1, 0),
                new Coordinate(1, -1)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);
        final PolygonRoutingTestGraph customTestGraph = collinearEdgeWithNoOtherNeighborsThanBackwardsTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 1, 2);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);

        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    @Test
    public void collinearityStackOverFlowRight() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, 0),
                new Coordinate(1, -1),
                new Coordinate(0, -3),
                new Coordinate(1, -4),
                new Coordinate(1, -3),
                new Coordinate(1, -2),
                new Coordinate(1, -1),
                new Coordinate(1, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);
        final PolygonRoutingTestGraph customTestGraph = collinearEdgeWithNoOtherNeighborsThanBackwardsTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 1, 2);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph collinearEdgeWithNoOtherNeighborsThanBackwardsTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(1, 0, 1, 0),
                new Node(2, -1, 1, 0),
                new Node(3, -2, 1, 0),
                new Node(4, -3, 1, 0),
                new Node(5, -4, 1, 0),
                new Node(6, -3, 0, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(1,2));
        edges.add(new Pair(2,3));
        edges.add(new Pair(2,6));
        edges.add(new Pair(3,4));
        edges.add(new Pair(3,5));
        edges.add(new Pair(4,5));
        edges.add(new Pair(4,6));
        edges.add(new Pair(5,6));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    @Test
    public void issueOnEdgeGermanyLeft() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(0, -1),
                new Coordinate(0, -2),
                new Coordinate(-1, -2),
                new Coordinate(0, -2),
                new Coordinate(1, -2),
                new Coordinate(0, -2),
                new Coordinate(0, -1),
                new Coordinate(1, -1),
                new Coordinate(0, -1),
                new Coordinate(0, 0),
                new Coordinate(0, -1)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);
        final PolygonRoutingTestGraph customTestGraph = issueOnEdgeGermanyTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 1);
        final CellRunner cr = new CellRunnerLeft(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsLeft);

        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    @Test
    public void issueOnEdgeGermanyRight() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(0, -1),
                new Coordinate(0, -2),
                new Coordinate(-1, -2),
                new Coordinate(0, -2),
                new Coordinate(1, -2),
                new Coordinate(0, -2),
                new Coordinate(0, -1),
                new Coordinate(1, -1),
                new Coordinate(0, -1),
                new Coordinate(0, 0)
        };
        final Polygon expectedPolygon = gf.createPolygon(coordinates);
        final VisibilityCell expectedVc = VisibilityCell.create(expectedPolygon);
        final PolygonRoutingTestGraph customTestGraph = issueOnEdgeGermanyTestGraph();

        final CellRunnerTestInputs cti = new CellRunnerTestInputs(customTestGraph, 0, 1);
        final CellRunner cr = new CellRunnerRight(cti.graph, cti.visitedManagerDual, cti.startingEdge, cti.preSortedNeighborsRight);

        final VisibilityCell vc = cr.extractVisibilityCell();

        assertEquals(expectedVc, vc);
    }

    private PolygonRoutingTestGraph issueOnEdgeGermanyTestGraph() {
        final Node[] nodes = new Node[]{
                new Node(0, 0, 0, 0),
                new Node(1, -1, 0, 0),
                new Node(2, -2, 0, 0),
                new Node(3, -2, 1, 0),
                new Node(4, -1, 1, 0),
                new Node(5, -2, 0, 0),
                new Node(6, -2, -1, 0)
        };
        final List<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair(0,1));
        edges.add(new Pair(1,2));
        edges.add(new Pair(1,4));
        edges.add(new Pair(2,3));
        edges.add(new Pair(2,5));
        edges.add(new Pair(2,6));
        return new PolygonRoutingTestGraph(nodes, edges);
    }

    public static class CellRunnerTestInputs {
        private final PolygonRoutingTestGraph graphMocker;
        public final RoadGraph graph;
        public final BinaryHashFunction<AscendingEdge> visitedManagerDual;
        public final Edge startingEdge;
        public final Map<Node, SortedNeighbors> preSortedNeighborsLeft;
        public final Map<Node, SortedNeighbors> preSortedNeighborsRight;

        public CellRunnerTestInputs(final PolygonRoutingTestGraph graphMocker, final int startBaseNode, final int startAdjNode) {
            this.graphMocker = graphMocker;
            this.graph = graphMocker.graph;
            this.visitedManagerDual = new BinaryHashFunction<>();
            this.startingEdge = getEdge(startBaseNode, startAdjNode);

            final NeighborPreSorter neighborPreSorter = new NeighborPreSorter(graph);
            this.preSortedNeighborsLeft = neighborPreSorter.getAllSortedNeighborsLeft();
            this.preSortedNeighborsRight = neighborPreSorter.getAllSortedNeighborsRight();
        }

        public Edge getEdge(final long sourceId, final long targetId) {
            return getEdge(graph.getVertex(sourceId), graph.getVertex(targetId));
        }

        public Edge getEdge(final Node source, final Node target) {
            final List<Edge> edges = graphMocker.getAllEdges();

            for (Edge edge : edges) {
                final ReflectiveEdge edgeAsReflective = new ReflectiveEdge(edge, graph);

                if (edgeAsReflective.source.equals(source) && edgeAsReflective.target.equals(target)) {
                    return edge;
                } else if (edgeAsReflective.source.equals(target) && edgeAsReflective.target.equals(source)) {
                    return edge;
                }
            }

            throw new IllegalArgumentException("Edge doesn't exist.");
        }
    }
}
