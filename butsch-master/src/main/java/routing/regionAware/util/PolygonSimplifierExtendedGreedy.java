package routing.regionAware.util;

import index.GridIndex;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;
import java.util.Random;

public class PolygonSimplifierExtendedGreedy extends PolygonSimplifier {
    private final GridIndex gridIndex;
    private final Random random = new Random();
    private SimplerPolygonContractionSetBuilder cSetBuilder;
    private Polygon polygon;
    private int[] maxSetSize;
    private int maxIndex;
    private int enlargedForward;
    private boolean simplified;

    public PolygonSimplifierExtendedGreedy(GridIndex gridIndex) {
        this.gridIndex = gridIndex;
    }

    @Override
    public Polygon simplify(Polygon polygon) {
        this.polygon = polygon;
        simplified = true;

        while (simplified) {
            cSetBuilder = new SimplerPolygonContractionSetBuilder(gridIndex, this.polygon);
            final int startIndex = random.nextInt(this.polygon.getNumPoints());
            final int[] startSetSize = cSetBuilder.getContractionSetSize(startIndex);

            enlarge(startIndex, startSetSize, 0);
            final int[] forwardMaxSetSize = Arrays.copyOf(maxSetSize, 2);
            final int forwardMaxIndex = maxIndex;

            enlarge(startIndex, startSetSize, 1);
            final int[] backwardMaxSetSize = Arrays.copyOf(maxSetSize, 2);
            final int backwardMaxIndex = maxIndex;

            updatePolygon(forwardMaxSetSize, forwardMaxIndex, backwardMaxSetSize, backwardMaxIndex);
        }

        return this.polygon;
    }

    public void enlarge(final int startIndex, final int[] startSetSize, final int backward) {
        int index = startIndex;
        maxSetSize = startSetSize;
        maxIndex = startIndex;
        int[] newSetSize = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        while (getSetSize(newSetSize) > getSetSize(maxSetSize) && getSetSize(
                startSetSize) + enlargedForward < this.polygon.getNumPoints()) {
            index = Math.floorMod(index + maxSetSize[backward], this.polygon.getNumPoints());
            newSetSize = cSetBuilder.getContractionSetSize(index);

            if (getSetSize(newSetSize) > getSetSize(maxSetSize)) {
                enlargedForward += newSetSize[backward];
                maxSetSize = newSetSize;
                maxIndex = index;
            }
        }
    }

    public void updatePolygon(final int[] forwardMaxSetSize, final int forwardMaxIndex,
                              final int[] backwardMaxSetSize, final int backwardMaxIndex) {
        if (getSetSize(forwardMaxSetSize) + getSetSize(backwardMaxSetSize) <= 0) {
            simplified = false;
        } else if (getSetSize(forwardMaxSetSize) > getSetSize(backwardMaxSetSize)) {
            this.polygon = new PolygonLineContractor(this.polygon, forwardMaxIndex).getPolygon(forwardMaxSetSize[0], forwardMaxSetSize[1]);
            contractions++;
        } else {
            this.polygon = new PolygonLineContractor(this.polygon, backwardMaxIndex).getPolygon(backwardMaxSetSize[0], backwardMaxSetSize[1]);
            contractions++;
        }
    }

    private int getSetSize(int[] newContractionSize) {
        return newContractionSize[0] + newContractionSize[1];
    }
}
