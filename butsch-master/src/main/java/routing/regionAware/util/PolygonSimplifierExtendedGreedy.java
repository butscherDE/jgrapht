package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public class PolygonSimplifierExtendedGreedy implements PolygonSimplifier {
    private final GridIndex gridIndex;
    private final Random random = new Random();
    private SimplerPolygonContractionSetBuilder cSetBuilder;
    private int optimalIndex = -1;
    private int[] optimalContractionSetSize = new int[] {-1, -1};
    private Polygon polygon;

    public PolygonSimplifierExtendedGreedy(GridIndex gridIndex) {
        this.gridIndex = gridIndex;
    }

    @Override
    public Polygon simplify(Polygon polygon) {
        this.polygon = polygon;
        boolean simplified = true;

        while (simplified == true) {
            cSetBuilder = new SimplerPolygonContractionSetBuilder(gridIndex, this.polygon);

            descent(random.nextInt(this.polygon.getNumPoints()), new int[] {-1, -1});


            simplified = getContractionSize(optimalContractionSetSize) > 0;
            if (simplified) {
                this.polygon = new PolygonLineContractor(this.polygon, optimalIndex).getPolygon(optimalContractionSetSize[0], optimalContractionSetSize[1]);
            }
        }

        return this.polygon;
    }

    private void descent(final int index, final int[] contractionSize) {
        final int[] newContractionSize = cSetBuilder.getContractionSetSize(index);

        if (getContractionSize(newContractionSize) > getContractionSize(contractionSize)) {
            descent((index + contractionSize[0]) % polygon.getNumPoints(), newContractionSize);
            descent((index - contractionSize[1]) % polygon.getNumPoints(), newContractionSize);
        }

        if (getContractionSize(newContractionSize) > getContractionSize(optimalContractionSetSize)) {
            optimalIndex = index;
            optimalContractionSetSize = newContractionSize;
        }
    }

    private int getContractionSize(int[] newContractionSize) {
        return newContractionSize[0] + newContractionSize[1];
    }
}
