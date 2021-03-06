package data;

import geometry.BoundingBox;
import geometry.RedBlueSegmentIntersectionCrossProductFactory;
import geometry.SegmentIntersectionAlgorithm;
import index.vc.ReflectiveEdge;
import org.apache.commons.lang3.NotImplementedException;
import org.locationtech.jts.geom.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class VisibilityCell implements PolygonSegmentCollection {
    private static AtomicLong AUTO_ID = new AtomicLong(0);
    private static final GeometryFactory gf = new GeometryFactory();

    public final long id;
    public final List<LineSegment> lineSegments;
    private final Coordinate[] coordinates;
    public final List<ReflectiveEdge> edges;

    private VisibilityCell(final long id, final List<LineSegment> lineSegments, final Coordinate[] coordinates,
                           final List<ReflectiveEdge> edges) {
        this.id = id;
        this.lineSegments = lineSegments;
        this.coordinates = coordinates;
        this.edges = edges;

        Collections.sort(this.lineSegments, Comparator.comparingDouble(a -> a.p1.x));
    }

    public static VisibilityCell create(final Polygon polygon, final List<ReflectiveEdge> edges) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        return create(coordinates, edges);
    }

    public static VisibilityCell create(final Coordinate[] coordinates, final List<ReflectiveEdge> edges) {
        if (!coordinates[0].equals(coordinates[coordinates.length - 1])) {
            throw new IllegalArgumentException("Coordinates do not form a closed LineString");
        }

        final List<LineSegment> lineSegments = getLineSegments(coordinates);

        return new VisibilityCell(AUTO_ID.getAndIncrement(), lineSegments, coordinates, edges);
    }

    public static VisibilityCell create(final long id, final Coordinate[] coordinates, final List<ReflectiveEdge> edges) {
        if (!coordinates[0].equals(coordinates[coordinates.length - 1])) {
            throw new IllegalArgumentException("Coordinates do not form a closed LineString");
        }

        final List<LineSegment> lineSegments = getLineSegments(coordinates);

        return new VisibilityCell(id, lineSegments, coordinates, edges);
    }

    public static List<LineSegment> getLineSegments(final Coordinate[] coordinates) {
        final List<LineSegment> lineSegments = new ArrayList<>(coordinates.length - 1);

        for (int i = 0; i < coordinates.length - 1; i++) {
            final Coordinate startCoordinate = coordinates[i];
            final Coordinate endCoordinate = coordinates[i + 1];

            if (startCoordinate.x <= endCoordinate.x) {
                lineSegments.add(new LineSegment(startCoordinate, endCoordinate));
            } else {
                lineSegments.add(new LineSegment(endCoordinate, startCoordinate));
            }
        }
        return lineSegments;
    }

    public static VisibilityCell create(final List<Node> nodes, final List<ReflectiveEdge> edges) {
        final Coordinate[] coordinates = new Coordinate[nodes.size() + 1];
        final Iterator<Node> nodeIterator = nodes.iterator();
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodeIterator.next();
            coordinates[i] = node.getPoint().getCoordinate();
        }
        coordinates[coordinates.length - 1] = nodes.get(0).getPoint().getCoordinate();

        return create(coordinates, edges);
    }

    public static VisibilityCell create(final String dump, final RoadGraph graph) {
        final String[] split = dump.split("\\|", -1);

        final String id = split[0];
        final String coordinateDump = split[1];
        final String edgeDump = split[2];

        final String[] coordinatesDumps = coordinateDump.split(";");
        final String[] edgesDumps = edgeDump.split(";");

        final Coordinate[] coordinates = getCoordinatesFromDumps(coordinatesDumps);
        final List<ReflectiveEdge> edges = getEdgeListFromDumps(graph, edgesDumps);

        return create(Long.valueOf(id), coordinates, edges);
    }

    public static Coordinate[] getCoordinatesFromDumps(final String[] coordinatesDumps) {
        System.out.println(coordinatesDumps.length);
        return Arrays.stream(coordinatesDumps).map(cs -> {
                final String[] elements = cs.split(",");
                final Double x = Double.valueOf(elements[0]);
                final Double y = Double.valueOf(elements[1]);
                final Coordinate coordinate = new Coordinate(x, y);
                return coordinate;
            }).toArray(size -> new Coordinate[coordinatesDumps.length]);
    }

    public static List<ReflectiveEdge> getEdgeListFromDumps(final RoadGraph graph, final String[] edgesDumps) {
        return Arrays.stream(edgesDumps).map(es -> {
                final String[] elements = es.split(",");
                final Long edgeId = Long.valueOf(elements[0]);
                final Node sourceNode = graph.getVertex(Long.valueOf(elements[1]));
                final Node targetNode = graph.getVertex(Long.valueOf(elements[2]));
                final ReflectiveEdge edge = new ReflectiveEdge(edgeId, sourceNode, targetNode, graph);
                return edge;
            }).collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VisibilityCell that = (VisibilityCell) o;
        return lineSegments.equals(that.lineSegments);
    }

    public Polygon getPolygon() {
        return gf.createPolygon(coordinates);
    }

    public boolean intersects(final Polygon polygon) {
        final SegmentIntersectionAlgorithm intersectionAlgoInstance = new RedBlueSegmentIntersectionCrossProductFactory()
                .createInstance(this, polygon);
        return intersectionAlgoInstance.isIntersectionPresent();
    }

    public boolean contains(final Geometry geometry) {
        throw new NotImplementedException("Necessary?");
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.createFrom(getPolygon());
    }

    public void insertVCNodesAndEdgesInto(final RoadGraph originalGraph, final RoadGraph newGraph) {
        edges.stream().forEach(a -> addEdgeDataToNewGraph(originalGraph, newGraph, a));
    }

    public void addEdgeDataToNewGraph(final RoadGraph originalGraph, final RoadGraph newGraph, final ReflectiveEdge a) {
        addVertices(newGraph, a);
        addEdge(originalGraph, newGraph, a);
        addReverseEdge(originalGraph, newGraph, a);
    }

    public void addVertices(final RoadGraph newGraph, final ReflectiveEdge a) {
        newGraph.addVertex(a.source);
        newGraph.addVertex(a.target);
    }

    public void addEdge(final RoadGraph originalGraph, final RoadGraph newGraph, final ReflectiveEdge a) {
        if (originalGraph.getEdge(a.source, a.target) != null) {
            newGraph.addEdge(a.source, a.target);
        }
    }

    public void addReverseEdge(final RoadGraph originalGraph, final RoadGraph newGraph, final ReflectiveEdge a) {
        if (originalGraph.getEdge(a.target, a.source) != null) {
            newGraph.addEdge(a.target, a.source);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates);
    }

    @Override
    public String toString() {
        return "VisibilityCell{" + "coordinates=" + Arrays.toString(coordinates) + '}';
    }

    @Override
    public List<LineSegment> getSortedLineSegments() {
        return lineSegments;
    }

    public String dump() {
        final String coordinateDump = Arrays.stream(coordinates).map(c -> toDump(c)).collect(Collectors.joining(";"));
        final String edgeDump = edges.stream().map(edge -> toDump(edge)).collect(Collectors.joining(";"));

        return id + "|" + coordinateDump + "|" + edgeDump;
    }

    private static String toDump(final Coordinate coordinate) {
        return coordinate.getX() + "," + coordinate.getY();
    }

    private static String toDump(final ReflectiveEdge edge) {
        return edge.id + "," + edge.source.id + "," + edge.target.id;
    }
}
