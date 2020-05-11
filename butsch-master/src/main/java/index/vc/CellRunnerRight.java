package index.vc;

import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import org.jgrapht.alg.util.Pair;
import util.BinaryHashFunction;

import java.util.Map;

class CellRunnerRight extends CellRunner {

    public CellRunnerRight(final RoadGraph graph, final BinaryHashFunction<Pair<Node, Node>> visitedManager,
                           final Edge startEdge, final Map<Integer, SortedNeighbors> sortedNeighborsMap) {
        super(graph, visitedManagerDual, new VectorAngleCalculatorRight(), startEdge, sortedNeighborsMap);
    }

    @Override
    VisibilityCell createVisibilityCell() {
        return VisibilityCell.createVisibilityCellFromNodeIDs(extractNodesFromVisitedEdges(), nodeAccess);
    }

    void markGloballyVisited(final EdgeIteratorState edge) {
        visitedManager.settleEdgeRight(edge);
    }
}
