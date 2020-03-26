package geometry;

import org.junit.Test;
import org.locationtech.jts.geom.Polygon;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CLPolygonGeneratorTest extends PolygonGeneratorTest {

    @Test
    public void createRandomPolygons() {
        final Random random = new Random(42);

        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            final int numPoints = random.nextInt(500); //105);

            final CLPolygonGenerator generator = new CLPolygonGenerator(numPoints);
            final Polygon randomPolygon = generator.createRandomSimplePolygon();

//            final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
//            col.addLineSegmentsFromCoordinates(Color.BLACK, Arrays.asList(randomPolygon.getCoordinates()));
//            final GeometryVisualizer vis = new GeometryVisualizer(col);
//            vis.visualizeGraph(100_000);

            assertFalse(isSelfIntersecting(randomPolygon));
        }
    }
}
