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
import java.util.ArrayList;
import java.util.List;

public class ImportERPGraph implements Importer {
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
        final List<Node> nodeList = new ArrayList<>(numNodes);
        for (int i = 0; i < numNodes; i++) {
            parseNextNode(nodeList);
        }

        final List<Node> filteredNodeList = nodeList;
//        final List<Node> filteredNodeList = getFilteredNodeList(numNodes, nodeList);


        for (int i = 0; i < numEdges; i++) {
            parseNextEdge(filteredNodeList);
        }
    }

    private List<Node> getFilteredNodeList(final int numNodes, final List<Node> nodeList) {
        double maxLatitude = Double.NEGATIVE_INFINITY;
        double minLatitude = Double.POSITIVE_INFINITY;
        double maxLongitude = Double.NEGATIVE_INFINITY;
        double minLongitude = Double.POSITIVE_INFINITY;
        for (final Node node : nodeList) {
            maxLatitude = Math.max(maxLatitude, node.latitude);
            minLatitude = Math.min(minLatitude, node.latitude);
            maxLongitude = Math.max(maxLongitude, node.longitude);
            minLongitude = Math.min(minLongitude, node.longitude);
        }
        double latDivisionLine = minLatitude + (maxLatitude - minLatitude) / 3;
        double lonDivisionLine = minLongitude + (maxLongitude - minLongitude) / 3;

        int newId = 0;
        final List<Node> filteredNodeList = new ArrayList<>(numNodes);
        for (final Node node : nodeList) {
            if (node.latitude < latDivisionLine && node.longitude < lonDivisionLine) {
                final Node newNode = new Node(newId++, node.longitude, node.latitude, node.elevation);
                graph.addVertex(newNode);
                filteredNodeList.add(newNode);
            }
        }
        return filteredNodeList;
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

    private void parseNextNode(List<Node> nodeList) throws IOException {
        final String[] currentNodeTokens = getTokenizedNextLine();

        final Node newNode = createNodeFromTokens(currentNodeTokens);

        graph.addVertex(newNode);
        nodeList.add(newNode);
    }

    private Node createNodeFromTokens(String[] currentNodeTokens) {
        final double longitude = Double.parseDouble(currentNodeTokens[0]);
        final double latitude = Double.parseDouble(currentNodeTokens[1]);
        final double elevation = Double.parseDouble(currentNodeTokens[2]);

        return new Node(autoId++, longitude, latitude, elevation);
    }

    private void parseNextEdge(List<Node> nodes) throws IOException {
        final String[] currentEdgeTokens = getTokenizedNextLine();

        createEdgeFromTokens(currentEdgeTokens, nodes);
    }

    private void createEdgeFromTokens(String[] currentEdgeTokens, List<Node> nodes) {
        final int baseNode = Integer.parseInt(currentEdgeTokens[0]);
        final int adjNode = Integer.parseInt(currentEdgeTokens[1]);
        final double cost = Double.parseDouble(currentEdgeTokens[2]);

        try {
            final Edge edge = graph.addEdge(nodes.get(baseNode), nodes.get(adjNode));
            if (edge != null) {
                graph.setEdgeWeight(edge, cost);
            }
        } catch (Exception e) {

        }
    }

    private String[] getTokenizedNextLine() throws IOException {
        final String currentLine = reader.readLine();
        return currentLine.split(" ");
    }
}
