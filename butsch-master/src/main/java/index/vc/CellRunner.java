package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import evalutation.StopWatchVerbose;
import org.jgrapht.Graph;
import visualizations.SubGraphVisualizer;

import java.util.*;
import java.util.stream.Collectors;

abstract class CellRunner {
    final LinkedList<ReflectiveEdge> edgesOnCell = new LinkedList<>();
    final VisitedEdgesHashFunction visitedManager;
    private final ReflectiveEdge startEdge;
    private final Map<Node, SortedNeighbors> sortedNeighborsMap;
    private RoadGraph originalGraph;
    private ReflectiveEdge lastNonZeroLengthEdge;

    CellRunner(final RoadGraph originalGraph, final Graph<Node, Edge> cellGraph, final VisitedEdgesHashFunction visitedManager, final Edge startEdge,
               Map<Node, SortedNeighbors> sortedNeighborsMap) {
        this.originalGraph = originalGraph;
        this.visitedManager = visitedManager;

        this.startEdge = forceEdgeAscendingNodeIDs(new ReflectiveEdge(startEdge, cellGraph));
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
//            findRepetition();
        }
        while (endNotReached);
    }

    private void findRepetition() {
        if (edgesOnCell.size() > 20) {
            LinkedList<Node> window = new LinkedList<>();
            LinkedList<Node> last5 = new LinkedList<>();

            final Iterator<ReflectiveEdge> iterator = edgesOnCell.iterator();
            window.add(iterator.next().target);
            window.add(iterator.next().target);
            window.add(iterator.next().target);
            window.add(iterator.next().target);
            window.add(iterator.next().target);

            last5.add(edgesOnCell.get(edgesOnCell.size() - 5).target);
            last5.add(edgesOnCell.get(edgesOnCell.size() - 4).target);
            last5.add(edgesOnCell.get(edgesOnCell.size() - 3).target);
            last5.add(edgesOnCell.get(edgesOnCell.size() - 2).target);
            last5.add(edgesOnCell.get(edgesOnCell.size() - 1).target);

            int lastRep = -1;
            int c = 5;
            while (iterator.hasNext()) {
                c++;
                if (window.equals(last5)) {
                    if (lastRep >= 0) {
                        foundRepetition(lastRep);
                    }
                    lastRep = c;
                }

                window.removeFirst();
                window.addLast(iterator.next().target);
            }
        }
    }

    private void foundRepetition(int from) {
        final List<Node> collect = edgesOnCell.stream()
                .map(a -> a.source)
                .collect(Collectors.toList());
        collect.add(edgesOnCell.getLast().target);
        final List<Node> nodes = collect.subList(from - 10, collect.size());
        final Set<Node> nodes1 = new LinkedHashSet<>(nodes);
        final LinkedList<Node> nodes2 = new LinkedList<>(nodes1);

        System.out.println(this.getClass().getSimpleName());
        nodes.forEach(a -> System.out.println(a));

        final SubGraphVisualizer subGraphVisualizer = new SubGraphVisualizer(originalGraph, nodes2);
        subGraphVisualizer.visualize(1_000_000);
    }

    private void addStartAndEndNodeOfCell() {
        edgesOnCell.add(startEdge);
        visitedManager.visited(startEdge);
    }

    private boolean processNextNeighborOnCell() {
        final ReflectiveEdge leftOrRightMostNeighbor = getMostLeftOrRightOrientedEdge();

        return settleFoundEdge(leftOrRightMostNeighbor);
    }

    private boolean settleFoundEdge(ReflectiveEdge edge) {
        if (lastEdgeNotReached(edge)) {
            settleEdge(edge);
            return true;
        } else {
            return false;
        }
    }

    private void settleEdge(ReflectiveEdge edge) {
        visitedManager.visited(edge);
        edgesOnCell.add(edge);
    }

    private boolean lastEdgeNotReached(final ReflectiveEdge lastEdge) {
        return !lastEdge.equals(startEdge);
    }

    private ReflectiveEdge getMostLeftOrRightOrientedEdge() {
        final ReflectiveEdge lastEdge = edgesOnCell.getLast();
        final ReflectiveEdge mostOrientedEdge = getMostOrientedEdgeFromSortedNeighbors(lastEdge);

        updateLastNonZeroLengthEdge(mostOrientedEdge);
        return mostOrientedEdge;
    }

    private ReflectiveEdge getMostOrientedEdgeFromSortedNeighbors(ReflectiveEdge lastEdge) {
        final Node lastEdgeAdjNode = lastEdge.target;
        final SortedNeighbors sortedNeighbors = sortedNeighborsMap.get(lastEdgeAdjNode);
        final ReflectiveEdge reversed = lastNonZeroLengthEdge.getReversed();
        final ReflectiveEdge mostOrientedEdge = sortedNeighbors.getMostOrientedEdge(reversed);
        return mostOrientedEdge;
    }

    private void updateLastNonZeroLengthEdge(ReflectiveEdge mostOrientedEdge) {
        if (!hasEdgeEndPointsWithEqualCoordinates(mostOrientedEdge)) {
            this.lastNonZeroLengthEdge = mostOrientedEdge;
        }
    }

    private boolean hasEdgeEndPointsWithEqualCoordinates(ReflectiveEdge edge) {
        final Node sourceNode = edge.source;
        final Node targetNode = edge.target;

        final double distance = sourceNode.getPoint()
                .distance(targetNode.getPoint());
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

    List<ReflectiveEdge> getActualGraphEdgeList() {
        final LinkedList<ReflectiveEdge> edges = new LinkedList<>();

        edgesOnCell.stream()
                .filter(e -> originalGraph.containsEdge(e.source, e.target))
                .collect(Collectors.toCollection(() -> edges));
        edgesOnCell.stream()
                .filter(e -> originalGraph.containsEdge(e.target, e.source))
                .map(e -> e.getReversed())
                .collect(Collectors.toCollection(() -> edges));

        return edges;
    }

}
