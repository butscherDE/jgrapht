package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;

public class PolygonSimplifierFullGreedy extends PolygonSimplifier {
    private final GridIndex gridIndex;

    public PolygonSimplifierFullGreedy(GridIndex gridIndex) {
        this.gridIndex = gridIndex;
    }

    @Override
    public Polygon simplify(Polygon polygon) {
        boolean simplified = true;

        while (simplified == true) {
            SimplerPolygonContractionSetBuilder cSetBuilder = new SimplerPolygonContractionSetBuilder(gridIndex, polygon);

            int maxSetSize[] = new int[] {-1, -1};
            int maxSetIndex = -1;
            for (int i = 0; i < polygon.getNumPoints(); i++) {
                int[] contractionSetSize = cSetBuilder.getContractionSetSize(i);

                if (contractionSetSize[0] + contractionSetSize[1] > maxSetSize[0] + maxSetSize[1]) {
                    maxSetSize = contractionSetSize;
                    maxSetIndex = i;
                }
            }

            simplified = maxSetSize[0] + maxSetSize[1] > 0;
            if (simplified) {
                polygon = new PolygonLineContractor(polygon, maxSetIndex).getPolygon(maxSetSize[0], maxSetSize[1]);
                contractions++;
            }
        }

        return polygon;
    }
}
