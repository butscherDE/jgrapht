package index.vc;

import data.Node;
import data.RoadGraph;

import java.util.*;
import java.util.stream.Collectors;

public class SortedNeighbors {
    public final static Node DO_NOT_IGNORE_NODE = RoadGraph.INVALID_NODE;
    private final RoadGraph graph;
    private final VectorAngleCalculator vectorAngleCalculator;

    private final List<ComparableEdge> sortedEdges;

    public SortedNeighbors(final RoadGraph graph, final Node baseNode, final Node ignore, final VectorAngleCalculator vectorAngleCalculator) {
        this.graph = graph;
        this.vectorAngleCalculator = vectorAngleCalculator;

        this.sortedEdges = sort(baseNode, ignore);
    }

    private List<ComparableEdge> sort(final Node baseNode, final Node ignore) {
        final List<ComparableEdge> comparableEdges = getAllNeighbors(baseNode, ignore);

        Collections.sort(comparableEdges);

        return comparableEdges;
    }

    private List<ComparableEdge> getAllNeighbors(Node baseNode, final Node ignore) {
        final Set<ReflectiveEdge> incidentEdges = getNeighbors(baseNode);
        final List<ComparableEdge> comparableEdges = new ArrayList<>();

        addAllNeighborsMaybeIncludingCompareEdge(ignore, incidentEdges, comparableEdges);

        return comparableEdges;
    }

    private void addAllNeighborsMaybeIncludingCompareEdge(final Node ignore, final Set<ReflectiveEdge> incidentEdges,
                                                          final List<ComparableEdge> comparableEdges) {
        for (final ReflectiveEdge incidentEdge : incidentEdges) {
            if (isNodeToIgnore(ignore, incidentEdge) && !isImpasseSubNode(incidentEdge)) {
                if (!hasEdgeEqualCoordinates(incidentEdge)) {
                    comparableEdges.add(new ComparableEdge(incidentEdge));
                } else {
                    comparableEdges.addAll(getAllNeighbors(incidentEdge.target, incidentEdge.source));
                }
            }
        }
    }

    private boolean isNodeToIgnore(Node ignore, ReflectiveEdge incidentEdge) {
        return !incidentEdge.target.equals(ignore);
    }

    private boolean isImpasseSubNode(final ReflectiveEdge edge) {
        boolean isImpasse;

        if (hasEdgeEqualCoordinates(edge)) {
            isImpasse = !hasANeighborNonZeroLengthEdge(edge);
        } else {
            isImpasse = false;
        }

        return isImpasse;
    }

    private boolean hasANeighborNonZeroLengthEdge(ReflectiveEdge edge) {
        final Set<ReflectiveEdge> neighbors = getNeighbors(edge.target);

        boolean hasAnyNeighborNonZeroEdge = false;
        for (ReflectiveEdge neighbor : neighbors) {
            hasAnyNeighborNonZeroEdge |= hasNeighborNonZeroLengthEdge(edge, neighbor);
        }
        return hasAnyNeighborNonZeroEdge;
    }

    private boolean hasNeighborNonZeroLengthEdge(final ReflectiveEdge neighborPredecessor, final ReflectiveEdge neighbor) {
        final boolean hasEqualCoordinates = hasEdgeEqualCoordinates(neighbor);

        if (hasEqualCoordinates && !areEdgesEqual(neighborPredecessor, neighbor)) {
            return !isImpasseSubNode(neighbor);
        } else {
            return !hasEqualCoordinates;
        }
    }

    private boolean areEdgesEqual(final ReflectiveEdge edge1, final ReflectiveEdge edge2) {
        final boolean areSourcesEqual = edge1.source.equals(edge2.source);
        final boolean areTargetsEqual = edge1.target.equals(edge2.target);
        final boolean areSourceAndTargetEqual = edge1.source.equals(edge2.target);
        final boolean areTargetAndSourceEqual = edge1.target.equals(edge2.source);

        return (areSourcesEqual && areTargetsEqual) || (areSourceAndTargetEqual && areTargetAndSourceEqual);
    }

