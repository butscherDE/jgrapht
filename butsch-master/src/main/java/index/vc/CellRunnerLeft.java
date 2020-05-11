package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import org.jgrapht.alg.util.Pair;
import util.BinaryHashFunction;

import java.util.Collections;
import java.util.Map;

class CellRunnerLeft extends CellRunner {

    public CellRunnerLeft(final RoadGraph graph, final BinaryHashFunction<Pair<Node, Node>> visitedManager,
                          final Edge startEdge, final Map<Integer, SortedNeighbors> sortedNeighborsMap) {
        super(graph, visitedManager, new VectorAngleCalculatorLeft(), startEdge, sortedNeighborsMap);
    }

    @Override
    VisibilityCell createVisibilityCell() {
        Collections.reverse(edgesOnCell);
        return VisibilityCell.createVisibilityCellFromNodes(extractNodesFromVisitedEdges());
    }
}
