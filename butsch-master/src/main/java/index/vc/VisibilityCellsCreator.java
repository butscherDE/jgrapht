package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import evalutation.StopWatchVerbose;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import util.BinaryHashFunction;

import java.util.*;

/**
 * "Left" and "Right" are always imagined as walking from baseNode to adjacent node and then turn left or right.
 * <p>
 * General schema: For each edge in the allEdgesIterator: Check if it was used in a left run, if not run left. Check if it was used in a right run if not run right
 */
class VisibilityCellsCreator {
    private final RoadGraph graph;
    private final EdgeReversedGraph<Node, Edge> reversedGraph;
    private final BinaryHashFunction<AscendingEdge> visitedManagerLeft = new BinaryHashFunction<>();;
    private final BinaryHashFunction<AscendingEdge> visitedManagerRight = new BinaryHashFunction<>();;
    private final Map<Node, SortedNeighbors> sortedNeighborListLeft;
    private final Map<Node, SortedNeighbors> sortedNeighborListRight;

    private final List<VisibilityCell> allFoundCells = new LinkedList<>();;
    private final Set<Edge> allEdges;

    public VisibilityCellsCreator(final RoadGraph graph) {
        this.graph = graph;
        this.reversedGraph = new EdgeReversedGraph<>(graph);
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
        StopWatchVerbose swAll = new StopWatchVerbose("VisibilityCells created");
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
            final AscendingEdge currentEdgeAscending = new AscendingEdge(currentEdge, graph);
            visitedManagerLeft.set(currentEdgeAscending, true);
            visitedManagerRight.set(currentEdgeAscending, true);
            return true;
        }
        return false;
    }

    private boolean isCurrentEdgeLengthZero(final Edge currentEdge) {
        final Node sourceNode = graph.getEdgeSource(currentEdge);
        final Node targetNode = graph.getEdgeTarget(currentEdge);

        return sourceNode.getPoint().distance(targetNode.getPoint()) < 0.00000000001;
    }

    private void addVisibilityCellToResults(VisibilityCell visibilityCell) {
        allFoundCells.add(visibilityCell);
    }

    private Boolean visibilityCellOnTheLeftFound(final Edge currentEdge) {
        final AscendingEdge currentEdgeAscending = new AscendingEdge(currentEdge, graph);
        return visitedManagerLeft.get(currentEdgeAscending);
    }

    private Boolean visibilityCellOnTheRightFound(final Edge currentEdge) {
        final AscendingEdge currentEdgeAscending = new AscendingEdge(currentEdge, graph);
        return visitedManagerRight.get(currentEdgeAscending);
    }
}
