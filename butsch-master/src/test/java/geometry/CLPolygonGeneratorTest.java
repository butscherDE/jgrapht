package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.Arrays;

public class CLPolygonGeneratorTest {
    @Test
    public void lala() {
        final CLPolygonGenerator clpg = new CLPolygonGenerator(10);
        final Polygon randomSimplePolygon = clpg.createRandomSimplePolygon();

        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(randomSimplePolygon.getCoordinates()));
        final GeometryVisualizer vis = new GeometryVisualizer(col);
        vis.visualizeGraph(100000);
    }
}
