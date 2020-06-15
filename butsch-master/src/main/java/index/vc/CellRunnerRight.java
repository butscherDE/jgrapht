package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;

import java.util.Map;

class CellRunnerRight extends CellRunner {

    public CellRunnerRight(final RoadGraph graph, final RoadGraph cellGraph, final VisitedEdgesHashFunction visitedManager,
                           final Edge startEdge, final Map<Node, SortedNeighbors> sortedNeighborsMap) {
        super(graph, cellGraph, visitedManager, startEdge, sortedNeighborsMap);
    }

    @Override
    VisibilityCell createVisibilityCell() {
        return VisibilityCell.create(extractNodesFromVisitedEdges(), getActualGraphEdgeList());
    }
}
