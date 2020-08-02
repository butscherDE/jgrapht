package evalutation;

import data.*;
import geometry.ConvexLayers;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.locationtech.jts.geom.*;
import routing.DijkstraCHFactory;
import storage.GeoJsonExporter;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RelationFinder {
    private final static GeometryFactory gf = new GeometryFactory();

    public static void main(String[] args) {
//        final int numPoints = 20;
//        final GeometryFactory gf = new GeometryFactory();
//        final Random random = new Random(46);
//        final List<Point> points = IntStream
//                .rangeClosed(1, numPoints)
//                .mapToObj(i -> new Coordinate(random.nextInt(20), random.nextInt(20)))
//                .map(c -> gf.createPoint(c))
//                .collect(Collectors.toList());
//        final MultiPoint multiPoint = gf.createMultiPoint(points.toArray(new Point[numPoints]));
//        final ConvexLayers cl = new ConvexLayers(multiPoint);
//
//        IntStream
//                .range(0, cl.size())
//                .mapToObj(i -> cl.getLayerAsLineSegments(i))
//                .flatMap(layer -> layer.stream())
//                .forEach(ls -> System.out.println("\\draw [black] (" + (int) ls.p0.x + "," + (int) ls.p0.y + ") to (" + (int) ls.p1.x + "," + (int) ls.p1.y + ");"));
//        points.forEach(p -> System.out.println("\\filldraw [black] (" + (int) p.getX() + "," + (int) p.getY() + ") circle(6pt);"));
//
//
//        System.exit(-1);

        final DataInstance instance = DataInstance.createFromImporter(new ImportPBF(Config.PBF_TUEBINGEN));

        final List<NodeRelation> nodeRelations = instance.relations;
        final NodeRelation bodensee = nodeRelations.stream().filter(r -> r.id == 1156846 ).findFirst().orElse(null);
        final NodeRelation federnsee = nodeRelations.stream().filter(r -> r.id == 8387767).findFirst().orElse(null);
        final NodeRelation neckarAlb = nodeRelations.stream().filter(r -> r.id == 2799137).findFirst().orElse(null);

        final Node ueberlingen = instance.index.getClosestNode(9.170290, 47.766256);
        final Node ravensburg = instance.index.getClosestNode(9.612264, 47.777967);
        final Node badSaulgau = instance.index.getClosestNode(9.498933, 48.014345);
        final Node riedlingen = instance.index.getClosestNode(9.469365, 48.160181);
        final Node sigmaringen = instance.index.getClosestNode(9.228290, 48.091957);
        final Node ulm = instance.index.getClosestNode(9.988786, 48.401491);

        System.out.println(ueberlingen);
        System.out.println(ravensburg);
        System.out.println(badSaulgau);
        System.out.println(riedlingen);
        System.out.println(sigmaringen);
        System.out.println(ulm);

        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = new ContractionHierarchyPrecomputation<Node, Edge>(
                instance.graph).computeContractionHierarchy();
        final RoadCH roadCh = new RoadCH(ch);

        final DijkstraCHFactory chFactory = new DijkstraCHFactory(roadCh, true);
        final Path uberRave = chFactory.createRoutingAlgorithm().findPath(ueberlingen, ravensburg);
        final Path badRied = chFactory.createRoutingAlgorithm().findPath(badSaulgau, riedlingen);
        final Path sigUlm = chFactory.createRoutingAlgorithm().findPath(sigmaringen, ulm);

        final GeoJsonExporter exp = new GeoJsonExporter("lala");
        exp.addLineString(toLineString(uberRave));
        exp.addLineString(toLineString(badRied));
        exp.addLineString(toLineString(sigUlm));

        exp.writeJson();
    }

    private static LineString toLineString(final Path path) {
        final Coordinate[] coordinates = path
                .getVertexList()
                .stream()
                .map(v -> v.getPoint().getCoordinate())
                .collect(Collectors.toList())
                .toArray(new Coordinate[path.getLength()]);
        return gf.createLineString(coordinates);
    }
}
