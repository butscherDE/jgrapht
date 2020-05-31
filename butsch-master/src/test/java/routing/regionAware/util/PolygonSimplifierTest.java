package routing.regionAware.util;

import data.Node;
import geometry.PolygonContainsChecker;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class PolygonSimplifierTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;
    private final PolygonSimplifier ps;

    public PolygonSimplifierTest(PolygonSimplifier ps) {
        this.ps = ps;
    }

    @Test
    public void run() {
        Polygon polygon = SimplerPolygonContractionSetBuilderTest.getTestPolygonGeneral();

        final Polygon simplified = ps.simplify(polygon);
        final PolygonContainsChecker pcc = new PolygonContainsChecker(simplified);

        for (int i = 0; i <= 53; i++) {
            final Node vertexI = GRAPH_MOCKER.graph.getVertex(i);
            final Point vertexLocation = vertexI.getPoint();
            assertFalse(pcc.contains(vertexLocation));
        }

        for (int i = 54; i < 57; i++) {
            final Node vertexI = GRAPH_MOCKER.graph.getVertex(i);
            final Point vertexLocation = vertexI.getPoint();
            assertTrue(pcc.contains(vertexLocation));
        }
    }
}
