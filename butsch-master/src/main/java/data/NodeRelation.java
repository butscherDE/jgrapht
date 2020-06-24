package data;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.*;
import java.util.stream.Collectors;

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
                                                 final List<Long> nodeIds, final Map<Long, Node> nodeMap) {
        final List<Node> nodes = new LinkedList<>();

        for (final Long nodeId : nodeIds) {
            final Node vertex = nodeMap.get(nodeId);

            if (vertex != null) {
                nodes.add(vertex);
            } else {
                throw new NoSuchElementException("Vertex " + nodeId + " was not found in the graph.");
            }
        }

        return new NodeRelation(id, description, tags, nodes);
    }

    public static NodeRelation createFromNodeIds(final long id, final String description, Map<String, String> tags,
                                                 final List<Long> nodeIds, final RoadGraph graph) {
        final List<Node> nodes = new LinkedList<>();

        for (final Long nodeId : nodeIds) {
            final Node vertex = graph.getVertex(nodeId);

            if (vertex != null) {
                nodes.add(vertex);
            } else {
                throw new NoSuchElementException("Vertex " + nodeId + " was not found in the graph.");
            }
        }

        return new NodeRelation(id, description, tags, nodes);
    }

    public static NodeRelation createFromDump(final String dump, final RoadGraph graph) {
        final String[] elements = dump.split("\\|");
        final long id = Long.valueOf(elements[0]);
        final String description = elements[1];
        final Map<String, String> tags = getTags(elements[2]);
        final List<Long> nodes = Arrays
                .stream(elements[3].split(","))
                .map(n -> Long.valueOf(n))
                .collect(Collectors.toList());

        return createFromNodeIds(id, description, tags, nodes, graph);
    }

    private static Map<String, String> getTags(final String tagsRaw) {
        final Map<String, String> tags = Arrays
                .stream(tagsRaw.split(","))
                .map(rawTag -> rawTag.split("="))
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
        return tags;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NodeRelation that = (NodeRelation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NodeRelation(" + id + "): " + nodes.toString();
    }

    public String dump() {
        final String tagsRaw = this.tags.toString();
        final String tags = tagsRaw.substring(1, tagsRaw.length() - 1).replaceAll(", ", ",");
        final String nodeDump = nodes.stream().map(n -> String.valueOf(n.id)).collect(Collectors.joining(","));
        return id + "|" + description + "|" + tags + "|" + nodeDump;
    }
}
