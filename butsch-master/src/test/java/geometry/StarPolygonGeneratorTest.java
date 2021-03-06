package geometry;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StarPolygonGeneratorTest extends PolygonGeneratorTest {
    @Test
    public void sortLearningTest() {
        final int numElements = 10;
        final int[] array = new int[numElements + 1];

        for (int i = 0; i < array.length; i++) {
            array[i] = 10 - i;
        }

        Arrays.sort(array, 0, numElements);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0}, array);
    }


    @Test
    public void generateRandomPolygonsAndTestIfTheyAreReallySimple() {
        final Random random = new Random(42);
        final TwoOptPolygonGenerator twoOptPolygonGenerator = new TwoOptPolygonGenerator(getNumPoints(100), random);
        for (int i = 0; i < 1000; i++) {
            final Polygon polygon = twoOptPolygonGenerator.createRandomSimplePolygon();

            assertFalse(isSelfIntersecting(polygon));
        }
    }
}
