package storage;


import data.RoadGraph;
import evalutation.Config;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.fail;

public class ImportPBFTest {
    @Test
    public void importTest() {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_LUXEMBOURG);

        final RoadGraph graph;
        try {
            graph = importPBF.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
            throw new IllegalStateException("Could not read data in");
        }

        System.out.println(graph.vertexSet().size());
        System.out.println(graph.edgeSet().size());

        // TODO find out how to verify a graph
    }
}
