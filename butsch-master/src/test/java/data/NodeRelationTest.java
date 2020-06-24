package data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class NodeRelationTest {
    private NodeRelation nodeRelationFromNodes;
    private NodeRelation nodeRelationFromIds;

    @BeforeEach
    public void setNodeRelationFromNodes() {
        final List<Node> nodes = new LinkedList<>();
        nodes.add(new Node(0, 0, 0, 0));
        nodes.add(new Node(1, 1, 1, 1));
        nodes.add(new Node(2, 2, 2, 2));

        nodeRelationFromNodes = new NodeRelation(0, "desc", Collections.singletonMap("type", "boundary"), nodes);
    }

    @BeforeEach
    public void setNodeRelationFromNodeIds() {
        final List<Long> nodeIds = new LinkedList<>(Arrays.asList(0L, 1L, 2L));

        final RoadGraph graph = new RoadGraph(Edge.class);
        graph.addVertex(new Node(0, 0, 0, 0));
        graph.addVertex(new Node(1, 1, 1, 1));
        graph.addVertex(new Node(2, 2, 2, 2));

        nodeRelationFromIds = NodeRelation
                .createFromNodeIds(0, "desc", Collections.singletonMap("type", "boundary"), nodeIds, graph);
    }

    @Test
    public void relationIdNodes() {
        System.out.println(nodeRelationFromNodes);
        assertEquals(0, nodeRelationFromNodes.id);
    }

    @Test
    public void relationDescriptionNodes() {
        assertEquals("desc", nodeRelationFromNodes.description);
    }

    @Test
    public void relationTagsNodes() {
        assertEquals(Collections.singletonMap("type", "boundary"), nodeRelationFromNodes.tags);
    }

    @Test
    public void relationNodesNodes() {
        for (int i = 0; i < nodeRelationFromNodes.nodes.size(); i++) {
            assertEquals(i, nodeRelationFromNodes.nodes.get(i).id);
        }
    }

    @Test
    public void relationPolygonNodes() {
        final Polygon polygon = nodeRelationFromNodes.toPolygon();

        final Polygon expectedPolygon = createExpectedPolygon();
        assertEquals(expectedPolygon, polygon);
    }

    @Test
    public void relationIdIds() {
        assertEquals(0, nodeRelationFromIds.id);
    }

    @Test
    public void relationDescriptionIds() {
        assertEquals("desc", nodeRelationFromIds.description);
    }

    @Test
    public void relationTagsIds() {
        assertEquals(Collections.singletonMap("type", "boundary"), nodeRelationFromIds.tags);
    }

    @Test
    public void relationNodesIds() {
        for (int i = 0; i < nodeRelationFromIds.nodes.size(); i++) {
            assertEquals(i, nodeRelationFromNodes.nodes.get(i).id);
        }
    }

    @Test
    public void relationPolygonIds() {
        final Polygon polygon = nodeRelationFromIds.toPolygon();

        final Polygon expectedPolygon = createExpectedPolygon();
        assertEquals(expectedPolygon, polygon);
    }

    private Polygon createExpectedPolygon() {
        final Coordinate[] coordinates = new Coordinate[4];
        coordinates[0] = new Coordinate(0,0);
        coordinates[1] = new Coordinate(1,1);
        coordinates[2] = new Coordinate(2,2);
        coordinates[3] = coordinates[0];

        return new GeometryFactory().createPolygon(coordinates);
    }

    @Test
    public void testToString() {
        final List<Node> list = Arrays.asList(new Node(0,0,0,0),
                                              new Node(1,1,1,1),
                                              new Node(2,2,2,2));
        final NodeRelation nodeRelation = new NodeRelation(1, "desc", Collections.emptyMap(), list);

        final String expectedString = "NodeRelation(1): [" + list.get(0).toString() + ", "
                                                          + list.get(1).toString() + ", "
                                                          + list.get(2).toString() + "]";
        final String actualString = nodeRelation.toString();

        assertEquals(expectedString, actualString);
    }

    @Test
    public void dump() {
        final RoadGraph graph = PolygonRoutingTestGraph.DEFAULT_INSTANCE.graph;
        final NodeRelation nodeRelation = getDumpRelation(graph);

        final String dump = nodeRelation.dump();
        assertEquals("1|desc|highway=primary,oneway=yes|0,1,2", dump);
    }

    @Test
    public void reimport() {
        final RoadGraph graph = PolygonRoutingTestGraph.DEFAULT_INSTANCE.graph;

        final String dump = getDumpRelation(graph).dump();
        final NodeRelation reimportedRelation = NodeRelation.createFromDump(dump, graph);

        assertEquals(25, reimportedRelation.nodes.get(0).latitude);
        assertEquals(dump, reimportedRelation.dump());
    }

    public NodeRelation getDumpRelation(final RoadGraph graph) {
        final List<Node> list = Arrays.asList(graph.getVertex(0),
                                              graph.getVertex(1),
                                              graph.getVertex(2));
        final Map<String, String> tags = new HashMap<>();
        tags.put("highway", "primary");
        tags.put("oneway", "yes");
        return new NodeRelation(1, "desc", tags, list);
    }
}
