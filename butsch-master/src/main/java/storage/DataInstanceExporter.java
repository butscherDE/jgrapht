package storage;

import data.RoadGraph;
import data.VisibilityCell;
import evalutation.DataInstance;
import geometry.BoundingBox;
import index.GridIndex;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataInstanceExporter {
    public static void export(final DataInstance instance, final String folder, final String name) {
        final String graphPath = folder + name + "_graph.txt";
        final String vcPath = folder + name + "_vc.txt";
        final String indexPath = folder + name + "_index.txt";
        final String relationsPath = folder + name + "_relations.txt";


    }

    private void exportGraph(final RoadGraph graph, final String path) {
        try {
            final ExportERPGraph exportERPGraph = new ExportERPGraph(graph, path);
            exportERPGraph.export();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportVcs(final GridIndex index, final String path) {
        final VCLogger vcLogger = new VCLogger();
        index.queryVisibilityCells(new BoundingBox(-180, 180, -90, 90), vcLogger);

//        vcLogger.cells
    }

    private class VCLogger implements GridIndex.GridIndexVisitor<VisibilityCell> {
        private Set<VisibilityCell> cells = new LinkedHashSet<>();

        @Override
        public void accept(final VisibilityCell entity, final BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(final VisibilityCell entity) {
            cells.add(entity);
        }
    }
}
