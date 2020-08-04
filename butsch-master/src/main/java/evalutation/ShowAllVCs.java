package evalutation;

import data.VisibilityCell;
import geometry.BoundingBox;
import index.GridIndex;
import storage.GeoJsonExporter;
import storage.ImportPBF;

import java.util.LinkedList;
import java.util.List;

public class ShowAllVCs {
    public static void main(String[] args) {
        final DataInstance instance = DataInstance.createFromImporter(new ImportPBF(Config.PBF_TUEBINGEN));
        final BoundingBox limiter = new BoundingBox(-200, 200, -100, 100);
        final AllVCLogger visitor = new AllVCLogger();
        instance.index.queryVisibilityCells(limiter, visitor);

        final GeoJsonExporter exp = new GeoJsonExporter("exp");
        visitor.vcs.forEach(vc -> exp.addPolygon(vc.getPolygon()));

        exp.writeJson();
    }

    private static class AllVCLogger implements GridIndex.GridIndexVisitor {
        public final List<VisibilityCell> vcs = new LinkedList<>();
        @Override
        public void accept(Object entity, BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(Object entity) {
            vcs.add((VisibilityCell) entity);
        }
    }
}
