package index;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import evalutation.Config;
import geometry.BoundingBox;
import index.vc.VCNodeTranslator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import storage.ImportERPGraph;
import util.PolygonRoutingTestGraph;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GridIndexTest {
    private final static int INTENSITY = 1000;
    private final static int RND_SEED = 42;

    private static GridIndex gridIndex;
    private static RoadGraph graph;

    private static double longitudeMinBound;
    private static double longitudeMaxBound;
    private static double latitudeMinBound;
    private static double latitudeMaxBound;
    private static double longitudeRange;
    private static double latitudeRange;

    @BeforeAll
    public static void prepare() {
        try {
            graph = new ImportERPGraph(Config.ERP_PATH).createGraph();
            gridIndex = new GridIndex(graph, 10, 10);

            for (final Node node : graph.vertexSet()) {
                longitudeMinBound = Math.min(node.longitude, longitudeMinBound);
                longitudeMaxBound = Math.max(node.longitude, longitudeMaxBound);
                latitudeMinBound = Math.min(node.latitude, latitudeMinBound);
                latitudeMaxBound = Math.max(node.latitude, latitudeMaxBound);
            }

            longitudeRange = longitudeMaxBound - longitudeMinBound;
            latitudeRange = latitudeMaxBound - latitudeMinBound;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
            throw new IllegalStateException("Could not load graph");
        }
    }

    public GridIndexTest() {
    }

    @Test
    public void testRandomClosestNodes() {
        final Random random = new Random(RND_SEED);

        for (int i = 0; i < INTENSITY; i++) {
            final Coordinate randomCoordinate = getRandomCoordinate(random);

            Node closestNode = getClosestNodeSequentially(randomCoordinate);
            Node closestNodeByIndex = gridIndex.getClosestNode(randomCoordinate.getX(), randomCoordinate.getY());
            assertEquals(closestNode, closestNodeByIndex, i + "th iteration");
        }
    }

    @Test
    public void testSpecificCoordinates() {
        final double x = 48.7163;
        final double y = 9.63076;
        final int z = 313;

        final Node expectedNode = new Node(0, x, y, z);
        final Node indexNode = gridIndex.getClosestNode(x, y);
        assertEquals(expectedNode, indexNode);
    }

    private Node getClosestNodeSequentially(final Coordinate randomCoordinate) {
        final List<Node> nodeList = getNodes();

        return Collections.min(nodeList, (node1, node2) -> {
            final Coordinate coordinate1 = createCoordinate(node1);
            final Coordinate coordinate2 = createCoordinate(node2);

            final double distance1 = coordinate1.distance(randomCoordinate);
            final double distance2 = coordinate2.distance(randomCoordinate);

            return Double.compare(distance1, distance2);
        });
    }

    private List<Node> getNodes() {
        final Set<Node> nodeSet = graph.vertexSet();
        return new ArrayList<>(nodeSet);
    }

    private Coordinate getRandomCoordinate(final Random random) {
        return new Coordinate(getRandomLongitude(random), getRandomLatitude(random));
    }

    private double getRandomLongitude(final Random random) {
        return random.nextDouble() * longitudeRange + longitudeMinBound;
    }

    private double getRandomLatitude(final Random random) {
        return random.nextDouble() * latitudeRange + latitudeMinBound;
    }

    final double getDistance(final Node node, final Coordinate coordinate) {
        final Coordinate nodeCoordinate = createCoordinate(node);

        return nodeCoordinate.distance(coordinate);
    }

    private Coordinate createCoordinate(final Node node) {
        return new Coordinate(node.longitude, node.latitude);
    }


    @Test
    public void testRandomClosestEdges() {
        final Random random = new Random(RND_SEED);
        for (int i = 0; i < INTENSITY; i++) {
            final Coordinate randomCoordinate = getRandomCoordinate(random);

            Edge closestEdge = getClosestEdgeSequentially(randomCoordinate);
            Edge closestEdgeByIndex = gridIndex.getClosestEdge(randomCoordinate.getX(), randomCoordinate.getY());

            final double closestDistance = getLineSegment(closestEdge).distance(randomCoordinate);
            final double indexClosestDistance = getLineSegment(closestEdgeByIndex).distance(randomCoordinate);

            assertEquals(closestDistance, indexClosestDistance, 0.000001);
        }
    }

    private Edge getClosestEdgeSequentially(final Coordinate coordinate) {
        final List<Edge> edges = getEdges();

        return Collections.min(edges, (edge1, edge2) -> {
            final LineSegment lineSegment1 = getLineSegment(edge1);
            final LineSegment lineSegment2 = getLineSegment(edge2);

            final double distance1 = lineSegment1.distance(coordinate);
            final double distance2 = lineSegment2.distance(coordinate);

            return Double.compare(distance1, distance2);
        });
    }

    private List<Edge> getEdges() {
        return new ArrayList<>(graph.edgeSet());
    }

    private LineSegment getLineSegment(final Edge edge) {
        final Node baseNode = graph.getEdgeSource(edge);
        final Node adjNode = graph.getEdgeTarget(edge);

        final Coordinate baseCoordinate = createCoordinate(baseNode);
        final Coordinate adjCoordinate = createCoordinate(adjNode);

        return new LineSegment(baseCoordinate, adjCoordinate);
    }

    @Test
    public void queryNodes() {
        final double minLongitude = 48.725;
        final double maxLongitude = 48.775;
        final double minLatitude = 9.725;
        final double maxLatitude = 9.775;

        final List<Node> expectedNodeList = new LinkedList<>();
        for (final Node node : graph.vertexSet()) {
            if (node.id % 2 == 1 && node.longitude >= minLongitude && node.longitude <= maxLongitude && node.latitude >= minLatitude && node.latitude <= maxLatitude) {
                expectedNodeList.add(node);
            }
        }

        final BoundingBox limiter = new BoundingBox(minLongitude, maxLongitude, minLatitude, maxLatitude);
        final OddIdVisitor oddIdVisitor = new OddIdVisitor(limiter);
        gridIndex.queryNodes(limiter, oddIdVisitor);
        final List<Node> actualNodeList = oddIdVisitor.getNodes();

        Collections.sort(expectedNodeList);
        Collections.sort(actualNodeList);

        assertEquals(expectedNodeList, actualNodeList);
        assertTrue(oddIdVisitor.acceptCounts < graph.vertexSet().size());
    }

    @Test
    public void queryEdges() {
        final PolygonRoutingTestGraph graphMocker = new PolygonRoutingTestGraph();
        final Index index = new GridIndex(graphMocker.graph, 51, 25);

        final double minLongitude = 5;
        final double maxLongitude = 11;
        final double minLatitude = 9;
        final double maxLatitude = 11;
        final BoundingBox limiter = new BoundingBox(minLongitude, maxLongitude, minLatitude, maxLatitude);

        final List<Edge> expectedEdgeList = Arrays.asList(graphMocker.getEdge(19,41),
                                                          graphMocker.getEdge(41,52),
                                                          graphMocker.getEdge(41, 53),
                                                          graphMocker.getEdge(19,42),
                                                          graphMocker.getEdge(41,19),
                                                          graphMocker.getEdge(52,41),
                                                          graphMocker.getEdge(53, 41),
                                                          graphMocker.getEdge(42,19));

        final EdgeVisitor edgeVisitor = new EdgeVisitor(limiter);
        index.queryEdges(limiter, edgeVisitor);
        final List<Edge> actualEdgeList = edgeVisitor.getEdges();

        Collections.sort(expectedEdgeList, compareEdges());
        Collections.sort(actualEdgeList, compareEdges());

        for (final Edge edge : expectedEdgeList) {
            final Node sourceNode = graphMocker.graph.getEdgeSource(edge);
            final Node targetNode = graphMocker.graph.getEdgeTarget(edge);


            boolean contains = false;
            for (final Edge other : expectedEdgeList) {
                final Node otherSourceNode = graphMocker.graph.getEdgeSource(other);
                final Node otherTargetNode = graphMocker.graph.getEdgeTarget(other);
                contains |= sourceNode == otherSourceNode && targetNode == otherTargetNode;
            }

            assertTrue(contains);
        }
        assertEquals(expectedEdgeList.size(), actualEdgeList.size());
        assertTrue(edgeVisitor.acceptCounts < graph.vertexSet().size());
    }

    public Comparator<Edge> compareEdges() {
        return (a, b) -> {
            final Node aSource = graph.getEdgeSource(a);
            final Node aTarget = graph.getEdgeTarget(a);
            final Node bSource = graph.getEdgeSource(b);
            final Node bTarget = graph.getEdgeTarget(b);

            final List<Node> listA = Arrays.asList(aSource, aTarget);
            final List<Node> listB = Arrays.asList(bSource, bTarget);

            return Collections.min(listA).compareTo(Collections.min(listB));
        };
    }

    @Test
    public void queryVCs() {
        final Index gridIndex = PolygonRoutingTestGraph.DEFAULT_INSTANCE.gridIndex;
        final BoundingBox limiter = new BoundingBox(17, 21, 12, 16);

        final AllVCsLogger visitor = new AllVCsLogger();
        gridIndex.queryVisibilityCells(limiter, visitor);

        visitor.getVisibilityCells().stream().forEach(a -> System.out.println(VCNodeTranslator.getNodeIDs(a, gridIndex)));
    }

    @Test
    public void testDump() {
        final GridIndex index = getDumpIndex();

        final String expectedDump = "0.0\n" +
                                    "1.0\n" +
                                    "0.0\n" +
                                    "1.0\n" +
                                    "3\n" +
                                    "3\n" +
                                    "0|0,1;2,0|12408,12409\n" +
                                    "0|0,1;2,0|12408,12409\n" +
                                    "0|0,1;2,0|12408,12409\n" +
                                    "|0,1|\n" +
                                    "|0,1|\n" +
                                    "|0,1|\n" +
                                    "-1,1|-1,-1;0,1;1,2|\n" +
                                    "-1,1|-1,-1;0,1;1,2|\n" +
                                    "-1,1|-1,-1;0,1;1,2|";
        final String dump = index.dump().collect(Collectors.joining("\n"));

        assertEquals(expectedDump, dump);
    }

    @Test
    public void reimport() {
        final GridIndex dumpIndex = getDumpIndex();
        final Stream<String> firstDump = dumpIndex.dump();

        final Map<Long, VisibilityCell> vcMap = new HashMap<>();
        final AllVCsLogger logger = new AllVCsLogger();
        dumpIndex.queryVisibilityCells(new BoundingBox(-1, 2, -1, 2), logger);
        logger.vcs.forEach(vc -> vcMap.put(vc.id, vc));

        final List<String> dumpAsList = firstDump.collect(Collectors.toList());
        final GridIndex reimportedIndex = new GridIndex(dumpIndex.graph, dumpAsList.iterator(),
                                                        vcMap);

        assertEquals(dumpAsList, reimportedIndex.dump().collect(Collectors.toList()));
    }

    public GridIndex getDumpIndex() {
        final RoadGraph graph = new RoadGraph(Edge.class);

        final Node[] nodes = new Node[] {
                new Node(0,0,0,0),
                new Node(1,1,1,0),
                new Node(2,0,1,0)
        };
        graph.addVertex(nodes[0]);
        graph.addVertex(nodes[1]);
        graph.addVertex(nodes[2]);
        graph.addEdge(nodes[0], nodes[1]);
        graph.addEdge(nodes[1], nodes[2]);
        graph.addEdge(nodes[2], nodes[0]);
        return new GridIndex(graph,3,3);
    }

    private class OddIdVisitor implements Index.IndexVisitor<Node> {
        private final Set<Node> nodes = new LinkedHashSet<>();
        private final BoundingBox boundingBox;
        public int acceptCounts = 0;

        public OddIdVisitor(final BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
        }

        @Override
        public void accept(final Node node) {
            if (node.id % 2 == 1 &&
                node.longitude >= boundingBox.minLongitude &&
                node.longitude <= boundingBox.maxLongitude &&
                node.latitude >= boundingBox.minLatitude &&
                node.latitude <= boundingBox.maxLatitude) {
                nodes.add(node);
            }

            acceptCounts++;
        }

        public List<Node> getNodes() {
            return new LinkedList<>(nodes);
        }
    }

    private class EdgeVisitor implements Index.IndexVisitor<Edge> {
        private final Set<Edge> edges = new LinkedHashSet<>();
        public final int acceptCounts = 0;

        public EdgeVisitor(final BoundingBox boundingBox) {
        }

        @Override
        public void accept(final Edge entity) {
            edges.add(entity);
        }

        public List<Edge> getEdges() {
            return new LinkedList<>(edges);
        }
    }

    private class AllVCsLogger implements GridIndex.GridIndexVisitor {
        final Set<VisibilityCell> vcs = new LinkedHashSet<>();


        @Override
        public void accept(final Object entity, final BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(final Object entity) {
            vcs.add((VisibilityCell) entity);
        }

        public List<VisibilityCell> getVisibilityCells() {
            return new LinkedList<>(vcs);
        }
    }
}
