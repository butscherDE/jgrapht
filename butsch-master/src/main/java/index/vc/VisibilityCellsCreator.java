package index.vc;

import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.StopWatch;
import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import evalutation.StopWatchVerbose;
import org.jgrapht.alg.util.Pair;
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
    private final RoadGraph graph;
    private final BinaryHashFunction<Pair<Node, Node>> visitedManagerLeft = new BinaryHashFunction<>();;
    private final BinaryHashFunction<Pair<Node, Node>> visitedManagerRight = new BinaryHashFunction<>();;
    private final Map<Integer, SortedNeighbors> sortedNeighborListLeft;
    private final Map<Integer, SortedNeighbors> sortedNeighborListRight;

    private final List<VisibilityCell> allFoundCells = new LinkedList<>();;
    private final Set<Edge> allEdges;

    public VisibilityCellsCreator(final RoadGraph graph) {
        this.graph = graph;
        this.allEdges = graph.edgeSet();

        final NeighborPreSorter neighborPreSorter = new NeighborPreSorter(graph);
        this.sortedNeighborListLeft = neighborPreSorter.getAllSortedNeighborsLeft();
        this.sortedNeighborListRight = neighborPreSorter.getAllSortedNeighborsRight();
    }

    public List<VisibilityCell> create() {
        startRunsOnEachEdgeInTheGraph();

        return allFoundCells;
    }

    private void startRunsOnEachEdgeInTheGraph() {
        StopWatchVerbose swAll = new StopWatchVerbose("VisibilityCells created").;
        for (final Edge currentEdge : this.allEdges) {
            if (continueOnLengthZeroEdge(currentEdge)) {
                continue;
            }

            if (!visibilityCellOnTheLeftFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerLeft(graph, visitedManagerLeft, currentEdge, sortedNeighborListLeft).extractVisibilityCell());
            }

            if (!visibilityCellOnTheRightFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerRight(graph, visitedManagerRight, currentEdge, sortedNeighborListRight).extractVisibilityCell());
            }
        }
        swAll.printTimingIfVerbose();
    }

    private boolean continueOnLengthZeroEdge(final Edge currentEdge) {
        if (isCurrentEdgeLengthZero(currentEdge)) {
            final Pair<Node, Node> nodePair = getAscendingNodePair(currentEdge);
            visitedManagerLeft.set(nodePair, true);
            visitedManagerRight.set(nodePair, true);
            return true;
        }
        return false;
    }

    private boolean isCurrentEdgeLengthZero(final Edge currentEdge) {
        final Node sourceNode = graph.getEdgeSource(currentEdge);
        final Node targetNode = graph.getEdgeTarget(currentEdge);

        return sourceNode.latitude == targetNode.latitude && sourceNode.longitude == targetNode.longitude;
    }

    private void addVisibilityCellToResults(VisibilityCell visibilityCell) {
        allFoundCells.add(visibilityCell);
    }

    private Boolean visibilityCellOnTheLeftFound(final Edge currentEdge) {
        return visitedManagerLeft.get(getAscendingNodePair(currentEdge));
    }

    private Boolean visibilityCellOnTheRightFound(final Edge currentEdge) {
        return visitedManagerRight.get(getAscendingNodePair(currentEdge));
    }

    public Pair<Node, Node> getAscendingNodePair(final Edge edge) {
        final Node edgeSource = graph.getEdgeSource(edge);
        final Node edgeTarget = graph.getEdgeTarget(edge);

        final boolean isEdgesVertexIdAscending = edgeSource.id < edgeTarget.id;
        return isEdgesVertexIdAscending ? new Pair<>(edgeSource, edgeTarget) : new Pair<>(edgeTarget, edgeSource);
    }
}
