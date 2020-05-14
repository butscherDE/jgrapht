package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import util.BinaryHashFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class CellRunner {
    final LinkedList<ReflectiveEdge> edgesOnCell = new LinkedList<>();
    private final RoadGraph graph;
    private final BinaryHashFunction<AscendingEdge> localVisitedManager;
    final BinaryHashFunction<AscendingEdge> visitedManager;
    private final VectorAngleCalculator vectorAngleCalculator;
    private final ReflectiveEdge startEdge;
    private final Map<Node, SortedNeighbors> sortedNeighborsMap;

    private ReflectiveEdge lastNonZeroLengthEdge;


    CellRunner(final RoadGraph graph, final BinaryHashFunction<AscendingEdge> visitedManager,
               final VectorAngleCalculator vectorAngleCalculator, final Edge startEdge,
               Map<Node, SortedNeighbors> sortedNeighborsMap) {
        this.graph = graph;
        this.localVisitedManager = new BinaryHashFunction<>();
        this.visitedManager = visitedManager;
        this.vectorAngleCalculator = vectorAngleCalculator;

        this.startEdge = forceEdgeAscendingNodeIDs(new ReflectiveEdge(startEdge, graph));
        this.sortedNeighborsMap = sortedNeighborsMap;
        this.lastNonZeroLengthEdge = this.startEdge;
    }

    public ReflectiveEdge forceEdgeAscendingNodeIDs(final ReflectiveEdge edge) {
        if (edge.source.id < edge.target.id) {
            return edge;
        } else {
            return edge.getReversed();
        }
    }

    public VisibilityCell extractVisibilityCell() {
        runAroundCellAndLogNodes();
        return createVisibilityCell();
    }

    private void runAroundCellAndLogNodes() {
        failOnLengthZeroStartEdge();

        addStartAndEndNodeOfCell();
        run();
    }

    private void failOnLengthZeroStartEdge() {
        if (hasEdgeEndPointsWithEqualCoordinates(startEdge)) {
            throw new IllegalArgumentException("Cannot start run on an edge with equal coordinates on both end nodes");
        }
    }

    private void run() {
        boolean endNotReached;
        do {
            endNotReached = processNextNeighborOnCell();
        }
        while (endNotReached);
    }

    private void addStartAndEndNodeOfCell() {
        edgesOnCell.add(startEdge);
        visitedManager.set(new AscendingEdge(startEdge), true);
    }

    private boolean processNextNeighborOnCell() {
        final ReflectiveEdge leftOrRightMostNeighbor = getMostLeftOrRightOrientedEdge();

        return settleAllFoundEdgesAndSetWhenRunHasStopped(leftOrRightMostNeighbor);
    }

    private boolean settleAllFoundEdgesAndSetWhenRunHasStopped(ReflectiveEdge edge) {
        if (lastEdgeNotReached(edge)) {
            settleEdge(edge);
            return true;
        } else {
            return false;
        }
    }

    private void settleEdge(ReflectiveEdge edge) {
        final AscendingEdge ascendingEdge = new AscendingEdge(edge);
        localVisitedManager.set(ascendingEdge, true);
        visitedManager.set(ascendingEdge, true);
        edgesOnCell.add(edge);
    }

    private boolean lastEdgeNotReached(final ReflectiveEdge lastEdge) {
        final AscendingEdge lastEdgeAscending = new AscendingEdge(lastEdge);
        return !lastEdgeAscending.equals(startEdge);

//        final Node lastEdgeSourceNode = graph.getEdgeSource(lastEdge);
//        final Node lastEdgeTargetNode = graph.getEdgeTarget(lastEdge);
//        final boolean baseNodeEqual = lastEdgeSourceNode.equals(startEdge.sourceNode);
//        final boolean adjNodeEqual = lastEdgeTargetNode.equals(startEdge.targetNode);
//        final boolean sameDirection = baseNodeEqual && adjNodeEqual;
//        return !sameDirection;
    }

    private ReflectiveEdge getMostLeftOrRightOrientedEdge() {
        final ReflectiveEdge lastEdge = edgesOnCell.getLast();
        final Node ignoreBackwardsEdge = getIgnoreBackwardsEdge(lastEdge);
        final ReflectiveEdge mostOrientedEdge = getMostOrientedEdgeFromSortedNeighbors(lastEdge, ignoreBackwardsEdge);

        updateLastNonZeroLengthEdge(mostOrientedEdge);

        return mostOrientedEdge;
    }

    private Node getIgnoreBackwardsEdge(ReflectiveEdge lastEdge) {
        final Node lastEdgeBaseNode = lastEdge.source;
        return hasEdgeEndPointsWithEqualCoordinates(lastEdge) ? lastEdgeBaseNode : SortedNeighbors.DO_NOT_IGNORE_NODE;
    }

    private ReflectiveEdge getMostOrientedEdgeFromSortedNeighbors(ReflectiveEdge lastEdge, Node ignoreBackwardsEdge) {
        final Node lastEdgeAdjNode = lastEdge.target;
        final SortedNeighbors sortedNeighbors = sortedNeighborsMap.get(lastEdgeAdjNode); //new SortedNeighbors(graph, lastEdgeAdjNode, ignoreBackwardsEdge, vectorAngleCalculator);
        final ReflectiveEdge reversed = lastNonZeroLengthEdge.getReversed();
        return sortedNeighbors.getMostOrientedEdge(reversed);
    }

    private void updateLastNonZeroLengthEdge(ReflectiveEdge mostOrientedEdge) {
        if (!hasEdgeEndPointsWithEqualCoordinates(mostOrientedEdge)) {
            this.lastNonZeroLengthEdge = mostOrientedEdge;
        }
    }

    private boolean hasEdgeEndPointsWithEqualCoordinates(ReflectiveEdge edge) {
        final Node sourceNode = edge.source;
        final Node targetNode = edge.target;

        final double distance = sourceNode.getPoint().distance(targetNode.getPoint());
        return distance < 0.0000000001;
    }

    List<Node> extractNodesFromVisitedEdges() {
        final List<Node> nodesOnCell = new LinkedList<>();

        for (ReflectiveEdge edge : edgesOnCell) {
            nodesOnCell.add(edge.source);
        }

        return nodesOnCell;
    }

    abstract VisibilityCell createVisibilityCell();

}
