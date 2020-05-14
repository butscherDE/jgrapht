package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReflectiveEdge that = (ReflectiveEdge) o;
        return id == that.id && Objects.equals(source, that.source) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source, target);
    }

    @Override
    public String toString() {
        return "ReflectiveEdge{" + "id=" + id + ", source=" + source + ", target=" + target + '}';
    }
}
