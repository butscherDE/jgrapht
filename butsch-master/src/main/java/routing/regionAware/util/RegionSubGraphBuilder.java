package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.locationtech.jts.geom.Polygon;

public class RegionSubGraphBuilder {


    public RoadGraph getSubGraph(final RoadGraph graph, final Polygon region) {
        final RoadGraph subGraph = new RoadGraph(Edge.class);

        for (final Node node : graph.vertexSet()) {

        }

        return null;
    }
}
