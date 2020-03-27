package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryHashFunctionTest {
    @Test
    public void testNonExistentObject() {
        final BinaryHashFunction<Integer> binaryHashFunction = new BinaryHashFunction<>();

        binaryHashFunction.set(0, true);

        assertFalse(binaryHashFunction.get(1));
    }

    @Test
    public void testTrue() {
        final BinaryHashFunction<Integer> binaryHashFunction = new BinaryHashFunction<>();

        binaryHashFunction.set(0, true);

        assertTrue(binaryHashFunction.get(0));
    }

    @Test
    public void testFalse() {
        final BinaryHashFunction<Integer> binaryHashFunction = new BinaryHashFunction<>();

        binaryHashFunction.set(0, false);

        assertFalse(binaryHashFunction.get(0));
    }
}
