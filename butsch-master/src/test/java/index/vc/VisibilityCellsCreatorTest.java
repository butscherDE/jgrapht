package index.vc;

import data.Node;
import data.RoadGraph;
import data.VisibilityCell;
import index.GridIndex;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import util.PolygonRoutingTestGraph;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisibilityCellsCreatorTest {
    private final static PolygonRoutingTestGraph GRAPH_MOCKER = PolygonRoutingTestGraph.DEFAULT_INSTANCE;

    @Test
    public void allCellsFound() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        graph.edgeSet().stream().forEach(a -> System.out.println(new ReflectiveEdge(a, graph)));

        final List<List<Long>> actualCellsAsNodesSorted = createAndSortData();
        final List<List<Long>> expectedCellsAsNodesSorted = getGroundTruth();
        assertEquals(expectedCellsAsNodesSorted, actualCellsAsNodesSorted);
    }

    public List<List<Long>> createAndSortData() {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final GridIndex index = GRAPH_MOCKER.gridIndex;

        final List<VisibilityCell> visibilityCells = createVCs(graph);
        final Set<List<Long>> cellsAsNodesUnique = convertToNodeIds(index, visibilityCells);
        final List<List<Long>> cellsAsNodes = asSortedList(cellsAsNodesUnique);

        return cellsAsNodes;
    }

    public List<VisibilityCell> createVCs(final RoadGraph graph) {
        final VisibilityCellsCreator vcc = new VisibilityCellsCreator(graph);
        return vcc.create();
    }

    public Set<List<Long>> convertToNodeIds(final GridIndex index, final List<VisibilityCell> visibilityCells) {
        final Set<List<Long>> cellsAsNodesUnique = new LinkedHashSet<>(visibilityCells.size());
        visibilityCells.forEach(a -> {
            final List<Long> nodeIds = getVCsNodes(index, a);
            Collections.sort(nodeIds);
            cellsAsNodesUnique.add(nodeIds);
        });
        return cellsAsNodesUnique;
    }

    public List<List<Long>> asSortedList(final Set<List<Long>> cellsAsNodesUnique) {
        final List<List<Long>> cellsAsNodes = new ArrayList<>(cellsAsNodesUnique);
        Collections.sort(cellsAsNodes, (a, b) -> longListCompare(a, b));
        return cellsAsNodes;
    }

    public List<Long> getVCsNodes(final GridIndex index, final VisibilityCell a) {
        final Coordinate[] coordinates = a.getPolygon().getCoordinates();
        final List<Long> nodeIds = new ArrayList<>(coordinates.length - 1);
        for (int i = 0; i < coordinates.length - 1; i++) {
            final Node closestNode = index.getClosestNode(coordinates[i].getX(), coordinates[i].getY());
            nodeIds.add(closestNode.id);
        }
        return nodeIds;
    }

    public int longListCompare(final List<Long> a, final List<Long> b) {
        final Iterator<Long> aIt = a.iterator();
        final Iterator<Long> bIt = b.iterator();

        while (aIt.hasNext() && bIt.hasNext()) {
            final Long aNext = aIt.next();
            final Long bNext = bIt.next();

            final int compare = Long.compare(aNext, bNext);
            if (compare != 0) {
                return compare;
            }
        }

        if (aIt.hasNext()) {
            return 1;
        } else if (bIt.hasNext()) {
            return -1;
        } else {
            return 0;
        }
    }

    public List<List<Long>> getGroundTruth() {
        final List<List<Long>> groundTruth = new LinkedList<>();

        groundTruth.add(Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 14L, 18L, 19L, 20L, 23L, 24L, 25L, 27L, 103L, 104L, 104L, 104L, 105L, 106L, 106L, 107L, 108L));
        groundTruth.add(Arrays.asList(0L, 1L, 7L));
        groundTruth.add(Arrays.asList(0L, 7L, 19L));
        groundTruth.add(Arrays.asList(1L, 2L, 28L));
        groundTruth.add(Arrays.asList(1L, 7L, 8L, 44L));
        groundTruth.add(Arrays.asList(1L, 8L, 45L));
        groundTruth.add(Arrays.asList(1L, 28L, 45L, 46L));
        groundTruth.add(Arrays.asList(2L, 3L, 29L));
        groundTruth.add(Arrays.asList(2L, 28L, 29L));
        groundTruth.add(Arrays.asList(3L, 4L, 9L));
        groundTruth.add(Arrays.asList(3L, 9L, 30L));
        groundTruth.add(Arrays.asList(3L, 29L, 30L));
        groundTruth.add(Arrays.asList(4L, 4L, 5L, 5L, 10L, 10L, 12L, 13L, 13L));
        groundTruth.add(Arrays.asList(4L, 9L, 10L, 12L, 31L, 31L, 32L, 49L, 200L, 200L, 202L));
        groundTruth.add(Arrays.asList(5L, 6L, 14L));
        groundTruth.add(Arrays.asList(5L, 11L, 13L, 16L));
        groundTruth.add(Arrays.asList(5L, 11L, 14L, 16L));
        groundTruth.add(Arrays.asList(7L, 19L, 42L, 43L, 53L));
        groundTruth.add(Arrays.asList(7L, 43L, 44L));
        groundTruth.add(Arrays.asList(8L, 44L, 45L));
        groundTruth.add(Arrays.asList(9L, 30L, 31L));
        groundTruth.add(Arrays.asList(12L, 13L, 15L));
        groundTruth.add(Arrays.asList(12L, 15L, 32L, 33L));
        groundTruth.add(Arrays.asList(13L, 15L, 16L));
        groundTruth.add(Arrays.asList(14L, 15L, 16L, 18L));
        groundTruth.add(Arrays.asList(14L, 18L, 18L, 100L, 100L, 100L, 101L, 102L, 106L, 107L, 108L, 109L, 109L, 111L));
        groundTruth.add(Arrays.asList(14L, 106L, 109L));
        groundTruth.add(Arrays.asList(15L, 17L, 18L));
        groundTruth.add(Arrays.asList(15L, 17L, 34L));
        groundTruth.add(Arrays.asList(15L, 33L, 34L, 49L, 50L));
        groundTruth.add(Arrays.asList(17L, 18L, 26L));
        groundTruth.add(Arrays.asList(17L, 26L, 35L));
        groundTruth.add(Arrays.asList(17L, 34L, 35L));
        groundTruth.add(Arrays.asList(18L, 25L, 26L, 27L, 35L));
        groundTruth.add(Arrays.asList(19L, 20L, 21L));
        groundTruth.add(Arrays.asList(19L, 21L, 40L, 41L, 52L));
        groundTruth.add(Arrays.asList(19L, 41L, 42L, 53L));
        groundTruth.add(Arrays.asList(20L, 21L, 22L, 23L));
        groundTruth.add(Arrays.asList(21L, 22L, 39L));
        groundTruth.add(Arrays.asList(21L, 39L, 40L));
        groundTruth.add(Arrays.asList(22L, 22L, 23L, 24L, 25L, 25L, 37L, 38L, 39L));
        groundTruth.add(Arrays.asList(23L, 24L, 37L, 38L));
        groundTruth.add(Arrays.asList(25L, 35L, 36L));
        groundTruth.add(Arrays.asList(25L, 36L, 37L));
        groundTruth.add(Arrays.asList(28L, 29L, 29L, 30L, 30L, 47L, 47L, 48L, 48L));
        groundTruth.add(Arrays.asList(28L, 46L, 47L));
        groundTruth.add(Arrays.asList(30L, 31L, 48L));
        groundTruth.add(Arrays.asList(31L, 48L, 49L));
        groundTruth.add(Arrays.asList(32L, 33L, 49L));
        groundTruth.add(Arrays.asList(34L, 35L, 50L));
        groundTruth.add(Arrays.asList(35L, 36L, 50L));
        groundTruth.add(Arrays.asList(36L, 37L, 37L, 38L, 38L, 39L, 50L, 50L, 51L, 51L));
        groundTruth.add(Arrays.asList(39L, 40L, 51L, 52L));
        groundTruth.add(Arrays.asList(41L, 52L, 53L));
        groundTruth.add(Arrays.asList(43L, 43L, 44L, 44L, 46L, 46L, 53L, 53L));
        groundTruth.add(Arrays.asList(44L, 45L, 46L));
        groundTruth.add(Arrays.asList(46L, 47L, 54L));
        groundTruth.add(Arrays.asList(46L, 53L, 54L));
        groundTruth.add(Arrays.asList(47L, 48L, 55L));
        groundTruth.add(Arrays.asList(47L, 54L, 55L));
        groundTruth.add(Arrays.asList(48L, 49L, 55L));
        groundTruth.add(Arrays.asList(49L, 50L, 56L));
        groundTruth.add(Arrays.asList(49L, 55L, 56L));
        groundTruth.add(Arrays.asList(50L, 51L, 56L));
        groundTruth.add(Arrays.asList(51L, 52L, 57L));
        groundTruth.add(Arrays.asList(51L, 56L, 57L));
        groundTruth.add(Arrays.asList(52L, 53L, 57L));
        groundTruth.add(Arrays.asList(53L, 54L, 57L));
        groundTruth.add(Arrays.asList(54L, 54L, 55L, 55L, 56L, 56L, 57L, 57L));

        return groundTruth;
    }
}
