package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import util.BinaryHashFunction;

import java.util.Collections;
import java.util.Map;

class CellRunnerLeft extends CellRunner {

    public CellRunnerLeft(final RoadGraph graph,
                          final BinaryHashFunction<AscendingEdge> visitedManager,
                          final Edge startEdge, final Map<Node, SortedNeighbors> sortedNeighborsMap) {
        super(graph, visitedManager, new VectorAngleCalculatorLeft(graph), startEdge, sortedNeighborsMap);
    }

    @Override
    VisibilityCell createVisibilityCell() {
        Collections.reverse(edgesOnCell);
        return VisibilityCell.create(extractNodesFromVisitedEdges(), edgesOnCell);
    }
}
