package geometry;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class StarPolygonGeneratorTest {
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
}
