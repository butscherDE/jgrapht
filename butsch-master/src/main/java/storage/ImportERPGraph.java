package storage;

import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.Config;
import evalutation.measurements.utils.StopWatchVerbose;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImportERPGraph implements GraphImporter {
    private final FileReader fileReader;
    private BufferedReader reader;
    private int autoId = 0;
    private final RoadGraph graph = new RoadGraph(Edge.class);

    public ImportERPGraph(final String path) throws FileNotFoundException {
        fileReader = createFileReader(path);
    }

    private FileReader createFileReader(String path) throws FileNotFoundException {
        return new FileReader(path);
    }

    @Override
    public RoadGraph createGraph() {

        final StopWatchVerbose sw = new StopWatchVerbose("Import time");
        try {
            parseDataAndCreateGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sw.printTimingIfVerbose();

        if (Config.VERBOSE) {
            System.out.println("Imported " + graph.vertexSet().size() + " Nodes and " + graph.edgeSet().size() + " edges.");
        }

        return graph;
    }

    private void parseDataAndCreateGraph() throws IOException {
        reader = new BufferedReader(fileReader);

        final int numNodes = getNumNodes();
        final int numEdges = getNumEdges();
        parseGraph(numNodes, numEdges);

        reader.close();
    }

    private void parseGraph(int numNodes, int numEdges) throws IOException {
        final Map<Long, Node> nodeList = new HashMap<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            parseNextNode(nodeList);
        }

        for (int i = 0; i < numEdges; i++) {
            parseNextEdge(nodeList);
        }
    }

    private int getNumNodes() throws IOException {
        return getNextMetadataLine();
    }

    private int getNumEdges() throws IOException {
        return getNextMetadataLine();
    }

    private int getNextMetadataLine() throws IOException {
        final String metaLine = reader.readLine();
        return Integer.parseInt(metaLine);
    }

    private void parseNextNode(final Map<Long, Node> nodeList) throws IOException {
        final String[] currentNodeTokens = getTokenizedNextLine();

        final Node newNode = createNodeFromTokens(currentNodeTokens);

        graph.addVertex(newNode);
        nodeList.put(newNode.id, newNode);
    }

    private Node createNodeFromTokens(String[] currentNodeTokens) {
        final long id;
        final double longitude;
        final double latitude;
        final double elevation;
        if (currentNodeTokens.length == 3) {
            id = autoId++;
            longitude = Double.parseDouble(currentNodeTokens[0]);
            latitude = Double.parseDouble(currentNodeTokens[1]);
            elevation = Double.parseDouble(currentNodeTokens[2]);
        } else if (currentNodeTokens.length == 4) {
            id = Long.parseLong(currentNodeTokens[0]);
            longitude = Double.parseDouble(currentNodeTokens[1]);
            latitude = Double.parseDouble(currentNodeTokens[2]);
            elevation = Double.parseDouble(currentNodeTokens[3]);
        } else {
            throw new NumberFormatException("Node has not the required number of fields");
        }

        return new Node(id, longitude, latitude, elevation);
    }

    private void parseNextEdge(final Map<Long, Node> nodes) throws IOException {
        final String[] currentEdgeTokens = getTokenizedNextLine();

        createEdgeFromTokens(currentEdgeTokens, nodes);
    }

    private void createEdgeFromTokens(String[] currentEdgeTokens, final Map<Long, Node> nodes) {
        final long baseNode = Long.parseLong(currentEdgeTokens[0]);
        final long adjNode = Long.parseLong(currentEdgeTokens[1]);
        final double cost = Double.parseDouble(currentEdgeTokens[2]);

        if (baseNode >= 0 && adjNode >= 0) {
            final Edge edge = graph.addEdge(nodes.get(baseNode), nodes.get(adjNode));
            if (edge != null) {
                graph.setEdgeWeight(edge, cost);
            }
        }
    }

    private String[] getTokenizedNextLine() throws IOException {
        final String currentLine = reader.readLine();
        return currentLine.split(" ");
    }
}
