package routing.regionAware.util;

import data.Edge;
import data.Node;
import data.Path;
import data.RoadGraph;
import org.jgrapht.alg.util.Pair;

import java.util.*;

public class LOTNodeExtractor {
    private final RoadGraph graph;
    private final Node startNode;
    private final Node endNode;
    private final Set<Node> entryExitPoints;
    private final Map<Pair<Node, Node>, Path> allPaths;

    private final Map<Node, List<Node>> viaPointToLOTNodes = new HashMap<>();


    public LOTNodeExtractor(final RoadGraph graph, final Node startNode, final Node endNode,
                            final Set<Node> entryExitPoints, final Map<Pair<Node, Node>, Path> allPaths) {
        this.graph = graph;
        this.startNode = startNode;
        this.endNode = endNode;
        this.entryExitPoints = entryExitPoints;
        this.allPaths = allPaths;

        findLotNodes();
    }

    private void findLotNodes() {
        final List<Node> startLotNodes = new ForwardLotNodeFinder().findLotNodes(startNode);
        viaPointToLOTNodes.put(startNode, startLotNodes);

        final List<Node> endLotNodes = new BackwardLotNodeFinder().findLotNodes(endNode);
        viaPointToLOTNodes.put(endNode, endLotNodes);
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

    private abstract class LotNodeFinder {
        List<Node> findLotNodes (final Node viaPoint) {
            final List<Node> lotNodes = new LinkedList<>();

            for (final Node entryExitPoint : entryExitPoints) {
                if (isLotNode(viaPoint, entryExitPoint)) {
                    lotNodes.add(entryExitPoint);
                }
            }

            return lotNodes;
        }

        private boolean isLotNode(final Node viaNode, final Node entryExitNode) {
            boolean longerToSomeNeighbor = false;

            for (final Object oIncidence : graph.incomingEdgesOf(entryExitNode)) {
                final Edge edge = (Edge) oIncidence;
                final Node neighbor = graph.getEdgeSource(edge);

                if (isEntryExitPoint(neighbor)) {
                    final Path pathNeighbor = getNeighborPath(viaNode, neighbor);
                    final double distanceToNeighbor = pathNeighbor.getWeight();
                    final Path pathEntryExitPoint = getEntryExitNodePath(viaNode, entryExitNode);
                    final double distanceToEntryExitPoint = pathEntryExitPoint.getWeight();

                    longerToSomeNeighbor |= distanceToEntryExitPoint > distanceToNeighbor;
                }
            }

            return !longerToSomeNeighbor;
        }

        abstract Path getNeighborPath(final Node viaPoint, final Node neighbor);

        abstract Path getEntryExitNodePath(final Node viaPoint, final Node entryExitPoint);
    }

    private class ForwardLotNodeFinder extends LotNodeFinder {
        @Override
        Path getNeighborPath(final Node viaPoint, final Node neighbor) {
            return  allPaths.get(new Pair<>(startNode, neighbor));
        }

        @Override
        Path getEntryExitNodePath(final Node viaPoint, final Node entryExitPoint) {
            return allPaths.get(new Pair<>(startNode, entryExitPoint));
        }
    }

    private class BackwardLotNodeFinder extends LotNodeFinder {
        @Override
        Path getNeighborPath(final Node viaPoint, final Node neighbor) {
            return allPaths.get(new Pair<>(neighbor, viaPoint));
        }

        @Override
        Path getEntryExitNodePath(final Node viaPoint, final Node entryExitPoint) {
            return allPaths.get(new Pair<>(entryExitPoint, viaPoint));
        }
    }
}
