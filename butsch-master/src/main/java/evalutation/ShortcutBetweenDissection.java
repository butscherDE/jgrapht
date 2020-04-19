package evalutation;

import data.NodeRelation;
import data.RoadGraph;
import data.Node;
import geometry.BoundingBox;
import index.GridIndex;
import index.Index;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import storage.ImportPBF;
import util.BinaryHashFunction;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.io.FileNotFoundException;

public class ShortcutBetweenDissection {
    public static void main(String[] args) throws FileNotFoundException {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_LUXEMBOURG);
        final RoadGraph graph = importPBF.createGraph();
        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations();
        final Index index = new GridIndex(graph, 3600 * 4, 3600 * 4);

        final NodeRelation max = Collections.max(nodeRelations, Comparator.comparingInt(a -> a.nodes.size()));
        System.out.println(max.nodes.size());

        final List<Integer> numNodesInArea = new LinkedList<>();
        int c = 0;
        for (final NodeRelation nodeRelation : nodeRelations) {
            if (nodeRelation.nodes.size() > 2) {
                final Polygon relationPolygon = nodeRelation.toPolygon();
                final BoundingBox relationBoundingBox = BoundingBox.createFrom(relationPolygon);

                final PolygonNodeLogger visitor = new PolygonNodeLogger(relationPolygon);
                index.queryNodes(relationBoundingBox, visitor);

                numNodesInArea.add(visitor.nodes.size());
            }

            System.out.println(c++ + " / " + nodeRelations.size());
        }

        System.out.println(numNodesInArea.stream().reduce(0, Integer::sum) / numNodesInArea.size());
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
