package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import evalutation.StopWatchVerbose;
import util.BinaryHashFunction;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * "Left" and "Right" are always imagined as walking from baseNode to adjacent node and then turn left or right.
 * <p>
 * General schema: For each edge in the allEdgesIterator: Check if it was used in a left run, if not run left. Check if it was used in a right run if not run right
 */
public class VisibilityCellsCreator {
    private final RoadGraph graph;
    private final VisitedEdgesHashFunction visitedManagerLeft = new VisitedEdgesHashFunction();
    private final VisitedEdgesHashFunction visitedManagerRight = new VisitedEdgesHashFunction();
    private final Map<Node, SortedNeighbors> sortedNeighborListLeft;
    private final Map<Node, SortedNeighbors> sortedNeighborListRight;

    private final List<VisibilityCell> allFoundCells = new LinkedList<>();
    private final Set<Edge> allEdges;

    public VisibilityCellsCreator(final RoadGraph graph) {
        this.graph = getPreprocessedGraph(graph);
        this.allEdges = this.graph.edgeSet();

        final NeighborPreSorter neighborPreSorter = new NeighborPreSorter(this.graph);
        this.sortedNeighborListLeft = neighborPreSorter.getAllSortedNeighborsLeft();
        this.sortedNeighborListRight = neighborPreSorter.getAllSortedNeighborsRight();
    }

    public RoadGraph getPreprocessedGraph(final RoadGraph graph) {
        return getGraphWithOutgoingEdgesOnEachVertex(graph);
    }

    private RoadGraph getGraphWithOutgoingEdgesOnEachVertex(final RoadGraph graph) {
        final RoadGraph cleanedGraph = graph.deepCopy();
        cleanedGraph.vertexSet().stream().forEach(v -> remove(v, graph));

        return cleanedGraph;
    }

    private void remove(final Node node, final RoadGraph graph) {
        if (graph.outDegreeOf(node) == 0) {
            final Stream<Edge> stream = graph.incomingEdgesOf(node).stream();
            graph.removeVertex(node);
            stream.forEach(e -> remove(graph.getEdgeSource(e), graph));
        }
    }

    public List<VisibilityCell> create() {
        startRunsOnEachEdgeInTheGraph();

        return allFoundCells;
    }

    private void startRunsOnEachEdgeInTheGraph() {
        StopWatchVerbose swAll = new StopWatchVerbose("VisibilityCells created");
        int c = 0;
        for (final Edge currentEdge : this.allEdges) {
            if (continueOnLengthZeroEdge(currentEdge) || currentEdge.id == RoadGraph.INVALID_EDGE.id) {
                continue;
            }

            if (!visibilityCellOnTheLeftFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerLeft(graph, visitedManagerLeft, currentEdge,
                                                              sortedNeighborListLeft).extractVisibilityCell());
            }

            if (!visibilityCellOnTheRightFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerRight(graph, visitedManagerRight, currentEdge,
                                                               sortedNeighborListRight).extractVisibilityCell());
            }
            System.out.println(++c);
        }
        swAll.printTimingIfVerbose();
    }

    private boolean continueOnLengthZeroEdge(final Edge currentEdge) {
        if (isCurrentEdgeLengthZero(currentEdge)) {
            visitedManagerLeft.visited(new ReflectiveEdge(currentEdge, graph));
            visitedManagerRight.visited(new ReflectiveEdge(currentEdge, graph));
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
        return visitedManagerLeft.isVisited(new ReflectiveEdge(currentEdge, graph));
    }

    private Boolean visibilityCellOnTheRightFound(final Edge currentEdge) {
        return visitedManagerRight.isVisited(new ReflectiveEdge(currentEdge, graph));
    }
}
