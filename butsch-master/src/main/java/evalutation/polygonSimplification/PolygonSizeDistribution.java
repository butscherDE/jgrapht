package evalutation.polygonSimplification;

import data.NodeRelation;
import data.RoadGraph;
import evalutation.Config;
import storage.CsvColumnDumper;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PolygonSizeDistribution {
    public static void main(String[] args) {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_BAWU);
        try {
            importPBF.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations();

        final String path = Config.POLY_SIMPLIFICATION + "\\bawu_dist.csv";
        final String[] headers = {"id", "size"};
        final List<List<Object>> elements = new ArrayList<>(2);
        elements.add(new ArrayList<>(nodeRelations.size()));
        elements.add(new ArrayList<>(nodeRelations.size()));

        nodeRelations.forEach(r -> {
            elements.get(0).add(r.id);
            elements.get(1).add(r.nodes.size());
        });

        try {
            new CsvColumnDumper(path, headers, elements, ',').dump();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Processed " + nodeRelations.size() + " Relations.");
    }
}
