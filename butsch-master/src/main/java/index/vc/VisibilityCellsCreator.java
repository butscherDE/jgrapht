package index.vc;

import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.StopWatch;
import data.Edge;
import data.RoadGraph;
import data.VisibilityCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.BinaryHashFunction;

import java.util.*;

/**
 * "Left" and "Right" are always imagined as walking from baseNode to adjacent node and then turn left or right.
 * <p>
 * General schema: For each edge in the allEdgesIterator: Check if it was used in a left run, if not run left. Check if it was used in a right run if not run right
 */
class VisibilityCellsCreator {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RoadGraph graph;
    private final BinaryHashFunction<Edge> globalVisitedManager;
    private final Map<Integer, SortedNeighbors> sortedNeighborListLeft;
    private final Map<Integer, SortedNeighbors> sortedNeighborListRight;

    private final List<VisibilityCell> allFoundCells;
    private final Set<Edge> allEdges;

    public VisibilityCellsCreator(final RoadGraph graph) {
        this.graph = graph;
        this.allEdges = graph.edgeSet();
        this.allFoundCells = new LinkedList<>();
        this.globalVisitedManager = new BinaryHashFunction<>();

        final NeighborPreSorter neighborPreSorter = new NeighborPreSorter(graph);
        this.sortedNeighborListLeft = neighborPreSorter.getAllSortedNeighborsLeft();
        this.sortedNeighborListRight = neighborPreSorter.getAllSortedNeighborsRight();
    }

    public List<VisibilityCell> create() {
        startRunsOnEachEdgeInTheGraph();

        return allFoundCells;
    }

    private void startRunsOnEachEdgeInTheGraph() {
        StopWatch swAll = new StopWatch("VisibilityCells created").start();
        while (allEdges.next()) {
            if (continueOnLengthZeroEdge()) {
                continue;
            }


            final EdgeIteratorState currentEdge = allEdges.detach(false);
            if (!visibilityCellOnTheLeftFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerLeft(graph, globalVisitedManager, currentEdge, sortedNeighborListLeft).extractVisibilityCell());
            }

            if (!visibilityCellOnTheRightFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerRight(graph, globalVisitedManager, currentEdge, sortedNeighborListRight).extractVisibilityCell());
            }
        }
        logger.info(swAll.stop().toString());
    }

    private boolean continueOnLengthZeroEdge() {
        if (isCurrentEdgeLengthZero()) {
            globalVisitedManager.settleEdgeLeft(allEdges);
            globalVisitedManager.settleEdgeRight(allEdges);
            return true;
        }
        return false;
    }

    private boolean isCurrentEdgeLengthZero() {
        final int baseNode = allEdges.getBaseNode();
        final int adjNode = allEdges.getAdjNode();

        final double baseNodeLatitude = nodeAccess.getLatitude(baseNode);
        final double baseNodeLongitude = nodeAccess.getLongitude(baseNode);
        final double adjNodeLatitude = nodeAccess.getLatitude(adjNode);
        final double adjNodeLongitude = nodeAccess.getLongitude(adjNode);

        return baseNodeLatitude == adjNodeLatitude && baseNodeLongitude == adjNodeLongitude;
    }

    private void addVisibilityCellToResults(VisibilityCell visibilityCell) {
        allFoundCells.add(visibilityCell);
    }

    private Boolean visibilityCellOnTheLeftFound(final EdgeIteratorState currentEdge) {
        return globalVisitedManager.isEdgeSettledLeft(VisitedManager.forceNodeIdsAscending(currentEdge));
    }

    private Boolean visibilityCellOnTheRightFound(final EdgeIteratorState currentEdge) {
        return globalVisitedManager.isEdgeSettledRight(VisitedManager.forceNodeIdsAscending(currentEdge));
    }
}
