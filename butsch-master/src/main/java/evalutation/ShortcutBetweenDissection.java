package evalutation;

import data.NodeRelation;
import data.RoadGraph;
import data.Node;
import geometry.BoundingBox;
import geometry.DistanceCalculator;
import index.GridIndex;
import index.Index;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import storage.CsvColumnDumper;
import storage.ImportPBF;
import util.BinaryHashFunction;

import java.io.IOException;
import java.util.*;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ShortcutBetweenDissection {
    public static void main(String[] args) throws FileNotFoundException {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_LUXEMBOURG);
        final RoadGraph graph = importPBF.createGraph();
        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations();
        final Index index = new GridIndex(graph, 3600, 3600);

        final NodeRelation max = Collections.max(nodeRelations, Comparator.comparingInt(a -> a.nodes.size()));
        System.out.println(max.nodes.size());

        final List<Object> numNodesInArea = new ArrayList<>(nodeRelations.size());
        final List<Object> longitudeLength = new ArrayList<>(nodeRelations.size());
        final List<Object> latitudeLength = new ArrayList<>(nodeRelations.size());
        final List<Object> areas = new ArrayList<>(nodeRelations.size());
        int c = 0;
        for (final NodeRelation nodeRelation : nodeRelations) {
            final StopWatchVerbose sw = new StopWatchVerbose("relation processing");
            if (nodeRelation.nodes.size() > 2) {
                final Polygon relationPolygon = nodeRelation.toPolygon();
                final BoundingBox relationBoundingBox = BoundingBox.createFrom(relationPolygon);

                final PolygonNodeLogger visitor = new PolygonNodeLogger(relationPolygon);
                index.queryNodes(relationBoundingBox, visitor);

                numNodesInArea.add(visitor.nodes.size());
                final Coordinate lowerLeft = new Coordinate(relationBoundingBox.minLongitude, relationBoundingBox.minLatitude);
                final Coordinate lowerRight = new Coordinate(relationBoundingBox.maxLongitude, relationBoundingBox.minLatitude);
                final Coordinate upperLeft = new Coordinate(relationBoundingBox.minLongitude, relationBoundingBox.maxLatitude);
                longitudeLength.add(DistanceCalculator.distance(lowerLeft, lowerRight, DistanceCalculator.Unit.METRIC));
                latitudeLength.add(DistanceCalculator.distance(lowerLeft, upperLeft, DistanceCalculator.Unit.METRIC));
                areas.add(DistanceCalculator.area(relationBoundingBox, DistanceCalculator.Unit.METRIC));

                System.out.println(c++ + " / " + nodeRelations.size() + ", id: " + nodeRelation.id);
                sw.printTimingIfVerbose();
            }
        }

        final String[] headers = {"id", "numNodes", "longitudeLength", "latitudeLength", "squareKilometer"};
        final AtomicInteger id = new AtomicInteger(0);
        final List<Object> ids = numNodesInArea.stream().map((a) -> id.getAndIncrement()).collect(Collectors.toList());
        final List<List<Object>> elements = Arrays.asList(ids, numNodesInArea, longitudeLength, latitudeLength, areas);

        final CsvColumnDumper dumper = new CsvColumnDumper(Config.PBF_LUXEMBOURG_STATS, headers, elements, ';');
        try {
            dumper.dump();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class PolygonNodeLogger implements GridIndex.GridIndexVisitor<Node> {
        private final BinaryHashFunction<BoundingBox> isCellContained = new BinaryHashFunction<>();
        final List<Node> nodes = new LinkedList<>();
        final Polygon polygon;

        public PolygonNodeLogger(final Polygon polygon) {
            this.polygon = polygon;
        }

        @Override
        public void accept(final Node node) {
            final Geometry point = node.getPoint();

            if (polygon.contains(point)) {
                nodes.add(node);
            }
        }

        @Override
        public void accept(final Node node, final BoundingBox cell) {
            Boolean cellContainedInPolygon = isCellContained(cell);

            if (cellContainedInPolygon) {
                nodes.add(node);
            } else {
                accept(node);
            }
        }

        public Boolean isCellContained(final BoundingBox cell) {
            Boolean cellContainedInPolygon = isCellContained.get(cell);

            if (cellContainedInPolygon == null) {
                cellContainedInPolygon = cell.isContainedBy(polygon);
                isCellContained.set(cell, cellContainedInPolygon);
            }
            return cellContainedInPolygon;
        }
    }
}
