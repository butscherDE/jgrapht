package index;

import data.Edge;
import data.Node;
import org.locationtech.jts.geom.LineSegment;

import java.util.List;

public interface Index {
    Node getClosestNode(double longitude, double latitude);

    Edge getClosestEdge(double longitude, double latitude);

    List<Edge> getIntersectingEdges(LineSegment lineSegment);

}
