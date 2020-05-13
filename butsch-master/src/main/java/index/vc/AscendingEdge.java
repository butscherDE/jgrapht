package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;

import java.util.Objects;

class AscendingEdge {
    public final long id;
    public final Node source;
    public final Node target;

    public AscendingEdge(final Edge edge, final RoadGraph graph) {
        this.id = edge.id;

        final Node sourceNode = graph.getEdgeSource(edge);
        final Node targetNode = graph.getEdgeTarget(edge);
        if (sourceNode.id < targetNode.id) {
            this.source = sourceNode;
            this.target = targetNode;
        } else {
            this.source = targetNode;
            this.target = sourceNode;
        }
    }

    public AscendingEdge(final ReflectiveEdge edge) {
        id = edge.id;

        if (edge.source.id < edge.target.id) {
            source = edge.source;
            target = edge.target;
        } else {
            source = edge.target;
            target = edge.source;
        }

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AscendingEdge that = (AscendingEdge) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
