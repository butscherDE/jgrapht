package index;

import data.Edge;
import data.Node;
import geometry.BoundingBox;
import org.locationtech.jts.geom.LineSegment;

import java.util.List;

public interface Index {
    Node getClosestNode(double longitude, double latitude);

    Edge getClosestEdge(double longitude, double latitude);

    List<Edge> getIntersectingEdges(LineSegment lineSegment);

    void queryNodes(final BoundingBox limiter, final IndexVisitor visitor);

    void queryEdges(final BoundingBox limiter, final IndexVisitor visitor);

    interface IndexVisitor<T> {
        void accept(final T entity);
    }
}
