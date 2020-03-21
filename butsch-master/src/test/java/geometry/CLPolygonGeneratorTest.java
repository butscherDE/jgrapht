package geometry;

import org.junit.Test;

public class CLPolygonGeneratorTest {
    @Test
    public void lala() {
        final CLPolygonGenerator clpg = new CLPolygonGenerator(10);
        clpg.createRandomSimplePolygon();
    }
}
