package index;

import data.Edge;
import data.Node;
import geometry.BoundingBox;
import org.locationtech.jts.geom.LineSegment;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface Index {
    Node getClosestNode(double longitude, double latitude);

    Edge getClosestEdge(double longitude, double latitude);

    List<Edge> getIntersectingEdges(LineSegment lineSegment);

    void queryNodes(final BoundingBox limiter, final Consumer<Node> visitor);
}
