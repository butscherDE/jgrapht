package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;

import java.util.Objects;

public class ReflectiveEdge {
    public final long id;
    public final Node source;
    public final Node target;
    private final ReflectiveEdge reverse;

    public ReflectiveEdge(final Edge edge, final RoadGraph graph) {
        this(edge.id, graph.getEdgeSource(edge), graph.getEdgeTarget(edge), graph);
    }

    public ReflectiveEdge(final long id, final Node source, final Node target, final RoadGraph graph) {
        this.id = id;
        this.source = source;
        this.target = target;

        Edge reverse = graph.getEdge(target, source);
        if (reverse != null) {
            this.reverse = new ReflectiveEdge(reverse.id, target, source, this);
        } else {
            this.reverse = new ReflectiveEdge(id, target, source, this);
        }
    }

    private ReflectiveEdge(final long id, final Node source, final Node target, final ReflectiveEdge reverse) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.reverse = reverse;
    }

    public ReflectiveEdge getReversed() {
        return reverse;
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
