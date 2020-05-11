package index.vc;

import com.graphhopper.storage.NodeAccess;

public class VectorAngleCalculatorLeft extends VectorAngleCalculator {
    public VectorAngleCalculatorLeft(NodeAccess nodeAccess) {
        super(nodeAccess);
    }

    @Override
    public double getAngleOfVectorsOriented(final int baseNode, final int adjNode) {
        return getAngle(baseNode, adjNode);
    }
}
