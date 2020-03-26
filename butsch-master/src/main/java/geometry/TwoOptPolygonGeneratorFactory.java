package geometry;

import java.util.Random;

public class TwoOptPolygonGeneratorFactory extends AbstractPolygonGeneratorFactory {
    public TwoOptPolygonGeneratorFactory(final Random random) {
        super(random);
    }

    @Override
    public PolygonGenerator createPolygonGenerator(final int numPoints) {
        return new TwoOptPolygonGenerator(numPoints, random);
    }
}
