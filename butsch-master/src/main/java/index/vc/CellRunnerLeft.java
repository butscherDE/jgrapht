package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import org.jgrapht.Graph;

import java.util.Collections;
import java.util.Map;

class CellRunnerLeft extends CellRunner {

    public CellRunnerLeft(final RoadGraph graph, final Graph<Node, Edge> cellGraph,
                          final VisitedEdgesHashFunction visitedManager,
                          final Edge startEdge, final Map<Node, SortedNeighbors> sortedNeighborsMap) {
        super(graph, cellGraph, visitedManager, startEdge, sortedNeighborsMap);
    }

    @Override
    VisibilityCell createVisibilityCell() {
        Collections.reverse(edgesOnCell);
        return VisibilityCell.create(extractNodesFromVisitedEdges(), getActualGraphEdgeList());
    }
}
