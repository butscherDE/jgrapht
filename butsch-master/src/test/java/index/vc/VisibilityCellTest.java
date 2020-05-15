package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import geometry.BoundingBox;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// TODO Visibility Cell now created by ingoring the first node, change it to have the first nodeId to be the first element of the polygon (cell shape).
public class VisibilityCellTest {
    private static GeometryFactory gf = new GeometryFactory();
    private static PolygonRoutingTestGraph graphMocker = graphMocker = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
    private static RoadGraph graph = graphMocker.graph;

    private VisibilityCell createDefaultVisibilityCell() {
        final List<Node> visibilityCellNodeIds = Arrays.asList(
                graph.getVertex(17),
                graph.getVertex(15),
                graph.getVertex(18));
        return VisibilityCell.create(visibilityCellNodeIds, null);
    }

    @Test
    public void cellShape() {
        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
        final Polygon expectedPolygon = createDefaultVisibilityCellsExpectedCellShape();
        final VisibilityCell expectedVC = VisibilityCell.create(expectedPolygon, null);

        assertEquals(expectedVC, visibilityCell);
    }

    private Polygon createDefaultVisibilityCellsExpectedCellShape() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(32, 7),
                new Coordinate(34, 11),
                new Coordinate(38, 7),
                new Coordinate(32, 7)
        };
        return gf.createPolygon(coordinates);
    }

    @Test
    public void surroundingPolygonNotIntersected() {
        final Polygon polygon = createSurrndingPolygon();
        final VisibilityCell visibilityCell = createDefaultVisibilityCell();

        assertFalse(visibilityCell.intersects(polygon));
    }

    private Polygon createSurrndingPolygon() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(31, 6),
                new Coordinate(31, 12),
                new Coordinate(39, 12),
                new Coordinate(39, 6),
                new Coordinate(31, 6)
        };
        return gf.createPolygon(coordinates);
    }

    @Test
    public void intersectingPolygonIntersected() {
        final Polygon polygon = createIntersectingPolygon();
        final VisibilityCell visibilityCell = createDefaultVisibilityCell();

        assertTrue(visibilityCell.intersects(polygon));
    }

    private Polygon createIntersectingPolygon() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(34, 6),
                new Coordinate(34, 8),
                new Coordinate(36, 8),
                new Coordinate(36, 6),
                new Coordinate(34, 6)
        };
        return gf.createPolygon(coordinates);
    }

    @Test
    public void surroundingBoundingBoxOverlapping() {
        fail();
//        final GridCell gridCell = createSurroundingGridCell();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//
//        assertTrue(visibilityCell.isOverlapping(gridCell));
    }

//    private GridCell createSurroundingGridCell() {
//        final BBox boundingBox = new BBox(31, 39, 6, 12);
//        return new GridCell(boundingBox);
//    }

    @Test
    public void visibilityCellInternalGridCellIsOverlapping() {
        fail();
//        final GridCell gridCell = createInternalGridCell();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//
//        assertTrue(visibilityCell.isOverlapping(gridCell));
    }

