package geometry;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShapeIntersectionTest {
    @Test
    public void quickAndDirtyTest() {
        final GeometryFactory gf = new GeometryFactory();

        final Polygon p1 = gf.createPolygon(new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(2,0),
                new Coordinate(2,2),
                new Coordinate(0,2),
                new Coordinate(0,0)
        });
        final Polygon p2 = gf.createPolygon(new Coordinate[] {
                new Coordinate(1,1),
                new Coordinate(3, 1),
                new Coordinate(3,3),
                new Coordinate(1,3),
                new Coordinate(1,1)
        });
        final Polygon p3 = gf.createPolygon(new Coordinate[] {
                new Coordinate(2,2),
                new Coordinate(4,2),
                new Coordinate(4,4),
                new Coordinate(2,4),
                new Coordinate(2,2)
        });
        final Polygon p4 = gf.createPolygon(new Coordinate[] {
                new Coordinate(3,3),
                new Coordinate(5,3),
                new Coordinate(5,5),
                new Coordinate(3,5),
                new Coordinate(3,3)
        });

        final SweepPolygonIntersectorSorted intersector1 = new SweepPolygonIntersectorSorted(
                toSegments(p1), toSegments(p2));
        final SweepPolygonIntersectorSorted intersector2 = new SweepPolygonIntersectorSorted(
                toSegments(p1), toSegments(p3));
        final SweepPolygonIntersectorSorted intersector3 = new SweepPolygonIntersectorSorted(
                toSegments(p1), toSegments(p4));

        assertTrue(intersector1.isIntersectionPresent());
        assertTrue(intersector2.isIntersectionPresent());
        assertFalse(intersector3.isIntersectionPresent());
    }

    private List<LineSegment> toSegments(final Polygon polygon) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final List<LineSegment> segments = new ArrayList<>(coordinates.length -1);

        for (int i = 0; i < coordinates.length - 1; i++) {
            segments.add(new LineSegment(coordinates[i], coordinates[i + 1]));
        }

        return segments;
    }
}
