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
            final int numPoints = random.nextInt(5000);

            final CLPolygonGenerator generator = new CLPolygonGenerator(numPoints);
            final Polygon randomPolygon = generator.createRandomSimplePolygon();
            assertFalse(isSelfIntersecting(randomPolygon));
        }
    }
}
