package data;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
    private static long EDGE_ID_COUNTER;

    public final long id = EDGE_ID_COUNTER++;

    public Edge() {
        super();
    }

    @Override
    public String toString() {
        return "[" + id + "](" + getSource() + " : " + getTarget() + ")";
    }
}
