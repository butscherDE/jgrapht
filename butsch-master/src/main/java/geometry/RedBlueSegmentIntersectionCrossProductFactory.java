package geometry;

import data.VisibilityCell;
import geometry.util.PolygonHelper;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.List;
import java.util.Collection;

public class RedBlueSegmentIntersectionCrossProductFactory extends RedBlueSegmentIntersectionFactory {
    @Override
    public SegmentIntersectionAlgorithm createInstance(final Collection<LineSegment> redSegments,
                                                final Collection<LineSegment> blueSegments) {
        return new RedBlueSegmentIntersectionCrossProduct(redSegments, blueSegments);
    }

    @Override
    public SegmentIntersectionAlgorithm createInstance(final VisibilityCell visibilityCell, final Polygon polygon) {
        final List<LineSegment> redSegments = visibilityCell.lineSegments;
        final List<LineSegment> blueSegments = PolygonHelper.toLineSegments(polygon);

        return createInstance(redSegments, blueSegments);
    }
}
