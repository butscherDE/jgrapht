package evalutation;

import data.*;
import geometry.ConvexLayers;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.locationtech.jts.geom.*;
import routing.DijkstraCHFactory;
import routing.regionAware.RegionAlong;
import routing.regionAware.RegionThrough;
import storage.GeoJsonExporter;
import storage.ImportPBF;

import javax.swing.plaf.synth.Region;
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

        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = new ContractionHierarchyPrecomputation<>(
                instance.graph).computeContractionHierarchy();
        final RoadCH roadCh = new RoadCH(ch);

        final DijkstraCHFactory chFactory = new DijkstraCHFactory(roadCh, true);
        final Path uberRaveDirect = chFactory.createRoutingAlgorithm().findPath(ueberlingen, ravensburg);
        final Path badRiedDirect = chFactory.createRoutingAlgorithm().findPath(badSaulgau, riedlingen);
        final Path sigUlmDirect = chFactory.createRoutingAlgorithm().findPath(sigmaringen, ulm);

        System.out.println("#################\nBodensee:");
        final StopWatchVerbose sw1 = new StopWatchVerbose("bodensee pathcalc");
        final RegionAlong bodenseeAlong = new RegionAlong(instance.graph, roadCh, instance.index,
                                                        new RegionOfInterest(bodensee.toPolygon()));
        final Path uberRaveAlong = bodenseeAlong.findPath(ueberlingen, ravensburg);
        sw1.printTimingIfVerbose();

        System.out.println("#################\nFedernsee:");
        final StopWatchVerbose sw2 = new StopWatchVerbose("federnsee pathcalc");
        final RegionAlong federnseeAlong = new RegionAlong(instance.graph, roadCh, instance.index,
                new RegionOfInterest(federnsee.toPolygon()));
        System.out.println("instance created");
        final Path badRiedAlong = federnseeAlong.findPath(badSaulgau, riedlingen);
        sw2.printTimingIfVerbose();

        System.out.println("#################\nNeckar Alb:");
        final StopWatchVerbose sw3 = new StopWatchVerbose("neckar alb path calc");
        final RegionThrough neckarAlbThrough = new RegionThrough(instance.graph, roadCh, instance.index,
                                                                 new RegionOfInterest(neckarAlb.toPolygon()));
        final Path sigUlmThrough = neckarAlbThrough.findPath(sigmaringen, ulm);
        sw3.printTimingIfVerbose();


        final GeoJsonExporter expUberRave = new GeoJsonExporter("lala");
        expUberRave.addLineString(toLineString(uberRaveDirect));
        expUberRave.addLineString(toLineString(uberRaveAlong));
        expUberRave.addPolygon(bodensee.toPolygon());

        final GeoJsonExporter expBadRied = new GeoJsonExporter("lala");
        expBadRied.addLineString(toLineString(badRiedDirect));
        expBadRied.addLineString(toLineString(badRiedAlong));
        expBadRied.addPolygon(federnsee.toPolygon());

        final GeoJsonExporter expSigUlm = new GeoJsonExporter("lala");
        expSigUlm.addLineString(toLineString(sigUlmDirect));
        expSigUlm.addLineString(toLineString(sigUlmThrough));
        expSigUlm.addPolygon(neckarAlb.toPolygon());

        expUberRave.writeJson();
        expBadRied.writeJson();
        expSigUlm.writeJson();
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
