package index.vc;


import data.Node;
import data.RoadGraph;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VectorAngleCalculatorTest {
    private final PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();
    private final RoadGraph graph = GRAPH_MOCKER.graph;

    @Test
    public void anglesToNode39to38right() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 6, 15, 0));
        graph.addVertex(new Node(38, 6, 15, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        assertEquals(0, vac.getAngle(graph.getVertex(39), graph.getVertex(21)), 0);
    }

    @Test
    public void anglesToNode39to38left() {
        graph.addVertex(new Node(21, 6, 13, 0));
        graph.addVertex(new Node(38, 6, 15, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        assertEquals(Math.PI, vac.getAngle(graph.getVertex(39), graph.getVertex(21)), 0);
    }

    @Test
    public void anglesToNode39to38below() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 5, 14, 0));
        graph.addVertex(new Node(38, 6, 15, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        assertEquals(Math.PI * 1.5, vac.getAngle(graph.getVertex(39), graph.getVertex(21)), 0);
    }

    @Test
    public void anglesToNode39to38above() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 5, 14, 0));
        graph.addVertex(new Node(38, 6, 15, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        assertEquals(Math.PI * 0.5, vac.getAngle(graph.getVertex(39), graph.getVertex(21)), 0);
    }
}
