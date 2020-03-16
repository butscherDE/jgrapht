package data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

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
        final List<Long> nodeIds = new LinkedList<>(Arrays.asList(0l, 1l, 2l));

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
}
