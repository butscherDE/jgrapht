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

    @BeforeEach
    public void setNodeRelationFromNodes() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(0,0,0),
                new Coordinate(1,1,1),
                new Coordinate(2,2,2),
                new Coordinate(0,0,0)
        };

        nodeRelationFromNodes = new NodeRelation(0, "desc", Collections.singletonMap("type", "boundary"), coordinates);
    }


    @Test
    public void relationIdNodes() {
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
    public void relationPolygonNodes() {
        final Polygon polygon = nodeRelationFromNodes.toPolygon();

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
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(0,0,0),
                new Coordinate(1,1,1),
                new Coordinate(2,2,2),
                new Coordinate(0,0,0)
        };
        final NodeRelation nodeRelation = new NodeRelation(1, "desc", Collections.emptyMap(), coordinates);

        final String expectedString = "NodeRelation(1): [" + coordinates[0].toString() + ", "
                                                          + coordinates[1].toString() + ", "
                                                          + coordinates[2].toString() + ", "
                                                          + coordinates[3].toString() + "]";
        final String actualString = nodeRelation.toString();

        assertEquals(expectedString, actualString);
    }

    @Test
    public void dump() {
        final RoadGraph graph = PolygonRoutingTestGraph.DEFAULT_INSTANCE.graph;
        final NodeRelation nodeRelation = getDumpRelation(graph);

        final String dump = nodeRelation.dump();
        assertEquals("1|desc|highway=primary;oneway=yes|0.0,25.0,0.0;8.0,25.0,0.0;16.0,25.0,0.0;0.0,25.0,0.0", dump);
    }

    @Test
    public void reimport() {
        final RoadGraph graph = PolygonRoutingTestGraph.DEFAULT_INSTANCE.graph;

        final String dump = getDumpRelation(graph).dump();
        final NodeRelation reimportedRelation = NodeRelation.createFromDump(dump, graph);

        assertEquals(25, reimportedRelation.coordinates[0].y);
        assertEquals(dump, reimportedRelation.dump());
    }

    public NodeRelation getDumpRelation(final RoadGraph graph) {
        final Coordinate[] coordinates = new Coordinate[] {
                graph.getVertex(0).getPoint().getCoordinate(),
                graph.getVertex(1).getPoint().getCoordinate(),
                graph.getVertex(2).getPoint().getCoordinate(),
                graph.getVertex(0).getPoint().getCoordinate()
        };
        final Map<String, String> tags = new HashMap<>();
        tags.put("highway", "primary");
        tags.put("oneway", "yes");
        return new NodeRelation(1, "desc", tags, coordinates);
    }
}
