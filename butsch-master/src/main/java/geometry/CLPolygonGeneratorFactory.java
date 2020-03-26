package geometry;

import java.util.Random;

public class CLPolygonGeneratorFactory extends AbstractPolygonGeneratorFactory {
    public CLPolygonGeneratorFactory(final Random random) {
        super(random);
    }

    @Override
    public PolygonGenerator createPolygonGenerator(final int numPoints) {
        return new CLPolygonGenerator(numPoints, random);
    }
}
