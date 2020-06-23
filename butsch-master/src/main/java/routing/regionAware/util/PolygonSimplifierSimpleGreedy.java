package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public class PolygonSimplifierSimpleGreedy extends PolygonSimplifier {
    private final GridIndex gridIndex;
    private final Random random = new Random();

    public PolygonSimplifierSimpleGreedy(GridIndex gridIndex) {
        this.gridIndex = gridIndex;
    }

    @Override
    public Polygon simplify(Polygon polygon) {
        boolean simplified = true;

        while (simplified == true) {
            SimplerPolygonContractionSetBuilder cSetBuilder = new SimplerPolygonContractionSetBuilder(gridIndex, polygon);

            final int index = random.nextInt(polygon.getNumPoints());
            final int[] contractionSetSize = cSetBuilder.getContractionSetSize(index);


            simplified = contractionSetSize[0] + contractionSetSize[1] > 0;
            if (simplified) {
                polygon = new PolygonLineContractor(polygon, index).getPolygon(contractionSetSize[0], contractionSetSize[1]);
                contractions++;
            }
        }

        return polygon;
    }
}
