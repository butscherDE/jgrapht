package util;

import data.Edge;
import data.Node;
import data.RoadGraph;

import java.util.ArrayList;
import java.util.List;

public class GeneralTestGraph implements TestGraph {
    public static RoadGraph createTestGraph() {
        final RoadGraph graph = new RoadGraph(Edge.class);

        final List<Node> nodes = createTestGraphNodes();
        for (Node node : nodes) {
            graph.addVertex(node);
        }

        graph.addEdge(nodes.get(0), nodes.get(1));
        graph.addEdge(nodes.get(0), nodes.get(3));
        graph.addEdge(nodes.get(1), nodes.get(0));
        graph.addEdge(nodes.get(1), nodes.get(2));
        graph.addEdge(nodes.get(1), nodes.get(4));
        graph.addEdge(nodes.get(2), nodes.get(1));
        graph.addEdge(nodes.get(2), nodes.get(4));
        graph.addEdge(nodes.get(3), nodes.get(0));
        graph.addEdge(nodes.get(3), nodes.get(4));
        graph.addEdge(nodes.get(3), nodes.get(6));
        graph.addEdge(nodes.get(4), nodes.get(1));
        graph.addEdge(nodes.get(4), nodes.get(2));
        graph.addEdge(nodes.get(4), nodes.get(3));
        graph.addEdge(nodes.get(4), nodes.get(5));
        graph.addEdge(nodes.get(4), nodes.get(7));
        graph.addEdge(nodes.get(4), nodes.get(8));
        graph.addEdge(nodes.get(5), nodes.get(4));
        graph.addEdge(nodes.get(5), nodes.get(7));
        graph.addEdge(nodes.get(5), nodes.get(8));
        graph.addEdge(nodes.get(6), nodes.get(3));
        graph.addEdge(nodes.get(6), nodes.get(7));
        graph.addEdge(nodes.get(7), nodes.get(4));
        graph.addEdge(nodes.get(7), nodes.get(5));
        graph.addEdge(nodes.get(7), nodes.get(6));
        graph.addEdge(nodes.get(8), nodes.get(4));
        graph.addEdge(nodes.get(8), nodes.get(5));

        return graph;
    }


    public static List<Node> createTestGraphNodes() {
        final List<Double> longitudes = createTestGraphLongitudes();
        final List<Double> latitudes = createTestGraphLatitudes();
        final List<Integer> elevations = createTestGraphElevations();

        final List<Node> nodes = new ArrayList<>(longitudes.size());
        for (int i = 0; i < longitudes.size(); i++) {
            nodes.add(new Node(i, longitudes.get(i), latitudes.get(i), elevations.get(i)));
        }

        return nodes;
    }

    private static List<Double> createTestGraphLongitudes() {
        final List<Double> longitudes = new ArrayList<>(9);

        longitudes.add(0.0);
        longitudes.add(2.0);
        longitudes.add(4.0);
        longitudes.add(0.0);
        longitudes.add(2.0);
        longitudes.add(6.0);
        longitudes.add(0.0);
        longitudes.add(2.0);
        longitudes.add(4.0);

        return longitudes;
    }

    private static List<Double> createTestGraphLatitudes() {
        final List<Double> latitudes = new ArrayList<>(9);

        latitudes.add(0.0);
        latitudes.add(0.0);
        latitudes.add(0.0);
        latitudes.add(2.0);
        latitudes.add(2.0);
        latitudes.add(2.0);
        latitudes.add(4.0);
        latitudes.add(4.0);
        latitudes.add(4.0);

        return latitudes;
    }

    private static List<Integer> createTestGraphElevations() {
        final List<Integer> elevations = new ArrayList<>(9);

        elevations.add(0);
        elevations.add(1);
        elevations.add(2);
        elevations.add(3);
        elevations.add(4);
        elevations.add(5);
        elevations.add(6);
        elevations.add(7);
        elevations.add(8);

        return elevations;
    }
}
