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
        System.out.println("instance created");
        final BoundingBox limiter = new BoundingBox(0, 20, 40, 60);
        final AllVCLogger visitor = new AllVCLogger();
        instance.index.queryVisibilityCells(limiter, visitor);
        visitor.exp.writeJson();
    }

    private static class AllVCLogger implements GridIndex.GridIndexVisitor {
        final GeoJsonExporter exp = new GeoJsonExporter(Config.RESULTS + "allVcsTuebingen.geojson");
        int i = 0;

        @Override
        public void accept(Object entity, BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(Object entity) {
            VisibilityCell vc = (VisibilityCell) entity;

            exp.addPolygon(vc.getPolygon());
            System.out.println(i++ + "th vc added");
        }
    }
}
