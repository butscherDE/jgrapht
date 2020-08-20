package routing.regionAware.util;

import data.Node;

import geometry.BoundingBox;
import geometry.PolygonContainsChecker;
import index.GridIndex;
import index.GridIndex.GridIndexVisitor;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PolygonSimplifierCH extends PolygonSimplifier {
    private final GridIndex index;
    private final static GeometryFactory gf = new GeometryFactory();

    public PolygonSimplifierCH(final GridIndex index) {
        this.index = index;
    }

    @Override
    public Polygon simplify(final Polygon polygon) {
        final ROINodes visitor = new ROINodes(polygon);
        index.queryNodes(BoundingBox.createFrom(polygon), visitor);

        final ConvexHull convexHull = new ConvexHull(visitor.points.toArray(new Coordinate[visitor.points.size()]), gf);
        final Coordinate[] hullCoordinates = convexHull.getConvexHull().getCoordinates();

        return gf.createPolygon(hullCoordinates);
    }

    private class ROINodes implements GridIndexVisitor<Node> {
        public final List<Coordinate> points = new ArrayList<>();
        private final PolygonContainsChecker polygonContainsChecker;

        private ROINodes(final Polygon polygon) {
            this.polygonContainsChecker = new PolygonContainsChecker(polygon);
        }

        @Override
        public void accept(final Node entity, final BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(final Node entity) {
            final Point point = entity.getPoint();
            if (polygonContainsChecker.contains(point)) {
                points.add(point.getCoordinate());
            }
        }
    }
}
