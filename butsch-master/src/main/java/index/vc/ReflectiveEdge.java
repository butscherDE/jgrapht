package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;

public class ReflectiveEdge {
    public final long id;
    public final Node source;
    public final Node target;

    public ReflectiveEdge(final Edge edge, final RoadGraph graph) {
        this(edge.id, graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
    }

    public ReflectiveEdge(final long id, final Node source, final Node target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public ReflectiveEdge getReversed() {
        return new ReflectiveEdge(id, target, source);
    }
}
