package index.vc;

import data.Node;
import data.RoadGraph;

public class VectorAngleCalculatorLeft extends VectorAngleCalculator {
    public VectorAngleCalculatorLeft(final RoadGraph graph) {
        super(graph);
    }

    @Override
    public double getAngleOfVectorsOriented(final Node baseNode, final Node adjNode) {
        final double angle = getAngle(baseNode, adjNode);
        return angle;
    }
}
