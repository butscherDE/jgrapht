package evalutation.polygonGenerator.utils;

import data.NodeRelation;
import geometry.*;
import org.locationtech.jts.geom.Polygon;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PolyognGeneratorHelpers {
    public static List<Polygon> generateTwoOpt(int numPoly, int polySize) {
        return generate(new TwoOptPolygonGeneratorFactory(new Random(42)), numPoly, polySize);
    }

    public static List<Polygon> generateStar(int numPoly, int polySize) {
        return generate(new StarPolygonGeneratorFactory(new Random(42)), numPoly, polySize);
    }

    public static List<Polygon> generateCL(int numPoly, int polySize) {
        return generate(new CLPolygonGeneratorFactory(new Random()), numPoly, polySize);
    }

    private static List<Polygon> generate(final PolygonGeneratorFactory polygonGeneratorFactory, int numPoly, int polySize) {
        final PolygonGenerator polygonGenerator = polygonGeneratorFactory.createPolygonGenerator(polySize);
        final List<Polygon> polygons = IntStream.rangeClosed(1, numPoly)
                .mapToObj(i -> polygonGenerator.createRandomSimplePolygon())
                .collect(Collectors.toList());

        return polygons;
    }

    public static List<Polygon> read(String pbfPath, long numPoly, int polySize) {
        final List<NodeRelation> relations = getNodeRelations(pbfPath);
        final List<Polygon> closestPolygons = getNPolygonsClosestToDesiredSize(relations, polySize, numPoly);
        return closestPolygons;
    }

    private static List<NodeRelation> getNodeRelations(String pbfPath) {
        final ImportPBF importer = new ImportPBF(pbfPath);
        try {
            importer.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return importer.getNodeRelations();
    }

    private static List<Polygon> getNPolygonsClosestToDesiredSize(List<NodeRelation> relations, int polySize, long numPoly) {
        return relations.stream()
                .map(r -> r.toPolygon())
                .sorted((p, q) -> {
                    final int pDistanceToDesiredSize = Math.abs(p.getNumPoints() - polySize);
                    final int qDistanceToDesiredSize = Math.abs(q.getNumPoints() - polySize);

                    return Integer.compare(pDistanceToDesiredSize, qDistanceToDesiredSize);
                })
                .filter(p -> p.getNumPoints() < 2 * polySize)
                .limit(numPoly)
                .collect(Collectors.toList());
    }
}
