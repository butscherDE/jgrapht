package routing.regionAware.util;

import data.Node;
import data.RegionOfInterest;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntryExitPointExtractorTest {
    private static PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();

    @Test
    public void entryExitNodes() {
        final List<Node> expectedEntryExitNodes = getExpectedEntryExitNodes();
        final List<Node> actualEntryExitNodes = getEntryExitNodes();
        sortResults(expectedEntryExitNodes, actualEntryExitNodes);

        assertEquals(expectedEntryExitNodes, actualEntryExitNodes);
    }

    public List<Node> getExpectedEntryExitNodes() {
        final List<Node> expectedEntryExitNodes = new LinkedList<>();
        expectedEntryExitNodes.add(new Node(28, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(29, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(30, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(31, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(32, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(33, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(34, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(35, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(36, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(37, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(38, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(39, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(40, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(41, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(42, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(43, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(44, 0, 0, 0));
        expectedEntryExitNodes.add(new Node(45, 0, 0, 0));
        return expectedEntryExitNodes;
    }

    public List<Node> getEntryExitNodes() {
        final EntryExitPointExtractor entryExitPointExtractor =
                new EntryExitPointExtractor(GRAPH_MOCKER.roi, GRAPH_MOCKER.gridIndex);
        return new LinkedList<>(entryExitPointExtractor.extract());
    }

    public void sortResults(final List<Node> expectedEntryExitNodes, final List<Node> actualEntryExitNodes) {
        Collections.sort(expectedEntryExitNodes, Comparator.comparingLong(n -> n.id));
        Collections.sort(actualEntryExitNodes, Comparator.comparingLong(n -> n.id));
    }

    @Test
    public void inner4Simple() {
        final Coordinate[] roiCoordinates = new Coordinate[] {
                new Coordinate(17, 12),
                new Coordinate(17, 16),
                new Coordinate(21, 16),
                new Coordinate(21, 12),
                new Coordinate(17, 12)
        };
        assertInner4(roiCoordinates);
    }

    @Test
    public void inner4Sophisticated() {
        final Coordinate[] roiCoordinates = new Coordinate[] {
                new Coordinate(16, 15),
                new Coordinate(17, 16),
                new Coordinate(18, 17),
                new Coordinate(18, 16),
                new Coordinate(20, 16),
                new Coordinate(19, 17),
                new Coordinate(20, 17),
                new Coordinate(21, 16),
                new Coordinate(21, 15),
                new Coordinate(21, 14),
                new Coordinate(21, 13),
                new Coordinate(20, 12),
                new Coordinate(16, 12),
                new Coordinate(16, 14),
                new Coordinate(17, 14),
                new Coordinate(16, 15)
        };
        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        col.addGraph(Color.BLACK, GRAPH_MOCKER.graph);
        col.addPolygon(Color.RED, new GeometryFactory().createPolygon(roiCoordinates));
        final GeometryVisualizer vis = new GeometryVisualizer(col);
//        vis.visualizeGraph(100000);
        assertInner4(roiCoordinates);
    }

    private void assertInner4(Coordinate[] roiCoordinates) {
        final Set<Node> expectedEntryExitNodes = getInner4ExpectedNodes();
        final Polygon roiPolygon = new GeometryFactory().createPolygon(roiCoordinates);
        final RegionOfInterest roi = new RegionOfInterest(roiPolygon);

        final EntryExitPointExtractor eepe = new EntryExitPointExtractor(roi, GRAPH_MOCKER.gridIndex);
        final Set<Node> actualEntryExitNodes = eepe.extract();

        assertEquals(expectedEntryExitNodes, actualEntryExitNodes);
    }

    private Set<Node> getInner4ExpectedNodes() {
        final Set<Node> expectedEntryExitNodes = new LinkedHashSet<>();
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(46));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(47));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(48));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(49));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(50));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(51));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(52));
        expectedEntryExitNodes.add(GRAPH_MOCKER.graph.getVertex(53));
        return expectedEntryExitNodes;
    }
}
