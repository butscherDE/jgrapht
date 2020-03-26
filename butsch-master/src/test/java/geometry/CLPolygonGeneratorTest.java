package geometry;

import org.locationtech.jts.geom.Polygon;
import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CLPolygonGeneratorTest extends PolygonGeneratorTest {

    @Test
    public void createRandomPolygons() {
        final Random random = new Random(42);

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            final int numPoints = 5000;// random.nextInt(4997) + 3; //105);

            final CLPolygonGenerator generator = new CLPolygonGenerator(numPoints, random);
            final Polygon randomPolygon = generator.createRandomSimplePolygon();

            assertFalse(isSelfIntersecting(randomPolygon));
        }
    }
}
