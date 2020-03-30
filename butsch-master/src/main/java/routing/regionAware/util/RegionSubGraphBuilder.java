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
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private RoadGraph graph;
    private RoadGraph subGraph;
    private Polygon whiteRegion;
    private Polygon blackRegion;
    private Collection<Node> whitelist;

    public RoadGraph getSubGraph(final RoadGraph graph, final Polygon whiteRegion, final Collection<Node> whitelist) {
        this.graph = graph;
        this.subGraph = new RoadGraph(Edge.class);
        this.whiteRegion = whiteRegion;
        this.whitelist = whitelist;

        addWhitelistNodes();
        addNodesWhite();
        addEdges();

        return subGraph;
    }

    public RoadGraph getSubGraph(final RoadGraph graph, final Polygon whiteRegion, final Polygon blackRegion, final Collection<Node> whitelist) {
        this.graph = graph;
        this.subGraph = new RoadGraph(Edge.class);
        this.whiteRegion = whiteRegion;
        this.blackRegion = blackRegion;
        this.whitelist = whitelist;

        addWhitelistNodes();
        addNodesWhiteBlack();
        addEdges();

        return subGraph;
    }

    public void addWhitelistNodes() {
        for (final Node node : whitelist) {
            subGraph.addVertex(node);
            isNodeInRegion.set(node, true);
        }
    }

    public void addNodesWhite() {
        for (final Node node : graph.vertexSet()) {
            final Geometry point = toPoint(node);
            if (whiteRegion.contains(point)) {
                addNode(node);
            }
        }
    }

    public void addNodesWhiteBlack() {
        for (final Node node : graph.vertexSet()) {
            final Geometry point = toPoint(node);

            final boolean isWhiteContainingPoint = whiteRegion.contains(point);
            final boolean isBlackContainingPoint = blackRegion.contains(point);
            if (isWhiteContainingPoint && !isBlackContainingPoint) {
                addNode(node);
            }
        }
    }

    public void addNode(final Node node) {
        isNodeInRegion.set(node, true);
        subGraph.addVertex(node);
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