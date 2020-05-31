package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.RoadGraph;
import index.GridIndex;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimplerPolygonContractionSetBuilderTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    @Test
    public void test1() {
        final Polygon polygon = getTestPolygonGeneral();

        SimplerPolygonContractionSetBuilder sp = new SimplerPolygonContractionSetBuilder(GRAPH_MOCKER.gridIndex, polygon);
        final int actualSetSize = sp.getContractionSetSize(12);
        assertEquals(7, actualSetSize);
    }


    private Polygon getTestPolygonGeneral() {
        final Coordinate[] coordinates = new Coordinate[] {
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
                new Coordinate(19, 14),
                new Coordinate(16, 15)
        };

        return new GeometryFactory().createPolygon(coordinates);
    }

    @Test
    public void furtherEnlargingImprovesResult() {
        final GridIndex index = getTestPolygonExtendingFurtherHelpsGraph();
        final Polygon polygon = getTestPolygonExtendingFurtherHelps();

        SimplerPolygonContractionSetBuilder sp = new SimplerPolygonContractionSetBuilder(index, polygon);
        final int actualSetSize = sp.getContractionSetSize(4);
        assertEquals(3, actualSetSize);
    }

    private GridIndex getTestPolygonExtendingFurtherHelpsGraph() {
        final RoadGraph roadGraph = new RoadGraph(Edge.class);

        roadGraph.addVertex(new Node(0, 0, 3, 0));
        roadGraph.addVertex(new Node(1, 0, 5, 0));
        roadGraph.addVertex(new Node(2, 0, 7, 0));
        roadGraph.addVertex(new Node(3, 3, 0, 0));
        roadGraph.addVertex(new Node(4, 3, 3, 0));
        roadGraph.addVertex(new Node(5, 3, 7, 0));
        roadGraph.addVertex(new Node(6, 4, 5, 0));
        roadGraph.addVertex(new Node(7, 5, 3, 0));
        roadGraph.addVertex(new Node(8, 5, 7, 0));

        addEdge(roadGraph, 0, 4);
        addEdge(roadGraph, 0, 4);
        addEdge(roadGraph, 1, 6);
        addEdge(roadGraph, 2, 5);
        addEdge(roadGraph, 3, 4);
        addEdge(roadGraph, 4, 6);
        addEdge(roadGraph, 4, 7);
        addEdge(roadGraph, 5, 6);
        addEdge(roadGraph, 5, 8);
        addEdge(roadGraph, 7, 8);

        return new GridIndex(roadGraph, 10, 10);
    }

    private void addEdge(RoadGraph roadGraph, int i, int j) {
        roadGraph.addEdge(roadGraph.getVertex(i), roadGraph.getVertex(j));
        roadGraph.addEdge(roadGraph.getVertex(j), roadGraph.getVertex(i));
    }

    private Polygon getTestPolygonExtendingFurtherHelps() {
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(1, 3),
                new Coordinate(1, 8),
                new Coordinate(6, 8),
                new Coordinate(6, 2),
                new Coordinate(4, 2),
                new Coordinate(2, 3),
                new Coordinate(3, 5),
                new Coordinate(1, 3)
        };

        return new GeometryFactory().createPolygon(coordinates);
    }
}
