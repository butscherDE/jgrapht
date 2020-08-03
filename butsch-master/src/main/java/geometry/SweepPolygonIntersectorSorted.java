package geometry;

import org.apache.commons.lang3.NotImplementedException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SweepPolygonIntersectorSorted implements SegmentIntersectionAlgorithm {
    private final List<LineSegment> redSegments;
    private final List<LineSegment> blueSegments;

    private final LinkedList<LSEntity> sweepQueue;
    private final List<LSEntity> redHeap;
    private final List<LSEntity> blueHeap;

    public SweepPolygonIntersectorSorted(final List<LineSegment> redSegments, final List<LineSegment> blueSegments) {
        this.redSegments = redSegments;
        this.blueSegments = blueSegments;
        this.sweepQueue = prepareSweepQueue(redSegments, blueSegments);
        this.redHeap = new LinkedList<>();
        this.blueHeap = new LinkedList<>();
    }

    public LinkedList<LSEntity> prepareSweepQueue(final List<LineSegment> redSegments, final List<LineSegment> blueSegments) {
        final ArrayList<LSEntity> entities = new ArrayList<>(2 * (redSegments.size() + blueSegments.size()));
        addAllEntities();
        Collections.sort(entities);

        return new LinkedList<>(entities);
    }

    public void addAllEntities() {
        addRedEntities();
        addBlueEntities();
    }

    public void addRedEntities() {
        for (final LineSegment redSegment : redSegments) {
            final LSEntity first = new LSEntity(redSegment.p0.x, false, null, redSegment);
            final LSEntity second = new LSEntity(redSegment.p1.x, false, first, redSegment);

            sweepQueue.add(first);
            sweepQueue.add(second);
        }
    }

    public void addBlueEntities() {
        for (final LineSegment blueSegment : blueSegments) {
            final LSEntity first = new LSEntity(blueSegment.p0.x, true, null, blueSegment);
            final LSEntity second =  new LSEntity(blueSegment.p1.x, true, first, blueSegment);

            sweepQueue.add(first);
            sweepQueue.add(second);
        }
    }

    @Override
    public boolean isIntersectionPresent() {
        throw new NotImplementedException("");
    }

    @Override
    public int getIntersectionCount() {
        throw new NotImplementedException("");
    }

    @Override
    public List<Coordinate> getIntersections() {
        throw new NotImplementedException("");
    }

    private List<Coordinate> nextSweep() {
        final List<Coordinate> intersections;
        final LSEntity popped = sweepQueue.removeFirst();

        if (!popped.isEndPoint()) {
            intersections = addToHeapAndIntersect(popped);
        } else {
            intersections = removeFromHeap(popped);
        }

        return intersections;
    }

    private List<Coordinate> addToHeapAndIntersect(final LSEntity popped) {
        final List<Coordinate> intersections;
        if (popped.isRedOrigin()) {
            redHeap.add(popped);

            intersections = getIntersectWithOtherHeap(popped, blueHeap);
        } else {
            blueHeap.add(popped);

            intersections = getIntersectWithOtherHeap(popped, redHeap);
        }
        return intersections;
    }

    private List<Coordinate> removeFromHeap(final LSEntity popped) {
        if (popped.isRedOrigin()) {
            redHeap.remove(popped.startPoint);
        } else {
            blueHeap.remove(popped.startPoint);
        }

        return new LinkedList<>();
    }

    private List<Coordinate> getIntersectWithOtherHeap(final LSEntity popped, final List<LSEntity> otherColorHeap) {
        final List<Coordinate> intersections = new LinkedList<>();
        for (final LSEntity other : otherColorHeap) {
            final Coordinate intersection = popped.segment.intersection(other.segment);
            if (intersection != null) {
                intersections.add(intersection);
            }
        }

        return intersections;
    }

    private class LSEntity implements Comparable {
        private final double x; // x position of segment
        private final boolean origin; //0 = red, 1 = blue
        private final LSEntity startPoint; // null if this is endpoint, pointer to endpoint if start point
        private final LineSegment segment; //segment from which this point was drawn

        public LSEntity(final double x, final boolean origin, final LSEntity startPoint, final LineSegment segment) {
            this.x = x;
            this.origin = origin;
            this.startPoint = startPoint;
            this.segment = segment;
        }

        @Override
        public int compareTo(final Object o) {
            final LSEntity that = (LSEntity) o;
            return Double.compare(this.x, that.x);
        }

        public boolean isEndPoint() {
            return this.startPoint != null;
        }

        public boolean isRedOrigin() {
            return !origin;
        }
    }
}
