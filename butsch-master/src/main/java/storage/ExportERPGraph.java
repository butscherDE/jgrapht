package storage;

import data.Edge;
import data.Node;
import data.RoadGraph;

import java.io.FileWriter;
import java.io.IOException;

public class ExportERPGraph implements GraphExporter {
    private final RoadGraph graphToExport;
    private final FileWriter fileWriter;

    public ExportERPGraph(final RoadGraph graphToExport, final String path) throws IOException {
        this.graphToExport = graphToExport;
        fileWriter = new FileWriter(path);
    }

    @Override
    public void export() throws IOException {
        exportMetaData();
        exportNodes();
        exportEdges();
        fileWriter.close();
    }

    private void exportMetaData() throws IOException {
        fileWriter.write(graphToExport.vertexSet().size() + "\n");
        fileWriter.write(graphToExport.edgeSet().size() + "\n");
    }

    private void exportNodes() throws IOException {
        final StringBuilder sb = new StringBuilder();

        for (Node node : graphToExport.vertexSet()) {
            sb.append(node.longitude).append(" ").append(node.latitude).append(" ").append(node.elevation).append(" ").append("\n");
        }

        fileWriter.write(sb.toString());
    }

    private void exportEdges() throws IOException {
        final StringBuilder sb = new StringBuilder();

        for (Edge edge : graphToExport.edgeSet()) {
            final Node baseNode = graphToExport.getEdgeSource(edge);
            final Node adjNode = graphToExport.getEdgeTarget(edge);
            final double weight = graphToExport.getEdgeWeight(edge);

            sb.append(baseNode.id).append(" ").append(adjNode.id).append(" ").append(weight).append("\n");

        }

        fileWriter.write(sb.toString());
    }
}
