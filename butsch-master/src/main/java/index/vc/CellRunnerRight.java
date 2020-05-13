package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import util.BinaryHashFunction;

import java.util.Map;

class CellRunnerRight extends CellRunner {

    public CellRunnerRight(final RoadGraph graph,final BinaryHashFunction<AscendingEdge> visitedManager,
                           final Edge startEdge, final Map<Node, SortedNeighbors> sortedNeighborsMap) {
        super(graph, visitedManager, new VectorAngleCalculatorRight(graph), startEdge, sortedNeighborsMap);
    }

    @Override
    VisibilityCell createVisibilityCell() {
        return VisibilityCell.create(extractNodesFromVisitedEdges());
    }
}
