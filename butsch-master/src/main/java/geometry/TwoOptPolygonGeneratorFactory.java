package geometry;

public class TwoOptPolygonGeneratorFactory implements PolygonGeneratorFactory {

    @Override
    public PolygonGenerator createPolygonGenerator(final int numPoints) {
        return new TwoOptPolygonGenerator(numPoints);
    }
}
