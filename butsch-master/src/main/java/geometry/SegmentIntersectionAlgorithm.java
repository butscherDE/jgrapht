package geometry;

import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public interface SegmentIntersectionAlgorithm {
    boolean isIntersectionPresent();

    int getIntersectionCount();

    List<Coordinate> getIntersections();

    List<Pair<Integer, Integer>> getIntersectionIndices();
}
