package evalutation;

import data.NodeRelation;
import data.RoadGraph;
import index.GridIndex;
import storage.ImportPBF;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static DataInstance createFromData(final RoadGraph graph, final GridIndex index, final List<NodeRelation> relations) {
        return new DataInstance(graph, index, relations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataInstance that = (DataInstance) o;

        final boolean graphEqual = isGraphEqual(that);
        final boolean indexEqual = isIndexEqual(that);
        final boolean relationsEqual = relations.equals(that.relations);

        return graphEqual && indexEqual && relationsEqual;
    }

    private boolean isGraphEqual(DataInstance that) {
        final boolean verticesEqual = isVerticesEqual(that);
        final boolean edgesEqual = isEdgesEqual(that);
        return verticesEqual && edgesEqual;
    }

    private boolean isVerticesEqual(DataInstance that) {
        return graph.vertexSet().equals(that.graph.vertexSet());
    }

    private boolean isEdgesEqual(DataInstance that) {
        final List<Long> thisEdgeIds = graph.edgeSet()
                .stream()
                .map(e -> e.id)
                .collect(Collectors.toList());
        final List<Long> thatEdgeIds = that.graph.edgeSet()
                .stream()
                .map(e -> e.id)
                .collect(Collectors.toList());
        return thisEdgeIds.equals(thatEdgeIds);
    }

    private boolean isIndexEqual(DataInstance that) {
        final List<String> thisIndexDump = index.dump()
                .collect(Collectors.toList());
        final List<String> thatIndexDump = that.index.dump()
                .collect(Collectors.toList());
        return thisIndexDump.equals(thatIndexDump);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph, index, relations);
    }
}
