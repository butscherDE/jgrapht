package util;

import data.*;
import index.GridIndex;
import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PolygonRoutingTestGraph {
    public static final PolygonRoutingTestGraph DEFAULT_INSTANCE = new PolygonRoutingTestGraph();

    private final Node[] nodes;
    private final List<Pair<Long,Long>> edges;
    private final Map<Long, Node> nodeMap = new HashMap<>();
    public RoadGraph graph;
    public RoadCH ch;
    public Polygon polygon;
    public GridIndex gridIndex;
    public RegionOfInterest roi;

    public PolygonRoutingTestGraph() {
        this(getDefaultNodeList(), getDefaultEdgeList());
    }

    public PolygonRoutingTestGraph(final Node[] nodes, final List<Pair<Long, Long>> edges) {
        this.nodes = nodes;
        this.edges = edges;

        this.createTestPolygon();
        this.createTestGraph();
        this.createPolygonTestGraphWithCh();
        this.createTestIndex();
    }

    private void createPolygonTestGraphWithCh() {
        ch = new CHPreprocessing(graph).createCHGraph();
    }

    private RoadGraph createPolygonTestGraph() {
        this.graph = new RoadGraph(Edge.class);

        // Exterior this.graph including to Entry / Exit nodes
        buildNodes();
        buildEdges();

        return graph;
    }

    private void buildNodes() {
        for (final Node node : nodes) {
            graph.addVertex(node);
            nodeMap.put(node.id, node);
        }
    }

    public static Node[] getDefaultNodeList() {
        return new Node[]{new Node(0, 0,25,  0),
                          new Node(1, 8, 25,  0),
                          new Node(2, 16, 25,  0),
                          new Node(3, 25,25,  0),
                          new Node(4, 34,25,  0),
                          new Node(5, 43,25,  0),
                          new Node(6, 46,25,  0),
                          new Node(7, 3,20,  0),
                          new Node(8, 8,22,  0),
                          new Node(9, 29,22,  0),
                          new Node(10, 35,21,  0),
                          new Node(11, 42,20,  0),
                          new Node(12, 34,16,  0),
                          new Node(13, 38,17,  0),
                          new Node(14, 43,15,  0),
                          new Node(15, 34,11,  0),
                          new Node(16, 38,12,  0),
                          new Node(17, 32,7,  0),
                          new Node(18, 38,7,  0),
                          new Node(19, 1,7, 0),
                          new Node(20, 1,1, 0),
                          new Node(21, 7,3, 0),
                          new Node(22, 13,2,  0),
                          new Node(23, 16,0,  0),
                          new Node(24, 21,0,  0),
                          new Node(25, 25,2,  0),
                          new Node(26, 33,3,  0),
                          new Node(27, 36,2,  0),
                          new Node(28, 16,22,  0),
                          new Node(29, 20,22,  0),
                          new Node(30, 23,21,  0),
                          new Node(31, 25,19,  0),
                          new Node(32, 30,14,  0),
                          new Node(33, 30,11,  0),
                          new Node(34, 27,7,  0),
                          new Node(35, 25,6,  0),
                          new Node(36, 22,5,  0),
                          new Node(37, 20,5,  0),
                          new Node(38, 17,5,  0),
                          new Node(39, 14,6,  0),
                          new Node(40, 11,7,  0),
                          new Node(41, 10,10,  0),
                          new Node(42, 9,13,  0),
                          new Node(43, 9,15,  0),
                          new Node(44, 10,19,  0),
                          new Node(45, 12,21,  0),
                          new Node(46, 16,17,  0),
                          new Node(47, 19,18,  0),
                          new Node(48, 22,17,  0),
                          new Node(49, 23,14,  0),
                          new Node(50, 22,11,  0),
                          new Node(51, 19,10,  0),
                          new Node(52, 16,11,  0),
                          new Node(53, 15,14,  0),
                          new Node(54, 18,15,  0),
                          new Node(55, 20,15,  0),
                          new Node(56, 20,13,  0),
                          new Node(57, 18,13,  0),
                          new Node(100, 42,7,  0),
                          new Node(101, 44,9,  0),
                          new Node(102, 44,5,  0),
                          new Node(103, 51,12,  0),
                          new Node(104, 51,10,  0),
                          new Node(105, 51,8,  0),
                          new Node(106, 47,10,  0),
                          new Node(107, 47,3,  0),
                          new Node(108, 41,3,  0),
                          new Node(109, 43,11,  0),
                          new Node(110, 43,11,  0),
                          new Node(111, 41,9,  0),

                          // Multi node test part
                          new Node(200, 28, 19, 0),
                          new Node(201, 28, 19, 0),
                          new Node(202, 31, 19,0)};
    }

    public static List<Pair<Long, Long>> getDefaultEdgeList() {
        final LinkedList<Pair<Long, Long>> edges = new LinkedList<>();
        edges.add(new Pair<>(0L, 1L));
        edges.add(new Pair<>(0L, 7L));
        edges.add(new Pair<>(0L, 19L));
        edges.add(new Pair<>(1L, 2L));
        edges.add(new Pair<>(1L, 7L));
        edges.add(new Pair<>(1L, 8L));
        edges.add(new Pair<>(1L, 45L));
        edges.add(new Pair<>(1L, 28L));
        edges.add(new Pair<>(2L, 3L));
        edges.add(new Pair<>(2L, 28L));
        edges.add(new Pair<>(2L, 29L));
        edges.add(new Pair<>(3L, 4L));
        edges.add(new Pair<>(3L, 29L));
        edges.add(new Pair<>(3L, 30L));
        edges.add(new Pair<>(3L, 9L));
        edges.add(new Pair<>(4L, 5L));
        edges.add(new Pair<>(4L, 9L));
        edges.add(new Pair<>(4L, 10L));
        edges.add(new Pair<>(4L, 13L));
        edges.add(new Pair<>(5L, 6L));
        edges.add(new Pair<>(5L, 10L));
        edges.add(new Pair<>(5L, 11L));
        edges.add(new Pair<>(5L, 13L));
        edges.add(new Pair<>(5L, 14L));
        edges.add(new Pair<>(6L, 14L));
        edges.add(new Pair<>(7L, 19L));
        edges.add(new Pair<>(7L, 43L));
        edges.add(new Pair<>(7L, 44L));
        edges.add(new Pair<>(8L, 44L));
        edges.add(new Pair<>(8L, 45L));
        edges.add(new Pair<>(9L, 30L));
        edges.add(new Pair<>(9L, 31L));
        edges.add(new Pair<>(10L, 12L));
        edges.add(new Pair<>(11L, 16L));
        edges.add(new Pair<>(12L, 13L));
        edges.add(new Pair<>(12L, 15L));
        edges.add(new Pair<>(12L, 32L));
        edges.add(new Pair<>(13L, 15L));
        edges.add(new Pair<>(13L, 16L));
        edges.add(new Pair<>(14L, 16L));
        edges.add(new Pair<>(14L, 18L));
        edges.add(new Pair<>(15L, 16L));
        edges.add(new Pair<>(15L, 17L));
        edges.add(new Pair<>(15L, 18L));
        edges.add(new Pair<>(15L, 33L));
        edges.add(new Pair<>(15L, 34L));
        edges.add(new Pair<>(17L, 18L));
        edges.add(new Pair<>(17L, 26L));
        edges.add(new Pair<>(17L, 34L));
        edges.add(new Pair<>(17L, 35L));
        edges.add(new Pair<>(18L, 26L));
        edges.add(new Pair<>(18L, 27L));
        edges.add(new Pair<>(19L, 20L));
        edges.add(new Pair<>(19L, 21L));
        edges.add(new Pair<>(19L, 41L));
        edges.add(new Pair<>(19L, 42L));
        edges.add(new Pair<>(20L, 21L));
        edges.add(new Pair<>(20L, 23L));
        edges.add(new Pair<>(21L, 22L));
        edges.add(new Pair<>(21L, 39L));
        edges.add(new Pair<>(21L, 40L));
        edges.add(new Pair<>(22L, 23L));
        edges.add(new Pair<>(22L, 25L));
        edges.add(new Pair<>(22L, 39L));
        edges.add(new Pair<>(23L, 24L));
        edges.add(new Pair<>(23L, 38L));
        edges.add(new Pair<>(24L, 25L));
        edges.add(new Pair<>(24L, 37L));
        edges.add(new Pair<>(25L, 27L));
        edges.add(new Pair<>(25L, 35L));
        edges.add(new Pair<>(25L, 36L));
        edges.add(new Pair<>(25L, 37L));
        edges.add(new Pair<>(26L, 35L));

        // Entry/Exit to Interior this.graph
        edges.add(new Pair<>(28L, 29L));
        edges.add(new Pair<>(28L, 46L));
        edges.add(new Pair<>(28L, 47L));
        edges.add(new Pair<>(29L, 30L));
        edges.add(new Pair<>(29L, 48L));
        edges.add(new Pair<>(30L, 31L));
        edges.add(new Pair<>(30L, 47L));
        edges.add(new Pair<>(30L, 48L));
        edges.add(new Pair<>(31L, 48L));
        edges.add(new Pair<>(31L, 49L));
        edges.add(new Pair<>(32L, 33L));
        edges.add(new Pair<>(32L, 49L));
        edges.add(new Pair<>(33L, 49L));
        edges.add(new Pair<>(34L, 35L));
        edges.add(new Pair<>(34L, 50L));
        edges.add(new Pair<>(35L, 36L));
        edges.add(new Pair<>(35L, 50L));
        edges.add(new Pair<>(36L, 37L));
        edges.add(new Pair<>(36L, 50L));
        edges.add(new Pair<>(37L, 38L));
        edges.add(new Pair<>(37L, 51L));
        edges.add(new Pair<>(38L, 39L));
        edges.add(new Pair<>(38L, 50L));
        edges.add(new Pair<>(39L, 40L));
        edges.add(new Pair<>(39L, 51L));
        edges.add(new Pair<>(40L, 52L));
        edges.add(new Pair<>(41L, 52L));
        edges.add(new Pair<>(41L, 53L));
        edges.add(new Pair<>(42L, 53L));
        edges.add(new Pair<>(43L, 44L));
        edges.add(new Pair<>(43L, 46L));
        edges.add(new Pair<>(43L, 53L));
        edges.add(new Pair<>(44L, 45L));
        edges.add(new Pair<>(44L, 46L));
        edges.add(new Pair<>(44L, 53L));
        edges.add(new Pair<>(45L, 46L));

        // Interior this.graph
        edges.add(new Pair<>(46L, 47L));
        edges.add(new Pair<>(46L, 53L));
        edges.add(new Pair<>(46L, 54L));
        edges.add(new Pair<>(47L, 48L));
        edges.add(new Pair<>(47L, 54L));
        edges.add(new Pair<>(47L, 55L));
        edges.add(new Pair<>(48L, 49L));
        edges.add(new Pair<>(48L, 55L));
        edges.add(new Pair<>(49L, 50L));
        edges.add(new Pair<>(49L, 55L));
        edges.add(new Pair<>(49L, 56L));
        edges.add(new Pair<>(50L, 51L));
        edges.add(new Pair<>(50L, 56L));
        edges.add(new Pair<>(51L, 52L));
        edges.add(new Pair<>(51L, 56L));
        edges.add(new Pair<>(51L, 57L));
        edges.add(new Pair<>(52L, 53L));
        edges.add(new Pair<>(52L, 57L));
        edges.add(new Pair<>(53L, 57L));
        edges.add(new Pair<>(53L, 54L));
        edges.add(new Pair<>(54L, 55L));
        edges.add(new Pair<>(54L, 56L));
        edges.add(new Pair<>(54L, 57L));
        edges.add(new Pair<>(55L, 56L));
        edges.add(new Pair<>(55L, 57L));
        edges.add(new Pair<>(56L, 57L));

        // VisibilityCellTestScenarios
        edges.add(new Pair<>(14L, 106L));
        edges.add(new Pair<>(18L, 100L));
        edges.add(new Pair<>(18L, 108L));
        edges.add(new Pair<>(100L, 101L));
        edges.add(new Pair<>(100L, 102L));
        edges.add(new Pair<>(103L, 104L));
        edges.add(new Pair<>(104L, 105L));
        edges.add(new Pair<>(104L, 106L));
        edges.add(new Pair<>(106L, 107L));
        edges.add(new Pair<>(107L, 108L));
        edges.add(new Pair<>(14L, 109L));
        edges.add(new Pair<>(109L, 110L));
        edges.add(new Pair<>(110L, 106L));
        edges.add(new Pair<>(110L, 111L));

        //Multi node test part
        edges.add(new Pair<>(31L, 200L));
        edges.add(new Pair<>(200L, 201L));
        edges.add(new Pair<>(201L, 202L));

        return edges;
    }

    private void buildEdges() {
        for (Pair<Long,Long> nodeIdPair : edges) {
            final Long sourceId = nodeIdPair.getFirst();
            final Long targetId = nodeIdPair.getSecond();
            final Node sourceNode = nodeMap.get(sourceId);
            final Node targetNode = nodeMap.get(targetId);
            final double weight = sourceNode.euclideanDistance(targetNode);

            final Edge edge = graph.addEdge(sourceNode, targetNode);
            graph.setEdgeWeight(edge, weight);

            final Edge edge2 = graph.addEdge(targetNode, sourceNode);
            graph.setEdgeWeight(edge2, weight);
        }
    }

    private Polygon createPolygon() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(14, 19),
                new Coordinate(24, 19),
                new Coordinate(24, 8),
                new Coordinate(14, 8),
                new Coordinate(14, 19)
        };
        return new GeometryFactory().createPolygon(coordinates);
    }

    private void createTestGraph() {
        this.graph = this.createPolygonTestGraph();
    }

    private void createTestPolygon() {
        this.polygon = createPolygon();
        this.roi = new RegionOfInterest(polygon);
    }

    public List<Edge> getAllEdges() {
        return new LinkedList<>(graph.edgeSet());
    }

    public Edge getEdge(final long sourceNodeId, final long targetNodeId) {
        final Node sourceNode = nodeMap.get(sourceNodeId);
        final Node targetNode = nodeMap.get(targetNodeId);
        return graph.getEdge(sourceNode, targetNode);
    }

    private boolean isEdgeEqual(int baseNode, int adjNode, Edge edge) {
        final Node edgeSourceNode = graph.getEdgeSource(edge);
        final Node edgeTargetNode = graph.getEdgeTarget(edge);
        return edgeSourceNode.id == baseNode && edgeTargetNode.id == adjNode;
    }

    private void createTestIndex() {
        gridIndex = new GridIndex(graph, 720, 360);
    }
}
