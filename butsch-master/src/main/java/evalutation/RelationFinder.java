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
        final DataInstance instance = DataInstance.createFromImporter(new ImportPBF(Config.PBF_TUEBINGEN));

        final List<NodeRelation> nodeRelations = instance.relations;
        System.out.println(nodeRelations.stream().filter(r -> r.coordinates.length > 1000).map(r -> r.id).collect(Collectors.toList()));
        final NodeRelation bodensee = nodeRelations.stream().filter(r -> r.id == 1156846 ).findFirst().orElse(null);
        System.out.println("bodensee length: " + bodensee.coordinates.length);

        final NodeRelation r1 = nodeRelations.stream().filter(r -> r.id == 9919238).findFirst().orElse(null);
        final NodeRelation r2 = nodeRelations.stream().filter(r -> r.id == 8408).findFirst().orElse(null);
        final NodeRelation r3 = nodeRelations.stream().filter(r -> r.id == 2799137).findFirst().orElse(null);

        final Node s1 = instance.index.getClosestNode(9.2546, 47.9228);
        final Node t1 = instance.index.getClosestNode(9.3998356, 48.494732);
        final Node s2 = instance.index.getClosestNode(9.055520, 48.520315);
        final Node t2 = t1; //instance.index.getClosestNode(9.469365, 48.160181);
        final Node s3 = instance.index.getClosestNode(9.228290, 48.091957);
        final Node t3 = instance.index.getClosestNode(9.988786, 48.401491);

        final ContractionHierarchyPrecomputation.ContractionHierarchy<Node, Edge> ch = new ContractionHierarchyPrecomputation<>(
                instance.graph).computeContractionHierarchy();
        final RoadCH roadCh = new RoadCH(ch);

        final DijkstraCHFactory chFactory = new DijkstraCHFactory(roadCh, true);
        final Path p1Direct = chFactory.createRoutingAlgorithm().findPath(s1, t1);
        final Path p2Direct = chFactory.createRoutingAlgorithm().findPath(s2, t2);
        final Path p3Direct = chFactory.createRoutingAlgorithm().findPath(s3, t3);

        System.out.println("#################\nBurgried:");
        final StopWatchVerbose sw1 = new StopWatchVerbose("Burgried pathcalc");
        final RegionAlong r1Along = new RegionAlong(instance.graph, roadCh, instance.index,
                                                        new RegionOfInterest(r1.toPolygon()));
        final Path p1Along = r1Along.findPath(s1, t1);
        sw1.printTimingIfVerbose();

        System.out.println("#################\nTübinger Naturpark:");
        final StopWatchVerbose sw2 = new StopWatchVerbose("Tübinger pathcalc");
        final RegionAlong r2Along = new RegionAlong(instance.graph, roadCh, instance.index,
                new RegionOfInterest(r2.toPolygon()));
        final Path p2Along = r2Along.findPath(s2, t2);
        sw2.printTimingIfVerbose();

        System.out.println("#################\nNeckar Alb:");
        final StopWatchVerbose sw3 = new StopWatchVerbose("neckar alb path calc");
        final RegionThrough r3Through = new RegionThrough(instance.graph, roadCh, instance.index,
                                                                 new RegionOfInterest(r3.toPolygon()));
        final Path p3Through = r3Through.findPath(s3, t3);
        sw3.printTimingIfVerbose();

        final GeoJsonExporter expWaldseeWangen = new GeoJsonExporter(null);
        expWaldseeWangen.addLineString(toLineString(p1Direct));
        expWaldseeWangen.addLineString(toLineString(p1Along));
        expWaldseeWangen.addPolygon(r1.toPolygon());

        final GeoJsonExporter expBadRied = new GeoJsonExporter(null);
        expBadRied.addLineString(toLineString(p2Direct));
        expBadRied.addLineString(toLineString(p2Along));
        expBadRied.addPolygon(r2.toPolygon());

        final GeoJsonExporter expSigUlm = new GeoJsonExporter(null);
        expSigUlm.addLineString(toLineString(p3Direct));
        expSigUlm.addLineString(toLineString(p3Through));
        expSigUlm.addPolygon(r3.toPolygon());

        expWaldseeWangen.writeJson();
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
