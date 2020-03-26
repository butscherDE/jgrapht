package geometry;

import java.util.Random;

public class StarPolygonGeneratorFactory extends AbstractPolygonGeneratorFactory {
    public StarPolygonGeneratorFactory(final Random random) {
        super(random);
    }

    @Override
    public PolygonGenerator createPolygonGenerator(final int numPoints) {
        return new StarPolygonGenerator(numPoints, random);
    }
}
