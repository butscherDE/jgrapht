package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.Path;
import data.RoadGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class LOTNodeExtractor {
    private final RoadGraph graph;
    private final List<Node> viaPoints;
    private final Set<Node> entryExitPoints;
    private final Map<Pair<Node, Node>, Path> allPaths;

    private final Map<Node, List<Node>> viaPointToLOTNodes = new HashMap<>();


    public LOTNodeExtractor(final RoadGraph graph, final List<Node> viaPoints, final Set<Node> entryExitPoints,
                            final Map<Pair<Node, Node>, Path> allPaths) {
        this.graph = graph;
        this.viaPoints = viaPoints;
        this.entryExitPoints = entryExitPoints;
        this.allPaths = allPaths;

        findLotNodes();
    }

    private void findLotNodes() {
        for (final Node viaPoint : viaPoints) {
            final List<Node> lotNodes = findLotNodes(viaPoint);
            viaPointToLOTNodes.put(viaPoint, lotNodes);
        }
    }

    private List<Node> findLotNodes(final Node viaPoint) {
        final List<Node> lotNodes = new LinkedList<>();

        for (final Node entryExitPoint : entryExitPoints) {
            if (isLotNode(viaPoint, entryExitPoint)) {
                lotNodes.add(entryExitPoint);
            }
        }

        return lotNodes;
    }

    private boolean isLotNode(final Node viaPoint, final Node entryExitPoint) {
        boolean longerToSomeNeighbor = false;

        for (final Object oIncidence : graph.incomingEdgesOf(entryExitPoint)) {
            final Edge edge = (Edge) oIncidence;
            final Node neighbor = (Node) graph.getEdgeSource(edge);

            if (isEntryExitPoint(neighbor)) {
                final Path pathToNeighbor = allPaths.get(new Pair<>(viaPoint, neighbor));
                final double distanceToNeighbor = pathToNeighbor.getWeight();
                final Path pathToEntryExitPoint = allPaths.get(new Pair<>(viaPoint, entryExitPoint));
                final double distanceToEntryExitPoint = pathToEntryExitPoint.getWeight();

                longerToSomeNeighbor |= distanceToEntryExitPoint > distanceToNeighbor;
            }
        }

        return !longerToSomeNeighbor;
    }

    private boolean isEntryExitPoint(final Node neighbor) {
        return entryExitPoints.contains(neighbor);
    }

    public List<Node> getLotNodesFor(final Node viaPoint) {
        return this.viaPointToLOTNodes.get(viaPoint);
    }

    public List<Node> getAllLotNodes() {
        final List<Node> lotNodes = new LinkedList<>();

        for (final Map.Entry<Node, List<Node>> nodeListEntry : viaPointToLOTNodes.entrySet()) {
            lotNodes.addAll(nodeListEntry.getValue());
        }

        return lotNodes;
    }

    public Path getLotNodePathFor(final Node viaPoint, final Node lotNode) {
        return this.allPaths.get(new Pair<>(viaPoint, lotNode));
    }
}
