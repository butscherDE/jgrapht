package index;

import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.Config;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import storage.ImportERPGraph;

import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GridIndexTest {
    private final static int INTENSITY = 1000;
    private final static int RND_SEED = 42;

    private final GridIndex gridIndex;
    private final RoadGraph graph;

    private final double longitudeMinBound;
    private final double longitudeMaxBound;
    private final double latitudeMinBound;
    private final double latitudeMaxBound;
    private final double longitudeRange;
    private final double latitudeRange;

    public GridIndexTest() {
        try {
            graph = new ImportERPGraph(Config.ERP_PATH).createGraph();
            this.gridIndex = new GridIndex(graph, 7200, 3600);

            double longitudeMinBound = Double.POSITIVE_INFINITY;
            double longitudeMaxBound = Double.NEGATIVE_INFINITY;
            double latitudeMinBound = Double.POSITIVE_INFINITY;
            double latitudeMaxBound = Double.NEGATIVE_INFINITY;;
            for (final Node node : graph.vertexSet()) {
                longitudeMinBound = Math.min(node.longitude, longitudeMinBound);
                longitudeMaxBound = Math.max(node.longitude, longitudeMaxBound);
                latitudeMinBound = Math.min(node.latitude, latitudeMinBound);
                latitudeMaxBound = Math.max(node.latitude, latitudeMaxBound);
            }

            this.longitudeMinBound = longitudeMinBound;
            this.longitudeMaxBound = longitudeMaxBound;
            this.latitudeMinBound = latitudeMinBound;
            this.latitudeMaxBound = latitudeMaxBound;

            this.longitudeRange = longitudeMaxBound - longitudeMinBound;
            this.latitudeRange = latitudeMaxBound - latitudeMinBound;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
            throw new IllegalStateException("Could not load graph");
        }
    }

    @Test
    public void testRandomClosestNodes() {
        final Random random = new Random(RND_SEED);

        for (int i = 0; i < INTENSITY; i++) {
            final Coordinate randomCoordinate = getRandomCoordinate(random);

            Node closestNode = getClosestNodeSequentially(randomCoordinate);
            Node closestNodeByIndex = gridIndex.getClosestNode(randomCoordinate.getX(), randomCoordinate.getY());

            assertEquals(closestNode, closestNodeByIndex);
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

        return Collections.min(nodeList, new Comparator<Node>() {
            @Override
            public int compare(final Node node1, final Node node2) {
                final Coordinate coordinate1 = createCoordinate(node1);
                final Coordinate coordinate2 = createCoordinate(node2);

                final double distance1 = coordinate1.distance(randomCoordinate);
                final double distance2 = coordinate2.distance(randomCoordinate);

                return Double.compare(distance1, distance2);
            }
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

        return Collections.min(edges, new Comparator<Edge>() {
            @Override
            public int compare(final Edge edge1, final Edge edge2) {
                final LineSegment lineSegment1 = getLineSegment(edge1);
                final LineSegment lineSegment2 = getLineSegment(edge2);

                final double distance1 = lineSegment1.distance(coordinate);
                final double distance2 = lineSegment2.distance(coordinate);

                return Double.compare(distance1, distance2);
            }
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
}
