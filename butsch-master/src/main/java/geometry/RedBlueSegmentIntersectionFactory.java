package geometry;

import data.VisibilityCell;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.Collection;

public abstract class RedBlueSegmentIntersectionFactory {
    public abstract SegmentIntersectionAlgorithm createInstance(final Collection<LineSegment> redSegments,
                                                                final Collection<LineSegment> blueSegments);
    abstract SegmentIntersectionAlgorithm createInstance(final VisibilityCell visibilityCell, final Polygon polygon);
}
