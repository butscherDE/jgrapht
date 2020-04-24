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
    private static List<NodeRelation> nodeRelations;
    private static Index index;
    private static List<Object> numNodesInArea;
    private static List<Object> longitudeLength;
    private static List<Object> latitudeLength;
    private static List<Object> areas;

    public static void main(String[] args) throws FileNotFoundException {
        prepareFields();
        extractData();

        dump();
    }

    public static void prepareFields() throws FileNotFoundException {
        prepareRelationsAndIndex();
        prepareDataStorages();
    }

    public static void prepareRelationsAndIndex() throws FileNotFoundException {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_LUXEMBOURG);
        final RoadGraph graph = importPBF.createGraph();
        nodeRelations = importPBF.getNodeRelations();
        index = new GridIndex(graph, 3600, 3600);
    }

    public static void prepareDataStorages() {
        numNodesInArea = new ArrayList<>(nodeRelations.size());
        longitudeLength = new ArrayList<>(nodeRelations.size());
        latitudeLength = new ArrayList<>(nodeRelations.size());
        areas = new ArrayList<>(nodeRelations.size());
    }

    public static void extractData() {
        int c = 0;
        for (final NodeRelation nodeRelation : nodeRelations) {
            final StopWatchVerbose sw = new StopWatchVerbose("relation processing");
            if (nodeRelation.nodes.size() > 2) {
                if (c >= 209) {
                    final Polygon relationPolygon = nodeRelation.toPolygon();
                    final BoundingBox relationBoundingBox = BoundingBox.createFrom(relationPolygon);
                    final PolygonNodeLogger visitor = findContainedNodes(relationPolygon, relationBoundingBox);

                    addData(relationBoundingBox, visitor);
                }
                System.out.println(c++ + " / " + nodeRelations.size() + ", id: " + nodeRelation.id);
                sw.printTimingIfVerbose();
            }
        }
    }

    public static PolygonNodeLogger findContainedNodes(final Polygon relationPolygon,
                                                       final BoundingBox relationBoundingBox) {
        final PolygonNodeLogger visitor = new PolygonNodeLogger(relationPolygon);
        index.queryNodes(relationBoundingBox, visitor);
        return visitor;
    }

    public static void addData(final BoundingBox relationBoundingBox, final PolygonNodeLogger visitor) {
        addNumNodesInArea(visitor);
        addLengths(relationBoundingBox);
        addArea(relationBoundingBox);
    }

    public static void addNumNodesInArea(final PolygonNodeLogger visitor) {
        numNodesInArea.add(visitor.nodes.size());
    }

    public static void addLengths(final BoundingBox relationBoundingBox) {
        final Coordinate lowerLeft = new Coordinate(relationBoundingBox.minLongitude, relationBoundingBox.minLatitude);
        final Coordinate lowerRight = new Coordinate(relationBoundingBox.maxLongitude, relationBoundingBox.minLatitude);
        final Coordinate upperLeft = new Coordinate(relationBoundingBox.minLongitude, relationBoundingBox.maxLatitude);
        longitudeLength.add(DistanceCalculator.distance(lowerLeft, lowerRight, DistanceCalculator.Unit.METRIC));
        latitudeLength.add(DistanceCalculator.distance(lowerLeft, upperLeft, DistanceCalculator.Unit.METRIC));
    }

    public static void addArea(final BoundingBox relationBoundingBox) {
        areas.add(DistanceCalculator.area(relationBoundingBox, DistanceCalculator.Unit.METRIC));
    }

    public static void dump() {
        final String[] headers = {"id", "numNodes", "longitudeLength", "latitudeLength", "squareKilometer"};
        final List<Object> ids = buildIds();
        final List<List<Object>> elements = buildElements(ids);

        writeOut(headers, elements);
    }

    public static List<Object> buildIds() {
        final AtomicInteger id = new AtomicInteger(0);
        return numNodesInArea.stream().map((a) -> id.getAndIncrement()).collect(Collectors.toList());
    }

    public static List<List<Object>> buildElements(final List<Object> ids) {
        return Arrays.asList(ids, numNodesInArea, longitudeLength, latitudeLength, areas);
    }

    public static void writeOut(final String[] headers, final List<List<Object>> elements) {
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
        final BoundingBox boundingBox;

        public PolygonNodeLogger(final Polygon polygon) {
            this.polygon = polygon;
            this.boundingBox = BoundingBox.createFrom(polygon);
        }

        @Override
        public void accept(final Node node) {
            final Geometry point = node.getPoint();

            if (boundingBox.contains(point) && polygon.contains(point)) {
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
