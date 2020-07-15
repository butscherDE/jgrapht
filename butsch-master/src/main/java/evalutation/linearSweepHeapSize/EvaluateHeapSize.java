package evalutation.linearSweepHeapSize;

import data.NodeRelation;
import evalutation.Config;
import org.locationtech.jts.geom.Coordinate;
import storage.CsvColumnDumper;
import storage.ImportPBF;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EvaluateHeapSize {
    private final static String PBF = Config.PBF_BAWU;
    private final static char SEPARATOR = ',';
    private final static String[] DUMP_HEADER = new String[]{"regionid", "regionsize", "max"};
    private final static String RESULT_PATH = Config.HEAPMAX + LocalDateTime
            .now()
            .toString()
            .replaceAll(":", "_") + ".csv";

    public static void main(String[] args) throws IOException {
        final ImportPBF importPBF = new ImportPBF(PBF);
        final List<NodeRelation> nodeRelations = importPBF.importRelationsOnly();
        System.out.println("imported");

        final List<List<Object>> elements = getResults(nodeRelations);
        System.out.println("results gathered");

        new CsvColumnDumper(RESULT_PATH, DUMP_HEADER, elements, SEPARATOR).dump();
        System.out.println("results written");

        System.out.println("Processed " + elements.size() + " relations.");
    }

    public static List<List<Object>> getResults(final List<NodeRelation> nodeRelations) {
        final List<Object> regionIds = nodeRelations.stream().map(r -> r.id).collect(Collectors.toList());
        final List<Object> regionSizes = nodeRelations
                .stream()
                .map(r -> r.coordinates.length - 1)
                .collect(Collectors.toList());
        final List<Object> maximas = nodeRelations
                .stream()
                .map(r -> r.coordinates)
                .map(c -> getSortedPoints(c))
                .map(ls -> getMaxHeapSize(ls))
                .collect(Collectors.toList());
        return Arrays.asList(regionIds, regionSizes, maximas);
    }

    private static List<PolyPoint> getSortedPoints(final Coordinate[] coordinates) {
        final ArrayList<PolyPoint> lineSegments = toLineSegments(coordinates);
        Collections.sort(lineSegments, Comparator.comparingDouble(p -> p.x));

        return lineSegments;
    }

    private static ArrayList<PolyPoint> toLineSegments(final Coordinate[] coordinates) {
        final ArrayList<PolyPoint> points = new ArrayList<>(2 * coordinates.length);

        for (int i = 0; i < coordinates.length - 1; i++) {
            final Coordinate p = coordinates[i];
            final Coordinate q = coordinates[i + 1];

            if (p.x <= q.x) {
                addAsForwardLine(points, p, q);
            } else {
                addAsBackwardLine(points, p, q);
            }
        }

        return points;
    }

    private static void addAsForwardLine(final ArrayList<PolyPoint> points, final Coordinate p, final Coordinate q) {
        final PolyPoint start = new PolyPoint(p.x);
        final PolyPoint end = new PolyPoint(q.x, start);
        points.add(start);
        points.add(end);
    }

    private static void addAsBackwardLine(final ArrayList<PolyPoint> points, final Coordinate p, final Coordinate q) {
        final PolyPoint start = new PolyPoint(q.x);
        final PolyPoint end = new PolyPoint(p.x, start);
        points.add(start);
        points.add(end);
    }

    private static int getMaxHeapSize(final List<PolyPoint> sortedPoints) {
        int currentSize = 0;
        int max = 0;

        for (final PolyPoint sortedPoint : sortedPoints) {
            if (sortedPoint.isStartPoint()) {
                currentSize++;
            } else {
                currentSize--;
            }

            max = Math.max(max, currentSize);
        }

        return max;
    }

    private static class PolyPoint {
        private final double x;
        private final PolyPoint startPoint;

        public PolyPoint(final double x) {
            this.x = x;
            this.startPoint = null;
        }

        public PolyPoint(final double x, final PolyPoint startPoint) {
            this.x = x;
            this.startPoint = startPoint;
        }

        private boolean isStartPoint() {
            return startPoint == null;
        }
    }
}
