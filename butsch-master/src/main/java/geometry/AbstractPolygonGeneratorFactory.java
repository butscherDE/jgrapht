package geometry;

import java.util.Random;

public abstract class AbstractPolygonGeneratorFactory implements PolygonGeneratorFactory {
    final Random random;

    protected AbstractPolygonGeneratorFactory(final Random random) {
        this.random = random;
    }
}
