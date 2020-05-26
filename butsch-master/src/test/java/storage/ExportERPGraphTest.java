package storage;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.junit.jupiter.api.Test;
import util.GeneralTestGraph;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExportERPGraphTest {
    @Test
    void exAndImport() {
        try {
            final RoadGraph graph = GeneralTestGraph.createTestGraph();
            final GraphExporter graphExporter = new ExportERPGraph(graph, "exp.txt");
            graphExporter.export();
            final GraphImporter graphImporter = new ImportERPGraph("exp.txt");
            final RoadGraph graphReImp = graphImporter.createGraph();

            assertNumNudes(graph, graphReImp);

            assertNodeAssociatedData(graph, graphReImp);

            cleanUp();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private void assertNumNudes(RoadGraph graph, RoadGraph graphReImp) {
        assertEquals(graph.vertexSet().size(), graphReImp.vertexSet().size());
    }

    private void assertNodeAssociatedData(RoadGraph graph, RoadGraph graphReImp) {
        for (final Node vertex : graph.vertexSet()) {
            assertNodeData(vertex, graphReImp);

            assertNeighbors(vertex, graph, graphReImp);
        }
    }

    private void assertNodeData(final Node vertex, RoadGraph graphReImp) {
        assertEquals(vertex, graphReImp.getVertex(vertex.id));
    }

    private void assertNeighbors(final Node vertex, final RoadGraph graph, final RoadGraph graphReImp) {
        final Node graphNodeI = graph.getVertex(vertex.id);
        final Node reImpGraphNodeI = graphReImp.getVertex(vertex.id);

        Set<Edge> edges = graph.outgoingEdgesOf(graphNodeI);
        Set<Edge> edgesReImp = graphReImp.outgoingEdgesOf(reImpGraphNodeI);

        Iterator<Edge> iteratorEdges = edges.iterator();
        Iterator<Edge> iteratorEdgesReImp = edgesReImp.iterator();

        for (int j = 0; j < edges.size(); j++) {
            assertTrue(equal(graph, iteratorEdges.next(), graphReImp, iteratorEdgesReImp.next()));
        }
    }

    private boolean equal(final RoadGraph graphA, final Edge edgeA, final RoadGraph graphB, final Edge edgeB) {
        boolean equal = true;
        equal &= graphA.getEdgeSource(edgeA).equals(graphB.getEdgeSource(edgeB));
        equal &= graphA.getEdgeTarget(edgeA).equals(graphB.getEdgeTarget(edgeB));
        equal &= graphA.getEdgeWeight(edgeA) == graphB.getEdgeWeight(edgeB);

        return equal;
    }

    private void cleanUp() {
        File file = new File("exp.txt");
        assertTrue(file.delete());
    }
}
