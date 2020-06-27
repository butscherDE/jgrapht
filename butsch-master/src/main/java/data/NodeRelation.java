package data;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeRelation {
    public final long id;
    public final String description;
    public final Map<String, String> tags;
    public final Coordinate[] coordinates;

    public NodeRelation(final long id, final String description, final Map<String, String> tags, final Coordinate[] coordinates) {
        this.id = id;
        this.description = description;
        this.tags = tags;
        this.coordinates = coordinates;
    }

    public static NodeRelation createFromNodeIds(final long id, final String description, Map<String, String> tags,
                                                 final List<Long> nodeIds, final Map<Long, Node> nodeMap) {
        final List<Node> nodes = collectNodes(nodeIds, nodeMap);
        final Coordinate[] coordinates = toCoordinates(nodes);

        return new NodeRelation(id, description, tags, coordinates);
    }

    private static List<Node> collectNodes(List<Long> nodeIds, Map<Long, Node> nodeMap) {
        final List<Node> nodes = new LinkedList<>();

        for (final Long nodeId : nodeIds) {
            final Node vertex = nodeMap.get(nodeId);

            if (vertex != null) {
                nodes.add(vertex);
            } else {
                throw new NoSuchElementException("Vertex " + nodeId + " was not found in the graph.");
            }
        }
        return nodes;
    }

    private static Coordinate[] toCoordinates(List<Node> nodes) {
        final Stream<Coordinate> nodesStream = nodes.stream()
                .map(n -> new Coordinate(n.longitude, n.latitude, n.elevation));
        final Node node0 = nodes.get(0);
        final Stream<Coordinate> appendStream = Stream.of(new Coordinate(node0.longitude, node0.latitude, node0.elevation));
        final Coordinate[] coordinates = Stream.concat(nodesStream, appendStream)
                .toArray(size -> new Coordinate[nodes.size() + 1]);
        return coordinates;
    }

    public static NodeRelation createFromDump(final String dump, final RoadGraph graph) {
        final String[] elements = dump.split("\\|");
        final long id = Long.valueOf(elements[0]);
        final String description = elements[1];
        final Map<String, String> tags = getTags(elements[2]);
        final Coordinate[] coordinates = Arrays.stream(elements[3].split(";"))
                .map(point -> {
                    final String[] split = point.split(",");
                    final double longitude = Double.valueOf(split[0]);
                    final double latitude = Double.valueOf(split[1]);
                    final double elevation = Double.valueOf(split[2]);

                    return new Coordinate(longitude, latitude, elevation);
                })
                .toArray(size -> new Coordinate[size]);

        return new NodeRelation(id, description, tags, coordinates);
    }

    private static Map<String, String> getTags(final String tagsRaw) {
        System.out.println(tagsRaw);
        final Map<String, String> tags = Arrays
                .stream(tagsRaw.split(";"))
                .map(rawTag -> {
                    System.out.println(rawTag);
                    return rawTag.split("=");
                })
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
        return tags;
    }

    public Polygon toPolygon() {
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
        return "NodeRelation(" + id + "): " + Arrays.toString(coordinates);
    }

    public String dump() {


        final String tagsRaw = this.tags.toString();
//        final String tags = tagsRaw.substring(1, tagsRaw.length() - 1).replaceAll(", ", ";");
        final String tags = this.tags.keySet()
                .stream()
                .map(key -> key + "=" + this.tags.get(key))
                .collect(Collectors.joining(";"));
        final String coordinateDump = Arrays.stream(coordinates)
                .map(c -> c.x + "," + c.y + "," + c.z)
                .collect(Collectors.joining(";"));

        return id + "|" + description + "|" + tags + "|" + coordinateDump;
    }
}
