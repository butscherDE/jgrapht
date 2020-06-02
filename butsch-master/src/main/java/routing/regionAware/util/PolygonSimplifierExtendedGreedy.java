package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

import java.util.Random;

public class PolygonSimplifierExtendedGreedy implements PolygonSimplifier {
    private final GridIndex gridIndex;
    private final Random random = new Random();
    private SimplerPolygonContractionSetBuilder cSetBuilder;
    private int optimalIndex;
    private int[] optimalContractionSetSize;
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
            optimalIndex = -1;
            optimalContractionSetSize = new int[] {-1, -1};

            recurseAroundPolygon(random.nextInt(this.polygon.getNumPoints()), new int[] {-1, -1}, new int[] {0, 0});

            simplified = getContractionSize(optimalContractionSetSize) > 0;
            if (simplified) {
                this.polygon = new PolygonLineContractor(this.polygon, optimalIndex).getPolygon(optimalContractionSetSize[0], optimalContractionSetSize[1]);
            }
        }

        return this.polygon;
    }

    private void recurseAroundPolygon(final int index, final int[] contractionSize, final int[] expanded) {
        if (getContractionSize(expanded) > polygon.getNumPoints()) {
            return;
        }

        final int[] newContractionSize = descent(index, contractionSize, expanded);

        updateOptimalResult(index, newContractionSize);
    }

    private int[] descent(int index, int[] contractionSize, int[] expanded) {
        final int[] newContractionSize = cSetBuilder.getContractionSetSize(index);

        if (getContractionSize(newContractionSize) > getContractionSize(contractionSize)) {
            expanded[0] += newContractionSize[0];
            expanded[1] += newContractionSize[1];
            final int nextForwardIndex = (index + contractionSize[0]) % polygon.getNumPoints();
            final int nextBackwardIndex = (index - contractionSize[1]) % polygon.getNumPoints();
            recurseAroundPolygon(nextForwardIndex, newContractionSize, expanded);
            recurseAroundPolygon(nextBackwardIndex, newContractionSize,expanded);
        }
        return newContractionSize;
    }

    private void updateOptimalResult(int index, int[] newContractionSize) {
        if (getContractionSize(newContractionSize) > getContractionSize(optimalContractionSetSize)) {
            optimalIndex = index;
            optimalContractionSetSize = newContractionSize;
        }
    }

    private int getContractionSize(int[] newContractionSize) {
        return newContractionSize[0] + newContractionSize[1];
    }
}
