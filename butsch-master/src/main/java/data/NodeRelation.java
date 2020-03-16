package data;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NodeRelation {
    public final long id;
    public final String description;
    public final Map<String, String> tags;
    public final List<Node> nodes;

    public NodeRelation(final long id, final String description, final Map<String, String> tags, final List<Node> nodes) {
        this.id = id;
        this.description = description;
        this.tags = tags;
        this.nodes = nodes;
    }

    public static NodeRelation createFromNodeIds(final long id, final String description, Map<String, String> tags,
                                                 final List<Long> nodeIds, final RoadGraph graph) {
        final List<Node> nodes = new LinkedList<>();

        for (final Long nodeId : nodeIds) {
            nodes.add(graph.getVertex(nodeId));
        }

        return new NodeRelation(id, description, tags, nodes);
    }

    public Polygon toPolygon() {
        final Coordinate[] coordinates = new Coordinate[nodes.size() + 1];

        final Iterator<Node> nodeIterator = nodes.iterator();
        for (int i = 0; i < coordinates.length - 1; i++) {
            final Node next = nodeIterator.next();
            coordinates[i] = new Coordinate(next.longitude, next.latitude);
        }
        coordinates[coordinates.length - 1] = coordinates[0];

        return new GeometryFactory().createPolygon(coordinates);
    }
}
