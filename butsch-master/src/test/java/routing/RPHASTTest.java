package routing;

import data.*;
import org.junit.jupiter.api.Test;
import util.GeneralTestGraph;
import util.PolygonRoutingTestGraph;

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

    @Test
    public void unreachableNode() {
        final Set<Node> sources = new LinkedHashSet<>(Arrays.asList(new Node(0, 0, 0, 0)));
        final Set<Node> targets = new LinkedHashSet<>(Arrays.asList(new Node(103,0,0,0),
                                                                    new Node(1,0,0,0),
                                                                    new Node(2,0,0,0)));

        final RoadGraph graph = new PolygonRoutingTestGraph().graph;
        graph.removeEdge(graph.getEdge(new Node(104,0,0,0), new Node(103,0,0,0)));
        final RoadCH roadCH = new CHPreprocessing(graph).createCHGraph();

        final RPHAST rphast = new RPHAST(roadCH, true);
        final List<Path> rphastPaths = rphast.findPaths(sources, targets);


        assertEquals(Double.MAX_VALUE, rphastPaths.get(0).getWeight());
        assertEquals(8.0, rphastPaths.get(1).getWeight());
        assertEquals(16.0, rphastPaths.get(2).getWeight());
    }
}
