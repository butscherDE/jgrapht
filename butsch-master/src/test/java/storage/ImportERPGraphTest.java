package storage;

import data.Node;
import data.RoadGraph;
import evalutation.Config;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ImportERPGraphTest {
    // TODO check for to few nodes/edges in metadata
    // TODO check IndexOutOfBounds when to few numbers in a line.
    @Test
    void verifyImportedGraphBySampleNodesAndEdges() {
        try {
            final Importer importer = new ImportERPGraph(Config.GER_PATH);
            final RoadGraph graph = importer.createGraph();

            assertNode(new Node(2923, 48.978, 9.80345, 339), graph);
            assertNode(new Node(29396, 48.7022, 9.58405, 314), graph);
            assertNode(new Node(64090, 48.9163, 9.57486, 475), graph);
            assertNode(new Node(84727, 48.8323, 9.85168, 454), graph);
            assertNode(new Node(100216, 48.8566, 9.81015, 475), graph);

            final Node node0 = new Node(0, 48.7163, 9.63076, 313);
            final Node node1 = new Node(1, 48.716, 9.62991, 313);
            final Node node25000 = new Node(25000, 48.9049, 9.96355, 361);
            final Node node52142 = new Node(52145, 48.9051, 9.9631, 361);

            assertEdge(node0, node1, 75, graph);
            assertEdge(node25000, node52142, 35, graph);
//            assertEdge(new Edge(50000, 47763, 19, nodes), graph);
//            assertEdge(new Edge(75000, 74999, 69, nodes), graph);
//            assertEdge(new Edge(100000, 100001, 37, nodes), graph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void verifyErrorHandlingOnWrongPath() {
        assertThrows(FileNotFoundException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\asdfasdfasdfasdfasdfasdf100k_j_d.txt");
        });
    }

    @Test
    void verifyErrorHandlingOnMalformedNodeCount() {
        assertThrows(NumberFormatException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d_malformedNodeCount.txt");
            final Graph graph = importer.createGraph();
        });
    }

    @Test
    void verifyErrorHandlingOnMalformedEdgeCount() {
        assertThrows(NumberFormatException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d_malformedEdgeCount.txt");
            final Graph graph = importer.createGraph();
        });
    }

    @Test
    void verifyErrorHandlingOnMalformedNode() {
        assertThrows(NumberFormatException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d_malformedNode.txt");
            final Graph graph = importer.createGraph();
        });
    }

    @Test
    void verifyErrorHandlingOnMalformedEdge() {
        assertThrows(NumberFormatException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d_malformedNodeCount.txt");
            final Graph graph = importer.createGraph();
        });
    }

    @Test
    void verifyErrorHandlingOnWrongEdgeCount() {
        assertThrows(NullPointerException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d_wrongEdgeCount.txt");
            final Graph graph = importer.createGraph();
        });
    }

    @Test
    void verifyErrorHandlingOnMalformedWrongNodeCount() {
        assertThrows(NullPointerException.class, () -> {
            final Importer importer = new ImportERPGraph("C:\\Users\\Daniel\\Dropbox\\uni\\Sem11\\ERP\\100k_j_d_wrongNodeCount.txt");
            final Graph graph = importer.createGraph();
        });
    }

    private void assertNode(final Node node, final Graph graph) {
        assertTrue(graph.vertexSet().contains(node));
    }

    private void assertEdge(final Node source, final Node target, final double weight, final Graph graph) {
        final DefaultWeightedEdge edge = (DefaultWeightedEdge) graph.getEdge(source, target);
        assertEquals(weight, graph.getEdgeWeight(edge));
    }
}
