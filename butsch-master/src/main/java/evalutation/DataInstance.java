package evalutation;

import data.NodeRelation;
import data.RoadGraph;
import index.GridIndex;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.util.List;

public class DataInstance {
    public final RoadGraph graph;
    public final GridIndex index;
    public final List<NodeRelation> relations;

    private DataInstance(RoadGraph graph, GridIndex index, List<NodeRelation> relations) {
        this.graph = graph;
        this.index = index;
        this.relations = relations;
    }

    public static DataInstance createFromImporter(final ImportPBF importer) {
        try {
            final RoadGraph graph = importer.createGraph();
            final GridIndex index = new GridIndex(graph, 100, 100);
            final List<NodeRelation> nodeRelations = importer.getNodeRelations();

            return new DataInstance(graph, index, nodeRelations);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
