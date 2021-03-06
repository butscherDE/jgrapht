package routing.regionAware;

import data.*;
import index.GridIndex;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RegionAlongTest extends AbstractRegionTest{
    @Test
    public void quickStartingTest() {
        // Just to let something run
        final long[] expectedNodeIds = new long[] {0L, 7L, 44L, 46L, 47L, 48L, 31L, 9L, 4L, 5L, 6L};
        final Path pathForCoordinates = getPathForCoordinates(0, 25, 46, 25);
        final long[] actualNodeIds = getNodeIdsFrom(pathForCoordinates);

        assertArrayEquals(expectedNodeIds, actualNodeIds);
    }

    private Path getPathForCoordinates(final int startLongitude, final int startLatitude, final int endLongitude,
                                       final int endLatitude) {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final GridIndex index = GRAPH_MOCKER.gridIndex;
        final RoadCH ch = GRAPH_MOCKER.ch;
        final RegionOfInterest roi = GRAPH_MOCKER.roi;

        final Node startNode = index.getClosestNode(startLongitude, startLatitude);
        final Node endNode = index.getClosestNode(endLongitude, endLatitude);

        final RegionAlong regionThrough = new RegionAlong(graph, ch, index, roi);
        return regionThrough.findPath(startNode, endNode);
    }

    private long[] getNodeIdsFrom(final Path path) {
        final long[] nodeIds = new long[path.getVertexList().size()];

        final Iterator<Node> pathVertexIterator = path.getVertexList().iterator();
        for (int i = 0; i < path.getVertexList().size(); i++) {
            final Node node = pathVertexIterator.next();

            nodeIds[i] = node.id;
        }

        return nodeIds;
    }

    @Override
    AbstractRegion getInstance(RoadGraph globalGraph, RoadCH globalCh, GridIndex index, RegionOfInterest roi) {
        return new RegionAlong(globalGraph, globalCh, index, roi);
    }
}
