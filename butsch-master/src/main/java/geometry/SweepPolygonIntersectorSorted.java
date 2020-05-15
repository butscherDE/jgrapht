package geometry;

import org.apache.commons.lang3.NotImplementedException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.List;

public class SweepPolygonIntersectorSorted implements SegmentIntersectionAlgorithm {
    private final List<LineSegment> redSegments;
    private final List<LineSegment> blueSegments;
    private final List<LSEntity> lsEntities;

    public SweepPolygonIntersectorSorted(final List<LineSegment> redSegments, final List<LineSegment> blueSegments) {
        this.redSegments = redSegments;
        this.blueSegments = blueSegments;

        lsEntities = new ArrayList<>(2 * (redSegments.size() + blueSegments.size()));

        addAllEntities();
    }

    public void addAllEntities() {
        addRedEntities();
        addBlueEntities();
    }

    public void addRedEntities() {
        for (final LineSegment redSegment : redSegments) {
            final LSEntity second = new LSEntity(redSegment.p1.x, redSegment.p1.y, true, false, null, redSegment);
            final LSEntity first = new LSEntity(redSegment.p0.x, redSegment.p1.y, false, false, second, redSegment);

            lsEntities.add(first);
            lsEntities.add(second);
        }
    }

    public void addBlueEntities() {
        for (final LineSegment blueSegment : blueSegments) {
            final LSEntity second =  new LSEntity(blueSegment.p1.x, blueSegment.p0.y, true, true, null, blueSegment);
            final LSEntity first = new LSEntity(blueSegment.p0.x, blueSegment.p0.y, false, true, second, blueSegment);

            lsEntities.add(first);
            lsEntities.add(second);
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



    private class LSEntity implements Comparable {
        private final double x;
        private final double y;
        private final boolean side; // Startpoint or endpoint
        private final boolean origin; // Red or blue
        private final LSEntity endpoint;
        private final LineSegment segment;

        public LSEntity(final double x, final double y, final boolean side, final boolean origin,
                        final LSEntity endpoint, final LineSegment segment) {
            this.x = x;
            this.y = y;
            this.side = side;
            this.origin = origin;
            this.endpoint = endpoint;
            this.segment = segment;
        }

        @Override
        public int compareTo(final Object o) {
            final LSEntity that = (LSEntity) o;
            return Double.compare(this.x, that.x);
        }
    }
}
