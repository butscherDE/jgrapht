package geometry;

import org.locationtech.jts.geom.LineSegment;

import java.util.Collection;

public class RedBlueSegmentIntersectionCrossProductFactory extends RedBlueSegmentIntersectionFactory {
    @Override
    SegmentIntersectionAlgorithm createInstance(final Collection<LineSegment> redSegments,
                                                final Collection<LineSegment> blueSegments) {
        return new RedBlueSegmentIntersectionCrossProduct(redSegments, blueSegments);
    }
}
