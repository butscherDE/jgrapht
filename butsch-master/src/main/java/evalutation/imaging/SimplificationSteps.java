package evalutation.imaging;

import evalutation.Config;
import evalutation.DataInstance;
import evalutation.polygonGenerator.VisualizePolygons;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.PolygonSimplifierFullGreedyExporting;
import util.PolygonRoutingTestGraph;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class SimplificationSteps {
    public static void main(String[] args) {
        final PolygonRoutingTestGraph mocker = new PolygonRoutingTestGraph();
        final PolygonSimplifierFullGreedyExporting simplifier = new PolygonSimplifierFullGreedyExporting(
                mocker.gridIndex);
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
        final Polygon polygon = new GeometryFactory().createPolygon(polygonCoordinates);
        final List<Polygon> steps = simplifier.simplify(polygon);

        int i = 0;
        for (final Polygon step : steps) {
            final GeometryVisualizer.GeometryDrawCollection drawCol = new GeometryVisualizer.GeometryDrawCollection();
            drawCol.addGraph(Color.BLACK, mocker.graph);
            drawCol.addPolygon(Color.RED, step);

            final GeometryVisualizer visualizer = new GeometryVisualizer(drawCol);
            visualizer.visualizeGraph(0);
            visualizer.save(Config.SIMPLIFICATION_STEPS + "mocker_" + i++ + ".jpg");

//            System.exit(-1);
        }

        System.out.println("Exported " + steps.size() + " steps.");
    }
}
