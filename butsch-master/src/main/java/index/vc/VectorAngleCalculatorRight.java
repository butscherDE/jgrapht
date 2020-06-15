package index.vc;

import data.Node;
import data.RoadGraph;

public class VectorAngleCalculatorRight extends VectorAngleCalculator {
    public VectorAngleCalculatorRight(final RoadGraph graph) {
        super(graph);
    }

    @Override
    public double getAngleOfVectorsOriented(final Node baseNode, final Node adjNode) {
        final double angle = getAngle(baseNode, adjNode);
        return angle == 0 || angle == ANGLE_WHEN_COORDINATES_ARE_EQUAL ? angle : angle * (-1) + 2 * Math.PI;
    }
}
