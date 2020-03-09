package index;

import data.Node;
import data.RoadGraph;
import evalutation.Config;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import storage.ImportERPGraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GridIndexTest {
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
            this.gridIndex = new GridIndex(graph, 100, 100);

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
        final Random random = new Random(42);
        final Set<Node> nodeSet = graph.vertexSet();
        final List<Node> nodeList = new ArrayList<>(nodeSet);

        for (int i = 0; i < 1000; i++) {
            final Coordinate randomCoordinate = getRandomCoordinate(random);

            double minDistance = Double.POSITIVE_INFINITY;
            Node closestNode = null;

            for (final Node node : nodeList) {
                final double distance = getDistance(node, randomCoordinate);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestNode = node;
                }
            }

            assertEquals(closestNode, gridIndex.getClosestNode(randomCoordinate.getX(), randomCoordinate.getY()));
        }
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
}
