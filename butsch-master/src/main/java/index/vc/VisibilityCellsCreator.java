package index.vc;

import data.*;
import evalutation.StopWatchVerbose;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * "Left" and "Right" are always imagined as walking from baseNode to adjacent node and then turn left or right.
 * <p>
 * General schema: For each edge in the allEdgesIterator: Check if it was used in a left run, if not run left. Check if it was used in a right run if not run right
 */
public class VisibilityCellsCreator {
    private final RoadGraph originalGraph;
    private final CellGraph cellGraph;
    private final VisitedEdgesHashFunction visitedManagerLeft = new VisitedEdgesHashFunction();
    private final VisitedEdgesHashFunction visitedManagerRight = new VisitedEdgesHashFunction();
    private final Map<Node, SortedNeighbors> sortedNeighborListLeft;
    private final Map<Node, SortedNeighbors> sortedNeighborListRight;

    private final List<VisibilityCell> allFoundCells = new LinkedList<>();
    private final Set<Edge> allEdges;

    public VisibilityCellsCreator(RoadGraph originalGraph) {
        this.originalGraph = originalGraph;
        this.cellGraph = getPreprocessedGraph(originalGraph);
        this.allEdges = this.cellGraph.edgeSet();

        final NeighborPreSorter neighborPreSorter = new NeighborPreSorter(this.cellGraph);
        this.sortedNeighborListLeft = neighborPreSorter.getAllSortedNeighborsLeft();
        this.sortedNeighborListRight = neighborPreSorter.getAllSortedNeighborsRight();
    }

    public CellGraph getPreprocessedGraph(final RoadGraph graph) {
//        final RoadGraph cleanedGraph = graph.deepCopy();
//        getGraphWithBidirectionalEdges(cleanedGraph);
        final CellGraph cleanedGraph = CellGraph.create(graph);
        getGraphWithOutgoingEdgesOnEachVertex(cleanedGraph);


        return cleanedGraph;
    }

    private void getGraphWithBidirectionalEdges(RoadGraph cleanedGraph) {
        cleanedGraph.edgeSet().stream().filter(e -> {
            final Node edgeSource = cleanedGraph.getEdgeSource(e);
            final Node edgeTarget = cleanedGraph.getEdgeTarget(e);
            return !cleanedGraph.containsEdge(edgeTarget, edgeSource);
        }).forEach(e -> {
            final Node edgeSource = cleanedGraph.getEdgeSource(e);
            final Node edgeTarget = cleanedGraph.getEdgeTarget(e);
            cleanedGraph.addEdge(edgeTarget, edgeSource);
        });
    }


    private CellGraph getGraphWithOutgoingEdgesOnEachVertex(final CellGraph cleanedGraph) {
        List<Node> checkNodes = getAllInitiallyDegreeZeroNodes(cleanedGraph);
        while (checkNodes.size() > 0) {
            final List<Node> neighbors = getAllNeighborsOfAllDegreeZeroNodes(cleanedGraph, checkNodes);
            removeAllCurrentDegreeZeroNodes(cleanedGraph, checkNodes);
            checkNodes = getAllNeighborsThatDegreeIsNowZero(cleanedGraph, neighbors);
        }

        return cleanedGraph;
    }

    private List<Node> getAllInitiallyDegreeZeroNodes(final CellGraph cleanedGraph) {
        return cleanedGraph
                    .vertexSet()
                    .stream()
                    .filter(a -> cleanedGraph.outDegreeOf(a) == 0)
                    .collect(Collectors.toList());
    }

    private List<Node> getAllNeighborsOfAllDegreeZeroNodes(final CellGraph cleanedGraph, final List<Node> checkNodes) {
        return checkNodes
                        .stream()
                        .map(a -> cleanedGraph.incomingEdgesOf(a))
                        .flatMap(a -> a.stream())
                        .map(a -> cleanedGraph.getEdgeSource(a))
                        .collect(Collectors.toList());
    }

    private void removeAllCurrentDegreeZeroNodes(final CellGraph cleanedGraph, final List<Node> checkNodes) {
        checkNodes.forEach(a -> cleanedGraph.removeVertex(a));
    }

    private List<Node> getAllNeighborsThatDegreeIsNowZero(final CellGraph cleanedGraph, final List<Node> neighbors) {
        final List<Node> checkNodes;
        checkNodes = neighbors
                .stream()
                .filter(a -> cleanedGraph.containsVertex(a))
                .filter(a -> cleanedGraph.outDegreeOf(a) == 0)
                .collect(Collectors.toList());
        return checkNodes;
    }

    public List<VisibilityCell> create() {
        startRunsOnEachEdgeInTheGraph();

        return allFoundCells;
    }

    private void startRunsOnEachEdgeInTheGraph() {
        StopWatchVerbose swAll = new StopWatchVerbose("VisibilityCells created");
        for (final Edge currentEdge : this.allEdges) {
            if (continueOnLengthZeroEdge(currentEdge) || currentEdge.id == RoadGraph.INVALID_EDGE.id) {
                continue;
            }

            if (!visibilityCellOnTheLeftFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerLeft(originalGraph, cellGraph, visitedManagerLeft, currentEdge,
                                                              sortedNeighborListLeft).extractVisibilityCell());
            }

            if (!visibilityCellOnTheRightFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerRight(originalGraph, cellGraph, visitedManagerRight, currentEdge,
                                                               sortedNeighborListRight).extractVisibilityCell());
            }
        }
        swAll.printTimingIfVerbose();
    }

    private boolean continueOnLengthZeroEdge(final Edge currentEdge) {
        if (isCurrentEdgeLengthZero(currentEdge)) {
            visitedManagerLeft.visited(new ReflectiveEdge(currentEdge, cellGraph));
            visitedManagerRight.visited(new ReflectiveEdge(currentEdge, cellGraph));
            return true;
        }
        return false;
    }

    private boolean isCurrentEdgeLengthZero(final Edge currentEdge) {
        final Node sourceNode = cellGraph.getEdgeSource(currentEdge);
        final Node targetNode = cellGraph.getEdgeTarget(currentEdge);

        return sourceNode.getPoint().distance(targetNode.getPoint()) < 0.00000000001;
    }

    private void addVisibilityCellToResults(VisibilityCell visibilityCell) {
        allFoundCells.add(visibilityCell);
    }

    private Boolean visibilityCellOnTheLeftFound(final Edge currentEdge) {
        return visitedManagerLeft.isVisited(new ReflectiveEdge(currentEdge, cellGraph));
    }

    private Boolean visibilityCellOnTheRightFound(final Edge currentEdge) {
        return visitedManagerRight.isVisited(new ReflectiveEdge(currentEdge, cellGraph));
    }
}
