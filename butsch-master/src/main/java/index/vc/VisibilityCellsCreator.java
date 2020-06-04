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
        this.allEdges = graph.edgeSet();

        final NeighborPreSorter neighborPreSorter = new NeighborPreSorter(graph);
        this.sortedNeighborListLeft = neighborPreSorter.getAllSortedNeighborsLeft();
        this.sortedNeighborListRight = neighborPreSorter.getAllSortedNeighborsRight();
    }

    public RoadGraph getPreprocessedGraph(final RoadGraph graph) {
        return getGraphWithOutgoingEdgesOnEachVertex(graph);
    }

    private RoadGraph getGraphWithOutgoingEdgesOnEachVertex(final RoadGraph graph) {
        final BinaryHashFunction<Node> hashFunction = getHashIndicatingOutDegreeGreaterZero(graph);

        final RoadGraph cleanedGraph = new RoadGraph(Edge.class);
        addFilteredVertices(graph, hashFunction, cleanedGraph);
        addFilteredEdges(graph, hashFunction, cleanedGraph);

        return cleanedGraph;
    }

    private BinaryHashFunction<Node> getHashIndicatingOutDegreeGreaterZero(final RoadGraph graph) {
        final BinaryHashFunction<Node> hashFunction = new BinaryHashFunction<>();
        graph.vertexSet().stream().forEach(a -> hashFunction.set(a, graph.outDegreeOf(a) > 0));
        hashFunction.set(graph.getVertex(-1), false);
        return hashFunction;
    }

    private void addFilteredVertices(final RoadGraph graph, final BinaryHashFunction<Node> hashFunction,
                                     final RoadGraph cleanedGraph) {
        graph.vertexSet().stream().filter(a -> hashFunction.get(a)).forEach(a -> cleanedGraph.addVertex(a));
    }

    private void addFilteredEdges(final RoadGraph graph, final BinaryHashFunction<Node> hashFunction,
                                  final RoadGraph cleanedGraph) {
        for (final Edge edge : graph.edgeSet()) {
            final Node edgeSource = graph.getEdgeSource(edge);
            final Node edgeTarget = graph.getEdgeTarget(edge);

            if (hashFunction.get(edgeSource) && hashFunction.get(edgeTarget)) {
                final Edge newEdge = cleanedGraph.addEdge(edgeSource, edgeTarget);
                cleanedGraph.setEdgeWeight(newEdge, graph.getEdgeWeight(edge));
            }
        }
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
                addVisibilityCellToResults(new CellRunnerLeft(graph, visitedManagerLeft, currentEdge,
                                                              sortedNeighborListLeft).extractVisibilityCell());
            }

            if (!visibilityCellOnTheRightFound(currentEdge)) {
                addVisibilityCellToResults(new CellRunnerRight(graph, visitedManagerRight, currentEdge,
                                                               sortedNeighborListRight).extractVisibilityCell());
            }
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
