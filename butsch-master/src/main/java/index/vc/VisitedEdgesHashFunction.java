package index.vc;

import util.BinaryHashFunction;

public class VisitedEdgesHashFunction {
    final BinaryHashFunction<ReflectiveEdge> hashFunction = new BinaryHashFunction<>();

    public void visited(final ReflectiveEdge edge) {
        hashFunction.set(edge, true);
    }

    public boolean isVisited(final ReflectiveEdge edge) {
        return hashFunction.get(edge);
    }
}
