package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CLPolygonGeneratorTest extends PolygonGeneratorTest {

    @Test
    public void createRandomPolygons() {
        final Random random = new Random(42);

        for (int i = 0; i < 1000; i++) {
            final int numPoints = random.nextInt(497) + 3;

            final CLPolygonGenerator generator = new CLPolygonGenerator(numPoints, random);
            final Polygon randomPolygon = generator.createRandomSimplePolygon();

            assertFalse(isSelfIntersecting(randomPolygon));
        }
    }
}
