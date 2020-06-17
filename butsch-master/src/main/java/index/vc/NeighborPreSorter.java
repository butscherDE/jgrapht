package index.vc;

import data.CellGraph;
import data.Edge;
import data.Node;
import data.RoadGraph;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NeighborPreSorter {
    private final Graph<Node, Edge> graph;

    private final Map<Node, SortedNeighbors> allSortedNeighborsLeft;
    private final Map<Node, SortedNeighbors> allSortedNeighborsRight;

    public NeighborPreSorter(Graph<Node, Edge> graph) {
        this.graph = graph;

        allSortedNeighborsLeft = new HashMap<>(graph.vertexSet().size());
        allSortedNeighborsRight = new HashMap<>(graph.vertexSet().size());

        createSortedNeighbors();
    }

    public Map<Node, SortedNeighbors> getAllSortedNeighborsLeft() {
        return allSortedNeighborsLeft;
    }

    public Map<Node, SortedNeighbors> getAllSortedNeighborsRight() {
        return allSortedNeighborsRight;
    }

    private void createSortedNeighbors() {
        final Set<Node> allNodes = getAllNodes();

        addSortedNeighbors(allNodes);
    }

    private Set<Node> getAllNodes() {
        final Set<Node> allNodes = new LinkedHashSet<>();

        final Set<Edge> allEdges = graph.edgeSet();
        for (final Edge edge : allEdges) {
            allNodes.add(graph.getEdgeSource(edge));
            allNodes.add(graph.getEdgeTarget(edge));
        }

        return allNodes;
    }

    private void addSortedNeighbors(final Set<Node> allNodes) {
        final VectorAngleCalculatorLeft vectorAngleCalculatorLeft = new VectorAngleCalculatorLeft(graph);
        final VectorAngleCalculatorRight vectorAngleCalculatorRight = new VectorAngleCalculatorRight(graph);
        final Node doNotIgnoreNode = SortedNeighbors.DO_NOT_IGNORE_NODE;

        for (final Node node : allNodes) {
            final SortedNeighbors sortedNeighborsLeft = new SortedNeighbors(graph, node, doNotIgnoreNode, vectorAngleCalculatorLeft);
            allSortedNeighborsLeft.put(node, sortedNeighborsLeft);
            final SortedNeighbors sortedNeighborsRight = new SortedNeighbors(graph, node, doNotIgnoreNode, vectorAngleCalculatorRight);
            allSortedNeighborsRight.put(node, sortedNeighborsRight);
        }
    }
}
