package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import org.locationtech.jts.planargraph.Subgraph;
import visualizations.SubGraphVisualizer;

import java.util.*;

abstract class CellRunner {
    final LinkedList<ReflectiveEdge> edgesOnCell = new LinkedList<>();
    final VisitedEdgesHashFunction visitedManager;
    private final ReflectiveEdge startEdge;
    private final Map<Node, SortedNeighbors> sortedNeighborsMap;

    private ReflectiveEdge lastNonZeroLengthEdge;

private RoadGraph graph;
    CellRunner(final RoadGraph graph, final VisitedEdgesHashFunction visitedManager, final Edge startEdge,
               Map<Node, SortedNeighbors> sortedNeighborsMap) {
        this.graph = graph;



        this.visitedManager = visitedManager;

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
            System.out.println(edgesOnCell.size());

            if (edgesOnCell.size() > 3) {
                final List<ReflectiveEdge> last3 = edgesOnCell.subList(edgesOnCell.size() - 4, edgesOnCell.size() - 1);
                LinkedList<ReflectiveEdge> window = new LinkedList<>();
                final Iterator<ReflectiveEdge> iterator = edgesOnCell.iterator();
                window.add(iterator.next());
                window.add(iterator.next());
                window.add(iterator.next());
                int equalCount = 0;
                while(iterator.hasNext() && equalCount < 3) {
                    if (last3.equals(window)) {
                        equalCount++;
                    }
                    window.removeFirst();
                    window.addLast(iterator.next());
                }

                if (equalCount >= 3) {
                    edgesOnCell.stream().forEach(a -> System.out.println(a));
                    final SubGraphVisualizer subGraphVisualizer = new SubGraphVisualizer(graph, Collections.emptyList());
                    subGraphVisualizer.add(graph.getVertex(5044301947L));
                    subGraphVisualizer.add(graph.getVertex(5044301948L));
                    subGraphVisualizer.add(graph.getVertex(5044301948L));
                    subGraphVisualizer.add(graph.getVertex(5044301947L));
                    subGraphVisualizer.add(graph.getVertex(5044301947L));
                    subGraphVisualizer.add(graph.getVertex(51384488L));
                    subGraphVisualizer.add(graph.getVertex(51384488L));
                    subGraphVisualizer.add(graph.getVertex(1870077939L));
                    subGraphVisualizer.add(graph.getVertex(1870077939L));
                    subGraphVisualizer.add(graph.getVertex(51384487L));
                    subGraphVisualizer.add(graph.getVertex(51384487L));
                    subGraphVisualizer.add(graph.getVertex(51384485L));
                    subGraphVisualizer.add(graph.getVertex(51384485L));
                    subGraphVisualizer.add(graph.getVertex(51442352L));
                    subGraphVisualizer.add(graph.getVertex(51442352L));
                    subGraphVisualizer.add(graph.getVertex(51385304L));
                    subGraphVisualizer.add(graph.getVertex(51385304L));
                    subGraphVisualizer.add(graph.getVertex(51385302L));
                    subGraphVisualizer.add(graph.getVertex(51385302L));
                    subGraphVisualizer.add(graph.getVertex(51442273L));
                    subGraphVisualizer.add(graph.getVertex(51442273L));
                    subGraphVisualizer.add(graph.getVertex(51385300L));
                    subGraphVisualizer.add(graph.getVertex(51385300L));
                    subGraphVisualizer.add(graph.getVertex(51385299L));
                    subGraphVisualizer.add(graph.getVertex(51385299L));
                    subGraphVisualizer.add(graph.getVertex(1870075853L));
                    subGraphVisualizer.add(graph.getVertex(1870075853L));
                    subGraphVisualizer.add(graph.getVertex(51385298L));
                    subGraphVisualizer.add(graph.getVertex(51385298L));
                    subGraphVisualizer.add(graph.getVertex(2188646083L));
                    subGraphVisualizer.add(graph.getVertex(2188646083L));
                    subGraphVisualizer.add(graph.getVertex(51385297L));
                    subGraphVisualizer.add(graph.getVertex(51385297L));
                    subGraphVisualizer.add(graph.getVertex(51385296L));
                    subGraphVisualizer.add(graph.getVertex(51385296L));
                    subGraphVisualizer.add(graph.getVertex(51385295L));
                    subGraphVisualizer.add(graph.getVertex(51385295L));
                    subGraphVisualizer.add(graph.getVertex(51385292L));
                    subGraphVisualizer.add(graph.getVertex(51385292L));
                    subGraphVisualizer.add(graph.getVertex(51385291L));
                    subGraphVisualizer.add(graph.getVertex(51385291L));
                    subGraphVisualizer.add(graph.getVertex(51385290L));
                    subGraphVisualizer.add(graph.getVertex(51385290L));
                    subGraphVisualizer.add(graph.getVertex(51442041L));
                    subGraphVisualizer.add(graph.getVertex(51442041L));
                    subGraphVisualizer.add(graph.getVertex(51442282L));
                    subGraphVisualizer.add(graph.getVertex(51442282L));
                    subGraphVisualizer.add(graph.getVertex(51442280L));
                    subGraphVisualizer.add(graph.getVertex(51442280L));
                    subGraphVisualizer.add(graph.getVertex(1870074789L));
                    subGraphVisualizer.add(graph.getVertex(1870074789L));
                    subGraphVisualizer.add(graph.getVertex(51442278L));
                    subGraphVisualizer.add(graph.getVertex(51442278L));
                    subGraphVisualizer.add(graph.getVertex(51442349L));
                    subGraphVisualizer.add(graph.getVertex(51442349L));
                    subGraphVisualizer.add(graph.getVertex(51442271L));
                    subGraphVisualizer.add(graph.getVertex(51442271L));
                    subGraphVisualizer.add(graph.getVertex(2188646080L));
                    subGraphVisualizer.add(graph.getVertex(2188646080L));
                    subGraphVisualizer.add(graph.getVertex(51442273L));
                    subGraphVisualizer.add(graph.getVertex(51442273L));
                    subGraphVisualizer.add(graph.getVertex(51385300L));
                    subGraphVisualizer.add(graph.getVertex(51385300L));
                    subGraphVisualizer.add(graph.getVertex(51385299L));
                    subGraphVisualizer.add(graph.getVertex(51385299L));
                    subGraphVisualizer.add(graph.getVertex(1870075853L));
                    subGraphVisualizer.add(graph.getVertex(1870075853L));
                    subGraphVisualizer.add(graph.getVertex(51385298L));
                    subGraphVisualizer.add(graph.getVertex(51385298L));
                    subGraphVisualizer.add(graph.getVertex(2188646083L));
                    subGraphVisualizer.add(graph.getVertex(2188646083L));
                    subGraphVisualizer.add(graph.getVertex(51385297L));
                    subGraphVisualizer.add(graph.getVertex(51385297L));
                    subGraphVisualizer.add(graph.getVertex(51385296L));
                    subGraphVisualizer.add(graph.getVertex(51385296L));
                    subGraphVisualizer.add(graph.getVertex(51385295L));
                    subGraphVisualizer.add(graph.getVertex(51385295L));
                    subGraphVisualizer.add(graph.getVertex(51385292L));
                    subGraphVisualizer.add(graph.getVertex(51385292L));
                    subGraphVisualizer.add(graph.getVertex(51385291L));
                    subGraphVisualizer.add(graph.getVertex(51385291L));
                    subGraphVisualizer.add(graph.getVertex(51385290L));
                    subGraphVisualizer.add(graph.getVertex(51385290L));
                    subGraphVisualizer.add(graph.getVertex(51442041L));
                    subGraphVisualizer.add(graph.getVertex(51442041L));
                    subGraphVisualizer.add(graph.getVertex(51442282L));
                    subGraphVisualizer.add(graph.getVertex(51442282L));
                    subGraphVisualizer.add(graph.getVertex(51442280L));
                    subGraphVisualizer.add(graph.getVertex(51442280L));
                    subGraphVisualizer.add(graph.getVertex(1870074789L));
                    subGraphVisualizer.add(graph.getVertex(1870074789L));
                    subGraphVisualizer.add(graph.getVertex(51442278L));
                    subGraphVisualizer.add(graph.getVertex(51442278L));
                    subGraphVisualizer.add(graph.getVertex(51442349L));
                    subGraphVisualizer.add(graph.getVertex(51442349L));
                    subGraphVisualizer.add(graph.getVertex(51442271L));
                    subGraphVisualizer.add(graph.getVertex(51442271L));
                    subGraphVisualizer.add(graph.getVertex(2188646080L));
                    subGraphVisualizer.add(graph.getVertex(2188646080L));
                    subGraphVisualizer.add(graph.getVertex(51442273L));
                    subGraphVisualizer.add(graph.getVertex(51442273L));
                    subGraphVisualizer.add(graph.getVertex(51385300L));
                    subGraphVisualizer.add(graph.getVertex(51385300L));
                    subGraphVisualizer.add(graph.getVertex(51385299L));
                    subGraphVisualizer.add(graph.getVertex(51385299L));
                    subGraphVisualizer.add(graph.getVertex(1870075853L));
                    subGraphVisualizer.add(graph.getVertex(1870075853L));
                    subGraphVisualizer.add(graph.getVertex(51385298L));
                    subGraphVisualizer.visualize(100_000_000);
                    System.exit(-1);
                }
            }
        }
        while (endNotReached);
    }

    private void addStartAndEndNodeOfCell() {
        edgesOnCell.add(startEdge);
        visitedManager.visited(startEdge);
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
