package geometry;

import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RedBlueSegmentIntersectionCrossProduct implements SegmentIntersectionAlgorithm {
    private final Collection<LineSegment> redSegments;
    private final Collection<LineSegment> blueSegments;

    RedBlueSegmentIntersectionCrossProduct(final Collection<LineSegment> redSegments,
                                                  final Collection<LineSegment> blueSegments) {
        this.redSegments = redSegments;
        this.blueSegments = blueSegments;
    }

    @Override
    public boolean isIntersectionPresent() {
        // Potentially slow, but so is the algorithm anyways.
        return getIntersectionCount() > 0;
    }

    @Override
    public int getIntersectionCount() {
        return getIntersections().size();
    }

    @Override
    public List<Coordinate> getIntersections() {
        final List<Coordinate> intersections = new LinkedList<>();

        for (final LineSegment redSegment : redSegments) {
            for (final LineSegment blueSegment : blueSegments) {
                final Coordinate intersection = redSegment.intersection(blueSegment);

                if (intersection != null) {
                    intersections.add(intersection);
                }
            }
        }

        return intersections;
    }

    @Override
    public List<Pair<Integer, Integer>> getIntersectionIndices() {
        final List<Pair<Integer, Integer>> intersectionIndices = new LinkedList<>();

        final Iterator<LineSegment> redIterator = redSegments.iterator();
        final Iterator<LineSegment> blueIterator = blueSegments.iterator();
        for (int i = 0; redIterator.hasNext(); i++) {
            final LineSegment redSegment = redIterator.next();
            for (int j = 0; blueIterator.hasNext(); j++) {
                final LineSegment blueSegment = blueIterator.next();
                final Coordinate intersection = redSegment.intersection(blueSegment);

                if (intersection != null) {
                    intersectionIndices.add(new Pair(i, j));
                }
            }
        }

        return intersectionIndices;
    }
}
