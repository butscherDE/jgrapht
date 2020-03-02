package storage;

import data.Edge;
import data.Node;
import data.RoadGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportERPGraph implements Importer{
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

        try {
            parseDataAndCreateGraph();
        } catch (IOException e) {
            e.printStackTrace();
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
        final int rank = getRank(currentNodeTokens);

        return new Node(autoId++, longitude, latitude, elevation, rank);
    }

    private int getRank(String[] currentNodeTokens) {
        final int rank;

        if (isRankPresentInData(currentNodeTokens)) {
            rank = 0;
        } else {
            rank = Integer.parseInt(currentNodeTokens[3]);
        }

        return rank;
    }

    private boolean isRankPresentInData(String[] currentNodeTokens) {
        return currentNodeTokens.length <= 3;
    }

    private void parseNextEdge(List<Node> nodes) throws IOException {
        final String[] currentEdgeTokens = getTokenizedNextLine();

        createEdgeFromTokens(currentEdgeTokens, nodes);
    }

    private void createEdgeFromTokens(String[] currentEdgeTokens, List<Node> nodes) {
        final int baseNode = Integer.parseInt(currentEdgeTokens[0]);
        final int adjNode = Integer.parseInt(currentEdgeTokens[1]);
        final double cost = Double.parseDouble(currentEdgeTokens[2]);

        final Edge edge = graph.addEdge(nodes.get(baseNode), nodes.get(adjNode));
        if (edge != null) {
            graph.setEdgeWeight(edge, cost);
        }
    }

    private String[] getTokenizedNextLine() throws IOException {
        final String currentLine = reader.readLine();
        return currentLine.split(" ");
    }
}
