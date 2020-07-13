package evalutation.imaging;

import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.Config;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.PolygonSimplifierFullGreedyExporting;
import util.PolygonRoutingTestGraph;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.List;

public class SimplificationSteps {
    public static void main(String[] args) {
        final PolygonRoutingTestGraph mocker = new PolygonRoutingTestGraph();
        final PolygonSimplifierFullGreedyExporting simplifier = new PolygonSimplifierFullGreedyExporting(
                mocker.gridIndex);
        final Polygon polygon = createPolygon();
        final List<Polygon> steps = simplifier.simplify(polygon);

        final RoadGraph subGraph = getPolygonAndEENodeSubgraph(mocker, polygon);

        writeImages(steps, subGraph);

        System.out.println("Exported " + steps.size() + " steps.");
        System.exit(0);
    }

    public static Polygon createPolygon() {
        final Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(13, 9),
                new Coordinate(12, 10),
                new Coordinate(13, 10),
                new Coordinate(13, 11),
                new Coordinate(11, 10),
                new Coordinate(13, 13),
                new Coordinate(15, 12),
                new Coordinate(10, 21),
                new Coordinate(12, 20),
                new Coordinate(13, 21),
                new Coordinate(12, 22),
                new Coordinate(11, 21),
                new Coordinate(9, 22),
                new Coordinate(12, 23),
                new Coordinate(23, 18),
                new Coordinate(28, 17),
                new Coordinate(21, 15),
                new Coordinate(21, 13),
                new Coordinate(25, 12),
                new Coordinate(22, 13),
                new Coordinate(22, 14),
                new Coordinate(23, 15),
                new Coordinate(27, 15),
                new Coordinate(25, 10),
                new Coordinate(20, 7),
                new Coordinate(13, 9),
                };
        return new GeometryFactory().createPolygon(polygonCoordinates);
    }

    public static RoadGraph getPolygonAndEENodeSubgraph(final PolygonRoutingTestGraph mocker, final Polygon polygon) {
        final RoadGraph subGraph = new RoadGraph(Edge.class);
        addSubNodes(mocker, polygon, subGraph);
        addSupEdges(mocker, polygon, subGraph);

        // For some reason this print fixes that the first image is not correctly writen
        System.out.println(subGraph.getVertex(54) != null);
        return subGraph;
    }

    public static void addSubNodes(final PolygonRoutingTestGraph mocker, final Polygon polygon,
                                   final RoadGraph subGraph) {
        mocker.graph.vertexSet().stream().filter(v -> {
            if (polygon.contains(v.getPoint())) {
                return true;
            } else {
                final long numberOfNeighborsInPolygon = mocker.graph
                        .outgoingEdgesOf(v)
                        .stream()
                        .filter(e -> polygon.contains(mocker.graph.getEdgeTarget(e).getPoint()))
                        .count();

                return numberOfNeighborsInPolygon > 0;
            }
        }).forEach(v -> subGraph.addVertex(v));
    }

    public static void addSupEdges(final PolygonRoutingTestGraph mocker, final Polygon polygon,
                                   final RoadGraph subGraph) {
        mocker.graph.edgeSet().stream().filter(e -> {
            final Node source = mocker.graph.getEdgeSource(e);
            final Node target = mocker.graph.getEdgeTarget(e);

            return polygon.contains(source.getPoint()) || polygon.contains(target.getPoint());
        }).forEach(e -> {
            final Node source = mocker.graph.getEdgeSource(e);
            final Node target = mocker.graph.getEdgeTarget(e);

            subGraph.addEdge(source, target);
        });
    }

    public static void writeImages(final List<Polygon> steps, final RoadGraph subGraph) {
        int i = 0;
        for (final Polygon step : steps) {
            final GeometryVisualizer.GeometryDrawCollection drawCol = new GeometryVisualizer.GeometryDrawCollection();
            drawCol.addGraph(Color.BLACK, subGraph);
            drawCol.addPolygon(Color.RED, step);

            final GeometryVisualizer visualizer = new GeometryVisualizer(drawCol);
            visualizer.visualizeGraph(0);
            visualizer.save(Config.SIMPLIFICATION_STEPS + "mocker_" + i++ + ".jpg");
        }
    }
}
