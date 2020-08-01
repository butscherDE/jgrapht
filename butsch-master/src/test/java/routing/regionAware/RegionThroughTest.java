package routing.regionAware;

import data.*;
import evalutation.Config;
import index.GridIndex;
import org.junit.jupiter.api.Test;
import util.PolygonRoutingTestGraph;
import visualizations.GeometryVisualizer;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SuppressWarnings("SameParameterValue")
public class RegionThroughTest extends AbstractRegionTest {
    @Test
    public void quickStartingTest() {
        // Just to let something run
        final long[] expectedNodeIds = new long[] {0L, 7L, 44L, 46L, 47L, 48L, 31L, 9L, 4L, 5L, 6L};
        final Path pathForCoordinates = getPathForCoordinates(0, 25, 46, 25);
        final long[] actualNodeIds = getNodeIdsFrom(pathForCoordinates);

        visualize(pathForCoordinates);

        System.out.println(Arrays.toString(actualNodeIds));

        assertArrayEquals(expectedNodeIds, actualNodeIds);
    }

    public void visualize(final Path pathForCoordinates) {
        final GeometryVisualizer.GeometryDrawCollection col = new GeometryVisualizer.GeometryDrawCollection();
        final RoadGraph graph = PolygonRoutingTestGraph.DEFAULT_INSTANCE.graph;
        col.addGraph(Color.BLACK, graph);
        pathForCoordinates.getEdgeList().forEach(e -> col.addEdge(Color.RED, e, graph));
        col.addPolygon(Color.BLUE, PolygonRoutingTestGraph.DEFAULT_INSTANCE.polygon);
        final GeometryVisualizer geometryVisualizer = new GeometryVisualizer(col);
        geometryVisualizer.visualizeGraph(1000);
        geometryVisualizer.save(Config.PBF_FILES + "standardExample.jpg");
    }

    private Path getPathForCoordinates(final int startLongitude, final int startLatitude, final int endLongitude,
                                      final int endLatitude) {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final GridIndex index = GRAPH_MOCKER.gridIndex;
        final RoadCH ch = GRAPH_MOCKER.ch;
        final RegionOfInterest roi = GRAPH_MOCKER.roi;

        final Node startNode = index.getClosestNode(startLongitude, startLatitude);
        final Node endNode = index.getClosestNode(endLongitude, endLatitude);

        final RegionThrough regionThrough = new RegionThrough(graph, ch, index, roi);
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
        return new RegionThrough(globalGraph, globalCh, index, roi);
    }
}
