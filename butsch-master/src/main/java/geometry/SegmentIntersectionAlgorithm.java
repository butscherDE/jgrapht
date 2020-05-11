package geometry;

import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public interface SegmentIntersectionAlgorithm {
    boolean isIntersectionPresent();

    int getIntersectionCount();

    List<Coordinate> getIntersections();
}