    private boolean hasEdgeEqualCoordinates(final ReflectiveEdge edge) {
        final Node baseNode = edge.source;
        final Node adjNode = edge.target;

        return baseNode.getPoint().distance(adjNode.getPoint()) < 0.0000000001;
    }

    private Set<ReflectiveEdge> getNeighbors(final Node node) {
        final Set<ReflectiveEdge> outgoingEdgesAsReflective = graph
                .outgoingEdgesOf(node)
                .stream()
                .map(a -> new ReflectiveEdge(a, graph))
                .collect(Collectors.toSet());
        return outgoingEdgesAsReflective;
    }

    private int indexIfEdgeWasAdded(final ReflectiveEdge lastEdge) {
        final ComparableEdge lastEdgeComparable = new ComparableEdge(lastEdge);
        final Iterator<ComparableEdge> allNeighborEdges = sortedEdges.iterator();

        int i = findIndex(lastEdgeComparable, allNeighborEdges);

        return i;
    }

    private int findIndex(ComparableEdge lastEdgeComparable, Iterator<ComparableEdge> allNeighborEdges) {
        boolean lastEdgeIsGreater = true;
        int i = 0;
        while (allNeighborEdges.hasNext() && lastEdgeIsGreater) {
            final ComparableEdge currentEdge = allNeighborEdges.next();
            lastEdgeIsGreater = lastEdgeComparable.compareTo(currentEdge) > 0;

            i = lastEdgeIsGreater ? i + 1 : i;
        }
        return i;
    }

    public ReflectiveEdge getMostOrientedEdge(final ReflectiveEdge lastEdge) {
        final int addIndex = indexIfEdgeWasAdded(lastEdge);
        int addIndexPredecessor = addIndex - 1;
        int indexOfEndOfList = sortedEdges.size() - 1;
        int indexOfPredecessorOfLastEdge = addIndexPredecessor < 0 ? indexOfEndOfList : addIndexPredecessor;

        return get(indexOfPredecessorOfLastEdge);
    }

    public ReflectiveEdge get(final int index) {
        final ComparableEdge comparableEdge = sortedEdges.get(index);
        final ReflectiveEdge edge = new ReflectiveEdge(graph.getEdge(comparableEdge.baseNode, comparableEdge.adjNode), graph);

        return edge;
    }

    public int size() {
        return sortedEdges.size();
    }

    @Override
    public String toString() {
        return sortedEdges.toString();
    }

    private class ComparableEdge implements Comparable<ComparableEdge> {
        private final long id;
        private final Node baseNode;
        private final Node adjNode;

        ComparableEdge(ReflectiveEdge edge) {
            this.id = edge.id;
            this.baseNode = edge.source;
            this.adjNode = edge.target;
        }

        @Override
        public int compareTo(ComparableEdge o) {
            final double angleThis = vectorAngleCalculator.getAngleOfVectorsOriented(baseNode, adjNode);
            final double angleOther = vectorAngleCalculator.getAngleOfVectorsOriented(o.baseNode, o.adjNode);
            final double angleDifference = angleThis - angleOther;
            final int angleResult = angleDifference > 0 ? 1 : angleDifference == 0 ? 0 : -1;
            final long actualIdDif = id - o.id;
            final long idDifference = vectorAngleCalculator instanceof VectorAngleCalculatorRight ? actualIdDif : actualIdDif * (-1);
            final int idComparision = idDifference < 0 ? 1 : idDifference == 0 ? 0 : -1;

            return angleResult != 0 ? angleResult : idComparision;
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof ComparableEdge) {
                final ComparableEdge ce = (ComparableEdge) o;
                return id == ce.id && adjNode == ce.adjNode;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "ComparableEdge{" + "id=" + id + ", baseNode=" + baseNode + ", adjNode=" + adjNode + '}';
        }
    }
}
