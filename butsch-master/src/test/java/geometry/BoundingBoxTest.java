package geometry;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoundingBoxTest {
    final BoundingBox mainOverlappingTestBox = new BoundingBox(1,2,1,2);

    @Test
    public void testIntersect() {
        //    ---
        //    | |
        // ---------
        // |  | |  |
        // --------
        //    |_|
        //

        // use ISO 19115 standard (minLongitude, maxLongitude followed by minLatitude(south!),maxLatitude)
        assertTrue(new BoundingBox(12, 15, 12, 15).intersects(new BoundingBox(13, 14, 11, 16)));
        // assertFalse(new BoundingBox(15, 12, 12, 15).intersects(new BoundingBox(16, 15, 11, 14)));

        // DOES NOT WORK: use bottom to top coord for lat
        // assertFalse(new BoundingBox(6, 2, 11, 6).intersects(new BoundingBox(5, 3, 12, 5)));
        // so, use bottom-left and top-right corner!
        assertTrue(new BoundingBox(2, 6, 6, 11).intersects(new BoundingBox(3, 5, 5, 12)));

        // DOES NOT WORK: use bottom to top coord for lat and right to left for lon
        // assertFalse(new BoundingBox(6, 11, 11, 6).intersects(new BoundingBox(5, 10, 12, 7)));
        // so, use bottom-right and top-left corner
        assertTrue(new BoundingBox(6, 11, 6, 11).intersects(new BoundingBox(7, 10, 5, 12)));
    }

    @Test
    public void overlapFalseWhenOtherBoxIsCompletelyLeft() {
        final BoundingBox completelyLeftSameHeight = new BoundingBox(0, 0.5, 1, 2);
        final BoundingBox completelyLeftPartiallyAbove = new BoundingBox(0, 0.5, 1.5, 2.5);
        final BoundingBox completelyLeftBorderAbove = new BoundingBox(0, 0.5, 2, 3);
        final BoundingBox completelyLeftCompletelyAbove = new BoundingBox(0, 0.5, 3, 4);
        final BoundingBox completelyLeftPartiallyBelow = new BoundingBox(0, 0.5, 0.5, 1.5);
        final BoundingBox completelyLeftBorderBelow = new BoundingBox(0, 0.5, 0, 1);
        final BoundingBox completelyLeftCompletelyBelow = new BoundingBox(0, 0.5, -1, 0);

        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftSameHeight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftPartiallyAbove));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftBorderAbove));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftCompletelyAbove));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftPartiallyBelow));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftBorderBelow));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyLeftCompletelyBelow));
    }

    @Test
    public void overlapFalseWhenOtherBoxIsCompletelyRight() {
        final BoundingBox completelyRightSameHeight = new BoundingBox(2.5, 3, 1, 2);
        final BoundingBox completelyRightPartiallyAbove = new BoundingBox(2.5, 3, 1.5, 2.5);
        final BoundingBox completelyRightBorderAbove = new BoundingBox(2.5, 3, 2, 3);
        final BoundingBox completelyRightCompletelyAbove = new BoundingBox(2.5, 3, 3, 4);
        final BoundingBox completelyRightPartiallyBelow = new BoundingBox(2.5, 3, 0.5, 1.5);
        final BoundingBox completelyRightBorderBelow = new BoundingBox(2.5, 3, 0, 1);
        final BoundingBox completelyRightCompletelyBelow = new BoundingBox(2.5, 3, -1, 0);

        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightSameHeight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightPartiallyAbove));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightBorderAbove));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightCompletelyAbove));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightPartiallyBelow));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightBorderBelow));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyRightCompletelyBelow));
    }

    @Test
    public void overlapFalseWhenOtherBoxIsCompletelyBelow() {
        final BoundingBox completelyBelowCenter = new BoundingBox(1, 2, 0, 0.5);
        final BoundingBox completelyBelowPartiallyRight = new BoundingBox(1.5, 2.5, 0, 0.5);
        final BoundingBox completelyBelowBorderRight = new BoundingBox(2, 3, 0, 0.5);
        final BoundingBox completelyBelowCompletelyRight = new BoundingBox(3, 4, 0, 0.5);
        final BoundingBox completelyBelowPartiallyLeft = new BoundingBox(0.5, 1.5, 0, 0.5);
        final BoundingBox completelyBelowBorderLeft = new BoundingBox(0, 1, 0, 0.5);
        final BoundingBox completelyBelowCompletelyLeft = new BoundingBox(-1, 0, 0, 0.5);

        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowCenter));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowPartiallyRight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowBorderRight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowCompletelyRight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowPartiallyLeft));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowBorderLeft));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyBelowCompletelyLeft));
    }

    @Test
    public void overlapFalseWhenOtherBoxIsCompletelyAbove() {
        final BoundingBox completelyAboveCenter = new BoundingBox(1, 2, 2.5, 3);
        final BoundingBox completelyAbovePartiallyRight = new BoundingBox(1.5, 2.5, 2.5, 3);
        final BoundingBox completelyAboveBorderRight = new BoundingBox(2, 3, 2.5, 3);
        final BoundingBox completelyAboveCompletelyRight = new BoundingBox(3, 4, 2.5, 3);
        final BoundingBox completelyAbovePartiallyLeft = new BoundingBox(0.5, 1.5, 2.5, 3);
        final BoundingBox completelyAboveBorderLeft = new BoundingBox(0, 1, 2.5, 3);
        final BoundingBox completelyAboveCompletelyLeft = new BoundingBox(-1, 0, 2.5, 3);

        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAboveCenter));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAbovePartiallyRight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAboveBorderRight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAboveCompletelyRight));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAbovePartiallyLeft));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAboveBorderLeft));
        assertFalse(mainOverlappingTestBox.isOverlapping(completelyAboveCompletelyLeft));
    }

    @Test
    public void overlapTrueBordercasesLeft() {
        final BoundingBox borderLeftLowerCorner = new BoundingBox(0,1, 0,1);
        final BoundingBox borderLeftPartiallyLower = new BoundingBox(0,1,0.5, 1.5);
        final BoundingBox borderLeftCenter = new BoundingBox(0,1,1,2);
        final BoundingBox borderLeftPartiallyAbove = new BoundingBox(0,1,1.5, 2.5);
        final BoundingBox borderLeftUpperCorner = new BoundingBox(0,1,2,3);

        assertTrue(mainOverlappingTestBox.isOverlapping(borderLeftLowerCorner));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderLeftPartiallyLower));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderLeftCenter));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderLeftPartiallyAbove));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderLeftUpperCorner));
    }

    @Test
    public void overlapTrueBordercasesRight() {
        final BoundingBox borderRightLowerCorner = new BoundingBox(2,3, 0,1);
        final BoundingBox borderRightPartiallyLower = new BoundingBox(2,3, 0.5, 1.5);
        final BoundingBox borderRightCenter = new BoundingBox(2,3, 1,2);
        final BoundingBox borderRightPartiallyAbove = new BoundingBox(2,3, 1.5, 2.5);
        final BoundingBox borderRightUpperCorner = new BoundingBox(2,3,2,3);

        assertTrue(mainOverlappingTestBox.isOverlapping(borderRightLowerCorner));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderRightPartiallyLower));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderRightCenter));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderRightPartiallyAbove));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderRightUpperCorner));
    }

    @Test
    public void overlapTrueBordercasesAbove() {
        final BoundingBox borderAboveLeftCorner = new BoundingBox(0,1,2,3);
        final BoundingBox borderAbovePartiallyLeft = new BoundingBox(0.5, 1.5, 2,3);
        final BoundingBox borderAboveCenter = new BoundingBox(1,2,0,1);
        final BoundingBox borderAbovePartiallyRight = new BoundingBox(1.5, 2.5, 2,3);
        final BoundingBox borderAboveRightCorner = new BoundingBox(2,3, 2,3);

        assertTrue(mainOverlappingTestBox.isOverlapping(borderAboveLeftCorner));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderAbovePartiallyLeft));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderAboveCenter));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderAbovePartiallyRight));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderAboveRightCorner));
    }

    @Test
    public void overlapTrueBordercasesBelow() {
        final BoundingBox borderBelowLeftCorner = new BoundingBox(0,1,0,1);
        final BoundingBox borderBelowPartiallyLeft = new BoundingBox(0.5, 1.5, 0,1);
        final BoundingBox borderBelowCenter = new BoundingBox(1,2,0,1);
        final BoundingBox borderBelowPartiallyRight = new BoundingBox(1.5, 2.5, 0,1);
        final BoundingBox borderBelowRightCorner = new BoundingBox(2,3, 0,1);

        assertTrue(mainOverlappingTestBox.isOverlapping(borderBelowLeftCorner));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderBelowPartiallyLeft));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderBelowCenter));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderBelowPartiallyRight));
        assertTrue(mainOverlappingTestBox.isOverlapping(borderBelowRightCorner));
    }

    @Test
    public void overlapTrueAreaShared() {
        final BoundingBox partiallyLeft = new BoundingBox(0.5, 1.5, 1, 2);
        final BoundingBox partiallyRight = new BoundingBox(1.5, 2.5, 1, 2);
        final BoundingBox partiallyAbove = new BoundingBox(1, 2, 1.5, 2.5);
        final BoundingBox partiallyBelow = new BoundingBox(1, 2, 0.5, 1.5);
        final BoundingBox partiallyLeftAbove = new BoundingBox(0.5, 1.5, 1.5, 2.5);
        final BoundingBox partiallyRightAbove = new BoundingBox(1.5, 2.5, 1.5, 2.5);
        final BoundingBox partiallyLeftBelow = new BoundingBox(0.5, 1.5, 0.5, 1.5);
        final BoundingBox partiallyRightBelow = new BoundingBox(1.5, 2.5, 0.5, 1.5);

        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyLeft));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyRight));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyAbove));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyBelow));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyLeftAbove));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyRightAbove));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyLeftBelow));
        assertTrue(mainOverlappingTestBox.isOverlapping(partiallyRightBelow));
    }

    @Test
    public void overlapTrueSameArea() {
        final BoundingBox sameBbox = new BoundingBox(1,2,1,2);

        assertTrue(mainOverlappingTestBox.isOverlapping(sameBbox));
    }

    @Test
    public void lineSegmentRepresentation() {
        final BoundingBox testBox = new BoundingBox(-1, 1, -1, 1);
        final List<LineSegment> testBoxLineSegments = testBox.getLineSegmentRepresentation();

        final List<LineSegment> groundTruth = new ArrayList<>(4);
        groundTruth.add(new LineSegment(-1, -1, -1, 1));
        groundTruth.add(new LineSegment(-1, 1, 1, 1));
        groundTruth.add(new LineSegment(1, 1, 1, -1));
        groundTruth.add(new LineSegment(1, -1, -1, -1));

        assertEquals(groundTruth, testBoxLineSegments);
    }
}
