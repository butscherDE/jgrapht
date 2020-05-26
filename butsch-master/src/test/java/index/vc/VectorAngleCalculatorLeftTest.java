package index.vc;

import data.Node;
import data.RoadGraph;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VectorAngleCalculatorLeftTest {
    private final PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();
    private final RoadGraph graph = GRAPH_MOCKER.graph;

    @Test
    public void anglesToNode39to38right() {
        final Node vertex = graph.getVertex(21);
        graph.removeVertex(vertex);
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 15, 6, 0));
        graph.addVertex(new Node(38, 15, 6, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final double angle = vac.getAngleOfVectorsOriented(graph.getVertex(39), graph.getVertex(21));
        assertEquals(0, angle, 0);
    }

    @Test
    public void anglesToNode39to38left() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 13, 6, 0));
        graph.addVertex(new Node(38, 15, 6, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final double angle = vac.getAngleOfVectorsOriented(graph.getVertex(39), graph.getVertex(21));
        assertEquals(Math.PI, angle, 0);
    }

    @Test
    public void anglesToNode39to38below() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 14, 5, 0));
        graph.addVertex(new Node(38, 15, 6, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final double angle = vac.getAngleOfVectorsOriented(graph.getVertex(39), graph.getVertex(21));
        assertEquals(Math.PI * 1.5, angle, 0);
    }

    @Test
    public void anglesToNode39to38above() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 14, 7, 0));
        graph.addVertex(new Node(38, 15, 6, 0));
        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final double angle = vac.getAngleOfVectorsOriented(graph.getVertex(39), graph.getVertex(21));
        assertEquals(Math.PI * 0.5, angle, 0);
    }

    @Test
    public void anglesOnNodesWithEqualCoordinatesLastEdge() {
        graph.removeVertex(graph.getVertex(0));
        graph.removeVertex(graph.getVertex(1));
        graph.addVertex(new Node(0, 0, 25, 0));
        graph.addVertex(new Node(1, 0, 25, 0));

        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final double angle = vac.getAngleOfVectorsOriented(graph.getVertex(0), graph.getVertex(1));
        assertEquals(-Double.MAX_VALUE, angle, 0);
    }

    @Test
    public void anglesOnNodesWithEqualCoordinatesCandidateEdge() {
        graph.removeVertex(graph.getVertex(21));
        graph.removeVertex(graph.getVertex(38));
        graph.addVertex(new Node(21, 14, 6, 0));
        graph.addVertex(new Node(38, 15, 6, 0));

        final VectorAngleCalculator vac = new VectorAngleCalculatorLeft(graph);

        final double angle = vac.getAngleOfVectorsOriented(graph.getVertex(39), graph.getVertex(21));
        assertEquals(-Double.MAX_VALUE, angle, 0);
    }
}
