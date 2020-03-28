package routing.regionAware.util;

import data.Node;
import data.RoadGraph;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegionSubGraphBuilderTest {
    @Test
    public void GeneralTestGraphSubGraph() {
        final PolygonRoutingTestGraph graphMocker = new PolygonRoutingTestGraph();

        final RoadGraph graph = graphMocker.graph;
        final Polygon polygon = graphMocker.polygon;
        final List<Node> whitelist = new LinkedList<>(Arrays.asList(graph.getVertex(44), graph.getVertex(28)));

        final RoadGraph subGraph = new RegionSubGraphBuilder().getSubGraph(graph, polygon, whitelist);

        assertNull(subGraph.getVertex(0));
        assertNull(subGraph.getVertex(1));
        assertNull(subGraph.getVertex(2));
        assertNull(subGraph.getVertex(3));
        assertNull(subGraph.getVertex(4));
        assertNull(subGraph.getVertex(5));
        assertNull(subGraph.getVertex(6));
        assertNull(subGraph.getVertex(7));
        assertNull(subGraph.getVertex(8));
        assertNull(subGraph.getVertex(9));
        assertNull(subGraph.getVertex(10));
        assertNull(subGraph.getVertex(11));
        assertNull(subGraph.getVertex(12));
        assertNull(subGraph.getVertex(13));
        assertNull(subGraph.getVertex(14));
        assertNull(subGraph.getVertex(15));
        assertNull(subGraph.getVertex(16));
        assertNull(subGraph.getVertex(17));
        assertNull(subGraph.getVertex(18));
        assertNull(subGraph.getVertex(19));
        assertNull(subGraph.getVertex(20));
        assertNull(subGraph.getVertex(21));
        assertNull(subGraph.getVertex(22));
        assertNull(subGraph.getVertex(23));
        assertNull(subGraph.getVertex(24));
        assertNull(subGraph.getVertex(25));
        assertNull(subGraph.getVertex(26));
        assertNull(subGraph.getVertex(27));
        assertNull(subGraph.getVertex(29));
        assertNull(subGraph.getVertex(30));
        assertNull(subGraph.getVertex(31));
        assertNull(subGraph.getVertex(32));
        assertNull(subGraph.getVertex(33));
        assertNull(subGraph.getVertex(34));
        assertNull(subGraph.getVertex(35));
        assertNull(subGraph.getVertex(36));
        assertNull(subGraph.getVertex(37));
        assertNull(subGraph.getVertex(38));
        assertNull(subGraph.getVertex(39));
        assertNull(subGraph.getVertex(40));
        assertNull(subGraph.getVertex(41));
        assertNull(subGraph.getVertex(42));
        assertNull(subGraph.getVertex(43));
        assertNull(subGraph.getVertex(45));

        assertNotNull(subGraph.getVertex(28));
        assertNotNull(subGraph.getVertex(44));
        assertNotNull(subGraph.getVertex(46));
        assertNotNull(subGraph.getVertex(47));
        assertNotNull(subGraph.getVertex(48));
        assertNotNull(subGraph.getVertex(49));
        assertNotNull(subGraph.getVertex(50));
        assertNotNull(subGraph.getVertex(51));
        assertNotNull(subGraph.getVertex(52));
        assertNotNull(subGraph.getVertex(53));
        assertNotNull(subGraph.getVertex(54));
        assertNotNull(subGraph.getVertex(55));
        assertNotNull(subGraph.getVertex(56));
        assertNotNull(subGraph.getVertex(57));

        assertNull(subGraph.getEdge(graph.getVertex(0), graph.getVertex(2)));
        assertNull(subGraph.getEdge(graph.getVertex(42), graph.getVertex(53)));
        assertNull(subGraph.getEdge(graph.getVertex(43), graph.getVertex(44)));
        assertNotNull(subGraph.getEdge(graph.getVertex(44), graph.getVertex(46)));
        assertNotNull(subGraph.getEdge(graph.getVertex(46), graph.getVertex(54)));
    }

    @Test
    public void testBlackListRegionHere() {
        final PolygonRoutingTestGraph graphMocker = new PolygonRoutingTestGraph();

        final RoadGraph graph = graphMocker.graph;
        final Polygon whitePolygon = graphMocker.polygon;
        final Coordinate[] blackCoordinates = {
                new Coordinate(17, 14),
                new Coordinate(17, 16),
                new Coordinate(19,16),
                new Coordinate(19, 14),
                new Coordinate(17, 14)};
        final Polygon blackPolygon = new GeometryFactory().createPolygon(blackCoordinates);
        final List<Node> whitelist = new LinkedList<>(Arrays.asList(graph.getVertex(44), graph.getVertex(28)));

        final RoadGraph subGraph = new RegionSubGraphBuilder().getSubGraph(graph, whitePolygon, blackPolygon, whitelist);

        assertNull(subGraph.getVertex(0));
        assertNull(subGraph.getVertex(1));
        assertNull(subGraph.getVertex(2));
        assertNull(subGraph.getVertex(3));
        assertNull(subGraph.getVertex(4));
        assertNull(subGraph.getVertex(5));
        assertNull(subGraph.getVertex(6));
        assertNull(subGraph.getVertex(7));
        assertNull(subGraph.getVertex(8));
        assertNull(subGraph.getVertex(9));
        assertNull(subGraph.getVertex(10));
        assertNull(subGraph.getVertex(11));
        assertNull(subGraph.getVertex(12));
        assertNull(subGraph.getVertex(13));
        assertNull(subGraph.getVertex(14));
        assertNull(subGraph.getVertex(15));
        assertNull(subGraph.getVertex(16));
        assertNull(subGraph.getVertex(17));
        assertNull(subGraph.getVertex(18));
        assertNull(subGraph.getVertex(19));
        assertNull(subGraph.getVertex(20));
        assertNull(subGraph.getVertex(21));
        assertNull(subGraph.getVertex(22));
        assertNull(subGraph.getVertex(23));
        assertNull(subGraph.getVertex(24));
        assertNull(subGraph.getVertex(25));
        assertNull(subGraph.getVertex(26));
        assertNull(subGraph.getVertex(27));
        assertNull(subGraph.getVertex(29));
        assertNull(subGraph.getVertex(30));
        assertNull(subGraph.getVertex(31));
        assertNull(subGraph.getVertex(32));
        assertNull(subGraph.getVertex(33));
        assertNull(subGraph.getVertex(34));
        assertNull(subGraph.getVertex(35));
        assertNull(subGraph.getVertex(36));
        assertNull(subGraph.getVertex(37));
        assertNull(subGraph.getVertex(38));
        assertNull(subGraph.getVertex(39));
        assertNull(subGraph.getVertex(40));
        assertNull(subGraph.getVertex(41));
        assertNull(subGraph.getVertex(42));
        assertNull(subGraph.getVertex(43));
        assertNull(subGraph.getVertex(45));
        assertNull(subGraph.getVertex(54));

        assertNotNull(subGraph.getVertex(28));
        assertNotNull(subGraph.getVertex(44));
        assertNotNull(subGraph.getVertex(46));
        assertNotNull(subGraph.getVertex(47));
        assertNotNull(subGraph.getVertex(48));
        assertNotNull(subGraph.getVertex(49));
        assertNotNull(subGraph.getVertex(50));
        assertNotNull(subGraph.getVertex(51));
        assertNotNull(subGraph.getVertex(52));
        assertNotNull(subGraph.getVertex(53));
        assertNotNull(subGraph.getVertex(55));
        assertNotNull(subGraph.getVertex(56));
        assertNotNull(subGraph.getVertex(57));

        assertNull(subGraph.getEdge(graph.getVertex(0), graph.getVertex(2)));
        assertNull(subGraph.getEdge(graph.getVertex(42), graph.getVertex(53)));
        assertNull(subGraph.getEdge(graph.getVertex(43), graph.getVertex(44)));
        assertNull(subGraph.getEdge(graph.getVertex(46), graph.getVertex(54)));
        assertNotNull(subGraph.getEdge(graph.getVertex(44), graph.getVertex(46)));
    }
}