//    private GridCell createInternalGridCell() {
//        final BBox boundingBox = new BBox(34, 35, 8, 9);
//        return new GridCell(boundingBox);
//    }

    @Test
    public void equalsOtherVisiblityCell() {
        final VisibilityCell visibilityCell1 = createDefaultVisibilityCell();
        final VisibilityCell visibilityCell2 = createDefaultVisibilityCell();

        assertEquals(visibilityCell1, visibilityCell2);
    }

    @Test
    public void unEqualOtherVisibilityCell() {
        final VisibilityCell visibilityCell1 = createDefaultVisibilityCell();
        final VisibilityCell visibilityCell2 = createVisibilityCellOtherThanDefaultVisibilityCell();

        assertNotEquals(visibilityCell1, visibilityCell2);
    }

    private VisibilityCell createVisibilityCellOtherThanDefaultVisibilityCell() {
        final List<Node> vcNodeId = Arrays.asList(
                graph.getVertex(34),
                graph.getVertex(15),
                graph.getVertex(17));
        return VisibilityCell.create(vcNodeId, null);
    }

    @Test
    public void equalWithCellShape() {
        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
        final Polygon cellShape = createDefaultVisibilityCellsExpectedCellShape();
        final VisibilityCell expectedVC = VisibilityCell.create(cellShape, null);

        assertEquals(visibilityCell, expectedVC);
    }

    @Test
    public void unequalWithCellShape() {
        fail();
        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
        final Polygon otherCellShape = createVisibilityCellOtherThanDefaultVisibilityCell().toPolygon();

        assertNotEquals(visibilityCell, otherCellShape);
    }

    @Test
    public void containsInner() {
        fail();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//        final double internalLatitude = 8;
//        final double internalLongitude = 35;
//
//        assertTrue(visibilityCell.contains(internalLatitude, internalLongitude));
    }

    @Test
    public void containsMinimallyInner() {
        fail();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//        final double internalLatitude = 7 + Double.MIN_VALUE;
//        final double internalLongitude = 35;
//
//        assertTrue(visibilityCell.contains(internalLatitude, internalLongitude));
    }

    @Test
    public void notContainsOutside() {
        fail();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//        final double outerLatitude = 6;
//        final double innerLongitude = 35;
//
//        assertFalse(visibilityCell.contains(outerLatitude, innerLongitude));
    }

    @Test
    public void notContainsMinimallyOutside() {
        fail();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//        final double outerLatitude = 6.999999999;
//        final double innerLongitude = 35;
//
//        assertFalse(visibilityCell.contains(outerLatitude, innerLongitude));
    }

    @Test
    public void containsLine() {
        fail();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//
//        assertTrue(visibilityCell.contains(7, 35));
    }

    @Test
    public void containsCorner() {
        fail();
//        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
//
//        assertTrue(visibilityCell.contains(7, 32));
    }

    @Test
    public void getMinimalBoundingBox() {
        final VisibilityCell visibilityCell = createDefaultVisibilityCell();
        final BoundingBox boundingBox = new BoundingBox(32, 38, 7, 11);

        assertEquals(boundingBox, visibilityCell.getBoundingBox());
    }

    @Test
    public void subGraphBuilding() {
        final RoadGraph originalGraph = graphMocker.graph;

        final Node vertex0 = originalGraph.getVertex(0);
        final Node vertex1 = originalGraph.getVertex(1);
        final Node vertex7 = originalGraph.getVertex(7);
        final Node vertex8 = originalGraph.getVertex(8);
        final Node vertex44 = originalGraph.getVertex(44);

        final List<Node> vc1nodes = Arrays.asList(vertex0, vertex1, vertex7);
        final List<Node> vc2nodes = Arrays.asList(vertex1, vertex8, vertex44, vertex7);

        final List<ReflectiveEdge> vc1edges = Arrays.asList(
                new ReflectiveEdge(0, vertex0, vertex1, originalGraph),
                new ReflectiveEdge(1, vertex1, vertex7, originalGraph),
                new ReflectiveEdge(2, vertex7, vertex0, originalGraph)
        );

        final List<ReflectiveEdge> vc2edges = Arrays.asList(
                new ReflectiveEdge(3, vertex1, vertex8, originalGraph),
                new ReflectiveEdge(4, vertex8, vertex44, originalGraph),
                new ReflectiveEdge(5, vertex44, vertex7, originalGraph),
                new ReflectiveEdge(6, vertex7, vertex1, originalGraph)
        );

        final VisibilityCell vc1 = VisibilityCell.create(vc1nodes, vc1edges);
        final VisibilityCell vc2 = VisibilityCell.create(vc2nodes, vc2edges);

        final RoadGraph newGraph = new RoadGraph(Edge.class);

        vc1.insertVCNodesAndEdgesInto(originalGraph, newGraph);
        vc2.insertVCNodesAndEdgesInto(originalGraph, newGraph);

        assertNotNull(newGraph.getVertex(0));
        assertNotNull(newGraph.getVertex(1));
        assertNotNull(newGraph.getVertex(7));
        assertNotNull(newGraph.getVertex(8));
        assertNotNull(newGraph.getVertex(44));

        assertNull(newGraph.getVertex(2));

        assertNotNull(newGraph.getEdge(vertex0, vertex1));
        assertNotNull(newGraph.getEdge(vertex0, vertex7));
        assertNotNull(newGraph.getEdge(vertex1, vertex7));
        assertNotNull(newGraph.getEdge(vertex1, vertex8));
        assertNotNull(newGraph.getEdge(vertex7, vertex44));
        assertNotNull(newGraph.getEdge(vertex8, vertex44));

        assertNotNull(newGraph.getEdge(vertex1, vertex0));
        assertNotNull(newGraph.getEdge(vertex7, vertex0));
        assertNotNull(newGraph.getEdge(vertex7, vertex1));
        assertNotNull(newGraph.getEdge(vertex8, vertex1));
        assertNotNull(newGraph.getEdge(vertex44, vertex7));
        assertNotNull(newGraph.getEdge(vertex44, vertex8));

        assertNull(newGraph.getEdge(graph.getVertex(7), graph.getVertex(43)));
    }
}
