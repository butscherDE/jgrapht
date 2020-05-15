package data;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegionOfInterestTest {
    @Test
    public void polygonToPolygonTest() {
        final RegionOfInterest roi = getSimpleTestROI();
        final Polygon actualPolygon = roi.getPolygon();

        final Coordinate[] expectedPolygonCoordinates = new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(2, 0),
                new Coordinate(2, 2),
                new Coordinate(1, 2),
                new Coordinate(0, 0)
        };
        final Polygon expectedPolygon = new GeometryFactory().createPolygon(expectedPolygonCoordinates);

        assertEquals(expectedPolygon, actualPolygon);
    }

    @Test
    public void polygonToSortedSegmentsTest() {
        final RegionOfInterest roi = getSimpleTestROI();
        final List<LineSegment> actualSegments = roi.getSortedLineSegments();

        final List<LineSegment> expectedSegments = Arrays.asList(
                new LineSegment(0,0,1,2),
                new LineSegment(0,0,2,0),
                new LineSegment(2,0,2,2),
                new LineSegment(1,2,2,2)
        );

        assertEquals(expectedSegments, actualSegments);
    }

    private RegionOfInterest getSimpleTestROI() {
        final Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(2,0),
                new Coordinate(2,2),
                new Coordinate(1,2),
                new Coordinate(0,0)
        };

        return new RegionOfInterest(new GeometryFactory().createPolygon(polygonCoordinates));
    }
}
