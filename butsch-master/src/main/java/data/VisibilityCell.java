package data;

import geometry.BoundingBox;
import geometry.RedBlueSegmentIntersectionCrossProduct;
import geometry.RedBlueSegmentIntersectionCrossProductFactory;
import geometry.SegmentIntersectionAlgorithm;
import org.apache.commons.lang3.NotImplementedException;
import org.jgrapht.util.VisitedManager;
import org.locationtech.jts.geom.*;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class VisibilityCell {
    private static GeometryFactory gf = new GeometryFactory();
    public final List<LineSegment> lineSegments;
    private final Coordinate[] coordinates;

    private VisibilityCell(final List<LineSegment> lineSegments, final Coordinate[] coordinates) {
        this.lineSegments = lineSegments;
        this.coordinates = coordinates;
    }

    public static VisibilityCell create(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        return create(coordinates);
    }

    public static VisibilityCell create(final Coordinate[] coordinates) {
        if (!coordinates[0].equals(coordinates[coordinates.length - 1])) {
            throw new IllegalArgumentException("Coordinates do not form a closed LineString");
        }

        final List<LineSegment> lineSegments = new ArrayList<>(coordinates.length - 1);

        for (int i = 0; i < coordinates.length - 1; i++) {
            lineSegments.add(new LineSegment(coordinates[i], coordinates[i + 1]));
        }

        return new VisibilityCell(lineSegments, coordinates);
    }

    public static VisibilityCell create(final List<Node> nodes) {
        final Coordinate[] coordinates = new Coordinate[nodes.size() + 1];
        final Iterator<Node> nodeIterator = nodes.iterator();
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodeIterator.next();
            coordinates[i] = node.getPoint().getCoordinate();
        }
        coordinates[coordinates.length - 1] = nodes.get(0).getPoint().getCoordinate();

        return create((Coordinate[]) coordinates);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VisibilityCell that = (VisibilityCell) o;
        return lineSegments.equals(that.lineSegments);
    }

    public Polygon toPolygon() {
        return gf.createPolygon(coordinates);
    }

    public boolean intersects(final Polygon polygon) {
        final SegmentIntersectionAlgorithm intersectionAlgoInstance = new RedBlueSegmentIntersectionCrossProductFactory()
                .createInstance(this, polygon);
        return intersectionAlgoInstance.isIntersectionPresent();
//        throw new NotImplementedException("TODO");
    }

    public boolean contains(final Geometry geometry) {
        throw new NotImplementedException("Necessary?");
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.createFrom(toPolygon());
    }

    @Override
    public String toString() {
        return "VisibilityCell{" + "coordinates=" + Arrays.toString(coordinates) + '}';
    }
}
