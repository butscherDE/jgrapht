package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

import static org.junit.Assert.assertFalse;

public class TwoOptPolygonGeneratorTest extends PolygonGeneratorTest {
    private Random random  = new Random(42);

    @Test
    public void testSimplePolygon() {
        final Coordinate[] coordinatesInNonSimpleOrder = new Coordinate[] {new Coordinate(0,0),
                                                                           new Coordinate(100, 100),
                                                                           new Coordinate(0, 100),
                                                                           new Coordinate(100,0),
                                                                           new Coordinate(0,0)};

        final Polygon simplifiedPolygon = new TwoOptPolygonGenerator(10).createSimplePolygon(coordinatesInNonSimpleOrder);
        assertFalse(isSelfIntersecting(simplifiedPolygon));
    }


    @Test
    public void generateRandomPolygonsAndTestIfTheyAreReallySimple() {
        final TwoOptPolygonGenerator twoOptPolygonGenerator = new TwoOptPolygonGenerator(getNumPoints(100));
        for (int i = 0; i < 1000; i++) {
            final Polygon polygon = twoOptPolygonGenerator.createRandomSimplePolygon();

            assertFalse(isSelfIntersecting(polygon));
        }
    }
}
