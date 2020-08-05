package geometry;

import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.*;
import java.util.stream.Collectors;

public class SweepPolygonIntersectorSorted implements SegmentIntersectionAlgorithm {
    private final Collection<LineSegment> redSegments;
    private final Collection<LineSegment> blueSegments;

    private final LinkedList<LSEntity> sweepQueue;
    private final List<LSEntity> redHeap;
    private final List<LSEntity> blueHeap;

    public SweepPolygonIntersectorSorted(final Collection<LineSegment> redSegments, final Collection<LineSegment> blueSegments) {
        this.redSegments = redSegments;
        this.blueSegments = blueSegments;
        this.sweepQueue = prepareSweepQueue(redSegments, blueSegments);
        this.redHeap = new LinkedList<>();
        this.blueHeap = new LinkedList<>();
    }

    public LinkedList<LSEntity> prepareSweepQueue(final Collection<LineSegment> redSegments, final Collection<LineSegment> blueSegments) {
        final ArrayList<LSEntity> entities = addAllEntities();
        Collections.sort(entities);

        return new LinkedList<>(entities);
    }

    public ArrayList<LSEntity> addAllEntities() {
        final ArrayList<LSEntity> entities = new ArrayList<>(2 * (redSegments.size() + blueSegments.size()));

        addRedEntities(entities);
        addBlueEntities(entities);

        return entities;
    }

    public void addRedEntities(final ArrayList<LSEntity> entities) {
        final Iterator<LineSegment> redIterator = redSegments.iterator();
        for (int i = 0; redIterator.hasNext(); i++) {
            final LineSegment redSegment = redIterator.next();
            final LSEntity first = new LSEntity(redSegment.p0.x, false, null, redSegment, i);
            final LSEntity second = new LSEntity(redSegment.p1.x, false, first, redSegment, i);

            entities.add(first);
            entities.add(second);
        }
    }

    public void addBlueEntities(final ArrayList<LSEntity> entities) {
        final Iterator<LineSegment> blueIterator = blueSegments.iterator();
        for (int i = 0; blueIterator.hasNext(); i++) {
            final LineSegment blueSegment = blueIterator.next();
            final LSEntity first = new LSEntity(blueSegment.p0.x, true, null, blueSegment, i);
            final LSEntity second =  new LSEntity(blueSegment.p1.x, true, first, blueSegment, i);

            entities.add(first);
            entities.add(second);
        }
    }

    @Override
    public boolean isIntersectionPresent() {
        while (hasQueueNext()) {
            if (nextSweepStep().size() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getIntersectionCount() {
        return getIntersections().size();
    }

    @Override
    public List<Coordinate> getIntersections() {
        return getIntersectionInfos().stream().map(is -> is.intersection).collect(Collectors.toList());
    }

    @Override
    public List<Pair<Integer, Integer>> getIntersectionIndices() {
        return getIntersectionInfos().stream().map(is -> new Pair<>(is.redIndex, is.blueIndex)).collect(Collectors.toList());
    }

    public List<SweepIntersection> getIntersectionInfos() {
        final List<SweepIntersection> intersectionInfos = new LinkedList<>();

        while (hasQueueNext()) {
            intersectionInfos.addAll(nextSweepStep());
        }

        return intersectionInfos;
    }

    private boolean hasQueueNext() {
        return this.sweepQueue.size() > 0;
    }

    private List<SweepIntersection> nextSweepStep() {
        final List<SweepIntersection> intersections;
        final LSEntity popped = sweepQueue.removeFirst();

        if (!popped.isEndPoint()) {
            intersections = addToHeapAndIntersect(popped);
        } else {
            intersections = removeFromHeap(popped);
        }

        return intersections;
    }

    private List<SweepIntersection> addToHeapAndIntersect(final LSEntity popped) {
        final List<SweepIntersection> intersections  = new LinkedList<>();

        if (popped.isRedOrigin()) {
            redHeap.add(popped);

            for (final LSEntity other : blueHeap) {
                final Coordinate intersection = popped.segment.intersection(other.segment);
                if (intersection != null) {
                    final SweepIntersection intersectionInfo = new SweepIntersection(intersection, popped.index, other.index);
                    intersections.add(intersectionInfo);
                }
            }
        } else {
            blueHeap.add(popped);

            for (final LSEntity other : redHeap) {
                final Coordinate intersection = popped.segment.intersection(other.segment);
                if (intersection != null) {
                    final SweepIntersection intersectionInfo = new SweepIntersection(intersection, other.index, popped.index);
                    intersections.add(intersectionInfo);
                }
            }
        }
        return intersections;
    }

    private List<SweepIntersection> removeFromHeap(final LSEntity popped) {
        if (popped.isRedOrigin()) {
            redHeap.remove(popped.startPoint);
        } else {
            blueHeap.remove(popped.startPoint);
        }

        return new LinkedList<>();
    }

    private class LSEntity implements Comparable {
        private final double x; // x position of segment
        private final boolean origin; //0 = red, 1 = blue
        private final LSEntity startPoint; // null if this is endpoint, pointer to endpoint if start point
        private final LineSegment segment; //segment from which this point was drawn
        private final int index;

        public LSEntity(final double x, final boolean origin, final LSEntity startPoint, final LineSegment segment, int index) {
            this.x = x;
            this.origin = origin;
            this.startPoint = startPoint;
            this.segment = segment;
            this.index = index;
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

    private class SweepIntersection {
        private final Coordinate intersection;
        private final int redIndex;
        private final int blueIndex;

        public SweepIntersection(Coordinate intersection, int redIndex, int blueIndex) {
            this.intersection = intersection;
            this.redIndex = redIndex;
            this.blueIndex = blueIndex;
        }
    }
}
