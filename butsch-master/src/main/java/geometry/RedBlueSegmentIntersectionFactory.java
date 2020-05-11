package geometry;

import org.locationtech.jts.geom.LineSegment;

import java.util.Collection;

public abstract class RedBlueSegmentIntersectionFactory {
    abstract SegmentIntersectionAlgorithm createInstance(final Collection<LineSegment> redSegments, final Collection<LineSegment> blueSegments);
}
