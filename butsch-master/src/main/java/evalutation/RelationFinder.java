package evalutation;

import data.NodeRelation;
import geometry.ConvexLayers;
import org.locationtech.jts.geom.*;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RelationFinder {
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

        final ImportPBF freiburg = new ImportPBF(Config.PBF_TUEBINGEN);
        try {
            freiburg.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final List<NodeRelation> nodeRelations = freiburg.getNodeRelations();
        final NodeRelation bodensee = nodeRelations.stream().filter(r -> r.id == 1156846 ).findFirst().orElse(null);
        final NodeRelation federnsee = nodeRelations.stream().filter(r -> r.id == 8387767).findFirst().orElse(null);
        final NodeRelation neckarAlb = nodeRelations.stream().filter(r -> r.id == 2799137).findFirst().orElse(null);

        System.out.println(bodensee.description);
        System.out.println(federnsee.description);
        System.out.println(neckarAlb.description);


    }
}
