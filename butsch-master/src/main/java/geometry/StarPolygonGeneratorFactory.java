package geometry;

public class StarPolygonGeneratorFactory implements PolygonGeneratorFactory {
    @Override
    public PolygonGenerator createPolygonGenerator(final int numPoints) {
        return new StarPolygonGenerator(numPoints);
    }
}
