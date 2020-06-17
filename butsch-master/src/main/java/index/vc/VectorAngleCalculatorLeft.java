package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.jgrapht.Graph;

public class VectorAngleCalculatorLeft extends VectorAngleCalculator {
    public VectorAngleCalculatorLeft(final Graph<Node, Edge> graph) {
        super(graph);
    }

    @Override
    public double getAngleOfVectorsOriented(final Node baseNode, final Node adjNode) {
        final double angle = getAngle(baseNode, adjNode);
        return angle;
    }
}
