package index.vc;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.jgrapht.Graph;

import java.util.*;
import java.util.stream.Collectors;

public class SortedNeighbors {
    public final static Node DO_NOT_IGNORE_NODE = RoadGraph.INVALID_NODE;
    private final Graph<Node, Edge> graph;
    private final VectorAngleCalculator vectorAngleCalculator;

    private final List<ComparableEdge> sortedEdges;

    public SortedNeighbors(final Graph<Node, Edge> graph, final Node baseNode, final Node ignore, final VectorAngleCalculator vectorAngleCalculator) {
        this.graph = graph;
        this.vectorAngleCalculator = vectorAngleCalculator;

        this.sortedEdges = sort(baseNode, ignore);

        if (sortedEdges.size() == 0 && baseNode.id >= 0) {
            System.out.println(baseNode);
            System.out.println(ignore);
            System.out.println(graph.outgoingEdgesOf(baseNode).size());
            throw new IllegalStateException("Never create Sorted Neighbors on node with no outgoing neighbors.");
        }
    }

    private List<ComparableEdge> sort(final Node baseNode, final Node ignore) {
        final List<ComparableEdge> comparableEdges = getAllNeighbors(baseNode, ignore);

        Collections.sort(comparableEdges);

        return comparableEdges;
    }

    private List<ComparableEdge> getAllNeighbors(Node baseNode, final Node ignore) {
        final Set<ReflectiveEdge> incidentEdgesRightOrientation = getNeighborsRightOriented(baseNode);
        final List<ComparableEdge> comparableEdges = new ArrayList<>();

        addAllNeighborsMaybeIncludingCompareEdge(ignore, incidentEdgesRightOrientation, comparableEdges);

        return comparableEdges;
    }

    private Set<ReflectiveEdge> getNeighborsRightOriented(final Node baseNode) {
        final Set<ReflectiveEdge> incidentEdges = getNeighbors(baseNode);
        return incidentEdges
                .stream()
                .map(edge -> {
                    if (edge.source.id != baseNode.id) {
                        return edge.getReversed();
                    } else {
                        return edge;
                    }
                })
                .collect(Collectors.toSet());
    }

    private void addAllNeighborsMaybeIncludingCompareEdge(final Node ignore, final Set<ReflectiveEdge> incidentEdges,
                                                          final List<ComparableEdge> comparableEdges) {
        for (final ReflectiveEdge incidentEdge : incidentEdges) {
            if (isNodeToIgnore(ignore, incidentEdge) && isNotImpasseSubNode(incidentEdge)) {
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

    private boolean isNotImpasseSubNode(final ReflectiveEdge edge) {
        boolean isImpasse;

        if (hasEdgeEqualCoordinates(edge)) {
            isImpasse = !hasANeighborNonZeroLengthEdge(edge);
        } else {
            isImpasse = false;
        }

        return !isImpasse;
    }

    private boolean hasANeighborNonZeroLengthEdge(ReflectiveEdge edge) {
        final Set<ReflectiveEdge> neighbors = getNeighbors(edge.target);

        boolean hasAnyNeighborNonZeroEdge = false;
        for (ReflectiveEdge neighbor : neighbors) {
            hasAnyNeighborNonZeroEdge |= hasNeighborNonZeroLengthEdge(edge, neighbor);
        }
        return hasAnyNeighborNonZeroEdge;
    }

    private Set<ReflectiveEdge> getNeighbors(final Node node) {
        return graph
                .outgoingEdgesOf(node)
                .stream()
                .map(a -> new ReflectiveEdge(a, graph))
                .collect(Collectors.toSet());
    }

    private boolean hasNeighborNonZeroLengthEdge(final ReflectiveEdge neighborPredecessor, final ReflectiveEdge neighbor) {
        final boolean hasEqualCoordinates = hasEdgeEqualCoordinates(neighbor);

        if (hasEqualCoordinates && !areEdgesEqual(neighborPredecessor, neighbor)) {
            return isNotImpasseSubNode(neighbor);
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

    private int getMostOrientedIndex(final ReflectiveEdge lastEdge) {
        final ComparableEdge lastEdgeComparable = new ComparableEdge(lastEdge);

        final int index = Collections.binarySearch(sortedEdges, lastEdgeComparable);

        return calcMostOrientedIndex(index);
    }

    private int calcMostOrientedIndex(int index) {
        if (index > 0) {
            return index - 1;
        } else if (index == 0) {
            return sortedEdges.size() - 1;
        } else {
            final int insertionPoint = (index + 1) * (-1);
            if (insertionPoint > 0) {
                return insertionPoint - 1;
            } else {
                return sortedEdges.size() - 1;
            }
        }
    }

    public ReflectiveEdge getMostOrientedEdge(final ReflectiveEdge lastEdge) {
        final int addIndex = getMostOrientedIndex(lastEdge);

        try {
            if (sortedEdges.size() > 1) {
                return get(addIndex);
            } else {
                return lastEdge;
            }
        } catch (Exception e) {

            e.printStackTrace();
            System.out.println(lastEdge);
            throw new IllegalArgumentException();
        }
    }

    public ReflectiveEdge get(final int index) {
        final ComparableEdge comparableEdge = sortedEdges.get(index);

        return new ReflectiveEdge(comparableEdge.id, comparableEdge.source, comparableEdge.target, graph);
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
        private final Node source;
        private final Node target;

        ComparableEdge(ReflectiveEdge edge) {
            this.id = edge.id;
            this.source = edge.source;
            this.target = edge.target;
        }

        @Override
        public int compareTo(ComparableEdge o) {
            final double angleThis = vectorAngleCalculator.getAngleOfVectorsOriented(source, target);
            final double angleOther = vectorAngleCalculator.getAngleOfVectorsOriented(o.source, o.target);
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
                return source.id == ce.source.id && target.id == ce.target.id;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "ComparableEdge{" + "id=" + id + ", baseNode=" + source + ", adjNode=" + target + '}';
        }
    }
}
