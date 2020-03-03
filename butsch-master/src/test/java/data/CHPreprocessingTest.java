package data;

import evalutation.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import storage.ImportERPGraph;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CHPreprocessingTest {
    private static RoadGraph graph;
    private static RoadCH roadCH;

    @BeforeAll
    public static void  importERPGraph() {
        try {
            graph = new ImportERPGraph(Config.GER_PATH).createGraph();
            final CHPreprocessing chProcessor = new CHPreprocessing(graph);
            roadCH = chProcessor.createCHGraph();
        } catch (IOException e) {
            fail();
            throw new IllegalStateException("Test failed");
        }
    }

    @Test
    public void startPreproc() {
        Set<Node> graphNodes = graph.vertexSet();
        Set<Node> chNodes = roadCH.getGraph().vertexSet();

        assertEquals(graphNodes.size(), chNodes.size());
    }

    @Test
    public void learningTestUpwardsEdges() {
        RoadGraph chGraph = roadCH.getGraph();

        Iterator<Node> graphNodes = graph.vertexSet().iterator();
        Iterator<Node> chNodes = roadCH.getGraph().vertexSet().iterator();

        while(graphNodes.hasNext()) {
            Node graphNode = graphNodes.next();
            Node chNode = chNodes.next();

            Set<Edge> graphIncidentEdges = graph.outgoingEdgesOf(graphNode);
            Set<Edge> chIncidentEdges = chGraph.outgoingEdgesOf(chNode);

            assertEquals(graphIncidentEdges.size(), chIncidentEdges.size());

            Iterator<Edge> graphNeighborIterator = graphIncidentEdges.iterator();
            Iterator<Edge> chNeighborIterator = chIncidentEdges.iterator();

            while (graphNeighborIterator.hasNext()) {
                Edge graphNeighbor = graphNeighborIterator.next();
                Edge chNeighbor = chNeighborIterator.next();

                assertEquals(graph.getEdgeTarget(graphNeighbor), chGraph.getEdgeTarget(chNeighbor));
            }
        }
    }
}
