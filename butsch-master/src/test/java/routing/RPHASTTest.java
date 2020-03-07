package routing;

import data.*;
import org.junit.jupiter.api.Test;
import util.GeneralTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RPHASTTest {
    @Test
    public void multiTest() {
        final Set<Node> sources = new LinkedHashSet<>(Arrays.asList(new Node(0, 0, 0, 0),
                                                                        new Node(1,0,0,0),
                                                                        new Node (2,0,0,0)));
        final Set<Node> targets = new LinkedHashSet<>(Arrays.asList(new Node(0,0,0,0),
                                                                    new Node(1,0,0,0),
                                                                    new Node(2,0,0,0)));

        final RoadGraph graph = GeneralTestGraph.createTestGraph();
        final RoadCH roadCH = new CHPreprocessing(graph).createCHGraph();

        final Dijkstra dijkstra = new Dijkstra(graph);
        final RPHAST rphast = new RPHAST(roadCH, true);

        final List<Path> dijkstraPaths = dijkstra.findPaths(sources, targets);
        final List<Path> rphastPaths = rphast.findPaths(sources, targets);

        final Iterator<Path> dijkstraPathsIt = dijkstraPaths.iterator();
        final Iterator<Path> rphastPathsIt = rphastPaths.iterator();

        assertEquals(dijkstraPaths.size(), rphastPaths.size());
        while (dijkstraPathsIt.hasNext() && rphastPathsIt.hasNext()) {
            final Path dijkstraPath = dijkstraPathsIt.next();
            final Path rphastPath = rphastPathsIt.next();

            assertEquals(dijkstraPath.getWeight(), rphastPath.getWeight(), 0);
        }
    }
}
