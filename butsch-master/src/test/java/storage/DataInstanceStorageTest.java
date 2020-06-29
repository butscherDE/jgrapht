package storage;

import data.RoadGraph;
import evalutation.Config;
import evalutation.DataInstance;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataInstanceStorageTest {
    @Test
    public void exAndImport() {
        final String tempPath = System.getProperty("java.io.tmpdir");
        final String[] tempPaths = new String[] {
                tempPath + "graph.txt",
                tempPath + "vcs.txt",
                tempPath + "index.txt",
                tempPath + "relations.txt"
        };
        System.out.println(tempPath);
        final DataInstance expectedInstance = DataInstance.createFromImporter(new ImportPBF(Config.PBF_ANDORRA));
        DataInstanceStorage.export(expectedInstance, tempPaths);
        final DataInstance importedInstance = DataInstanceStorage.importInstance(tempPaths);

        final List<Long> expEdges = expectedInstance.graph.edgeSet()
                .stream()
                .map(e -> e.id)
                .sorted()
                .collect(Collectors.toList());
        final List<Long> impEdges = importedInstance.graph.edgeSet()
                .stream()
                .map(e -> e.id)
                .sorted()
                .collect(Collectors.toList());
//        assertEquals(expEdges, impEdges);

        assertEquals(expectedInstance, importedInstance);

        for (String path : tempPaths) {
            final File file = new File(path);
            assertTrue(file.delete());
        }
    }
}
