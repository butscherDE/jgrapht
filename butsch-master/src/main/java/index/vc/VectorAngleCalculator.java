package index.vc;

import data.Node;
import data.Edge;
import data.RoadGraph;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

public abstract class VectorAngleCalculator {
    final static double ANGLE_WHEN_COORDINATES_ARE_EQUAL = -Double.MAX_VALUE;

    private final RoadGraph graph;

    public VectorAngleCalculator(final RoadGraph graph) {
        this.graph = graph;
    }

    public double getAngleOfVectorsOriented(final Edge candidateEdge) {
        final Node sourceNode = graph.getEdgeSource(candidateEdge);
        final Node targetNode = graph.getEdgeTarget(candidateEdge);
        return getAngleOfVectorsOriented(sourceNode, targetNode);
    }

    public abstract double getAngleOfVectorsOriented(final Node baseNode, final Node adjNode);

    double getAngle(final Node baseNode, final Node adjNode) {
        try {
            return getAngleAfterErrorHandling(baseNode, adjNode);
        } catch (IllegalArgumentException e) {
            return ANGLE_WHEN_COORDINATES_ARE_EQUAL;
        }
    }

    private double getAngleAfterErrorHandling(final Node baseNode, final Node adjNode) {
        final Vector2D lastEdgeVector = createHorizontalRightVector(baseNode);
        final Vector2D candidateEdgeVector = createVectorCorrespondingToEdge(baseNode, adjNode);

        return getAngle(lastEdgeVector, candidateEdgeVector);
    }

    private Vector2D createHorizontalRightVector(final Node baseNode) {
        final Coordinate candidateEdgeBaseNodeCoordinate = new Coordinate(baseNode.longitude, baseNode.latitude);
        final Coordinate coordinateToTheRightOfPrevious = new Coordinate(baseNode.longitude + 1, baseNode.latitude);

        return new Vector2D(candidateEdgeBaseNodeCoordinate, coordinateToTheRightOfPrevious);
    }

    private double getAngle(Vector2D lastEdgeVector, Vector2D candidateEdgeVector) {
        final double angleTo = lastEdgeVector.angleTo(candidateEdgeVector);
        final double angleToContinuousInterval = transformAngleToContinuousInterval(angleTo);

        return getAngleAsZeroIfCloseToTwoPi(angleToContinuousInterval);
    }

    private double getAngleAsZeroIfCloseToTwoPi(double angleToContinuousInterval) {
        final double differenceToTwoPi = Math.abs(2 * Math.PI - angleToContinuousInterval);
        return differenceToTwoPi < 0.000000000000001 ? 0 : angleToContinuousInterval;
    }

    private Vector2D createVectorCorrespondingToEdge(final Node baseNode, final Node adjNode) {
        return createVectorByNodeIds(baseNode, adjNode);
    }

    private Vector2D createVectorByNodeIds(final Node baseNode, final Node adjNode) {
        final Coordinate baseNodeCoordinate = baseNode.getPoint().getCoordinate();
        final Coordinate adjNodeCoordinate = adjNode.getPoint().getCoordinate();
        if (baseNodeCoordinate.equals2D(adjNodeCoordinate)) {
            throw new IllegalArgumentException("Coordinates of both edge end points shall not be equal");
        }
        return new Vector2D(baseNodeCoordinate, adjNodeCoordinate);
    }

    private double transformAngleToContinuousInterval(final double angleTo) {
        return angleTo > 0 ? angleTo : angleTo + 2 * Math.PI;
    }
}
