package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import util.BinaryHashFunction;

import java.util.Collection;

public class RegionSubGraphBuilder {
    private final BinaryHashFunction<Node> isNodeInRegion = new BinaryHashFunction<>();
    private GeometryFactory geometryFactory = new GeometryFactory();
    private RoadGraph graph;
    private RoadGraph subGraph;
    private Polygon region;
    private Collection<Node> whitelist;

    public RoadGraph getSubGraph(final RoadGraph graph, final Polygon region, final Collection<Node> whitelist) {
        this.graph = graph;
        this.subGraph = new RoadGraph(Edge.class);
        this.region = region;
        this.whitelist = whitelist;

        addWhitelistNodes();
        addNodes();
        addEdges();

        return subGraph;
    }

    public void addWhitelistNodes() {
        for (final Node node : whitelist) {
            subGraph.addVertex(node);
            isNodeInRegion.set(node, true);
        }
    }

    public void addNodes() {
        for (final Node node : graph.vertexSet()) {
            final Geometry point = toPoint(node);
            if (region.contains(point)) {
                isNodeInRegion.set(node, true);
                subGraph.addVertex(node);
            }
        }
    }

    public void addEdges() {
        for (final Edge edge : graph.edgeSet()) {
            final Node sourceNode = graph.getEdgeSource(edge);
            final Node targetNode = graph.getEdgeTarget(edge);

            if (isNodeInRegion.get(sourceNode) && isNodeInRegion.get(targetNode)) {
                final Edge newEdge = subGraph.addEdge(sourceNode, targetNode);
                subGraph.setEdgeWeight(newEdge, graph.getEdgeWeight(edge));
            }
        }
    }

    private Geometry toPoint(final Node node) {
        final Coordinate coordinate = new Coordinate(node.longitude, node.latitude, node.elevation);
        return geometryFactory.createPoint(coordinate);
    }
}
