package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolygonSimplifierFullGreedyExporting {
    private final GridIndex gridIndex;

    public PolygonSimplifierFullGreedyExporting(GridIndex gridIndex) {
        this.gridIndex = gridIndex;
    }

    public List<Polygon> simplify(Polygon polygon) {
        final List<Polygon> polygons = new ArrayList<>(Collections.singletonList(polygon));
        boolean simplified = true;

        while (simplified == true) {
            SimplerPolygonContractionSetBuilder cSetBuilder = new SimplerPolygonContractionSetBuilder(gridIndex, polygon);

            int maxSetSize[] = new int[] {-1, -1};
            int maxSetIndex = -1;
            for (int i = 0; i < polygon.getNumPoints(); i++) {
                int[] contractionSetSize = cSetBuilder.getContractionSetSize(i);

                if (contractionSetSize[0] + contractionSetSize[1] > 0) {
                    maxSetSize = contractionSetSize;
                    maxSetIndex = i;
                }
            }

            simplified = maxSetSize[0] + maxSetSize[1] > 0;
            if (simplified) {
                polygon = new PolygonLineContractor(polygon, maxSetIndex).getPolygon(maxSetSize[0], maxSetSize[1]);
                polygons.add(polygon);
            }
        }

        return polygons;
    }
}
