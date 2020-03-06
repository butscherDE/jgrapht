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
            final Importer importer = new ImportERPGraph("exp.txt");
            final RoadGraph graphReImp = importer.createGraph();

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
        for (int i = 0; i < graph.vertexSet().size(); i++) {
            assertNodeData(graph, graphReImp, i);

            assertNeighbors(graph, graphReImp, i);
        }
    }

    private void assertNodeData(RoadGraph graph, RoadGraph graphReImp, int i) {
        assertEquals(graph.getVertex(i), graphReImp.getVertex(i));
    }

    private void assertNeighbors(RoadGraph graph, RoadGraph graphReImp, int i) {
        final Node graphNodeI = graph.getVertex(i);
        final Node reImpGraphNodeI = graphReImp.getVertex(i);

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
