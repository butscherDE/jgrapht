package routing.regionAware;

import data.*;
import index.Index;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import util.PolygonRoutingTestGraph;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RegionAlongTest extends AbstractRegionTest{
    @Test
    public void quickStartingTest() {
        // Just to let something run
        final long[] expectedNodeIds = new long[] {19L, 0L, 1L, 28L, 29L, 3L, 4L};
        final Path pathForCoordinates = getPathForCoordinates(0, 25, 46, 25);
        final long[] actualNodeIds = getNodeIdsFrom(pathForCoordinates);

        System.out.println(Arrays.toString(actualNodeIds));

        assertArrayEquals(expectedNodeIds, actualNodeIds);
    }

    private Path getPathForCoordinates(final int startLongitude, final int startLatitude, final int endLongitude,
                                       final int endLatitude) {
        final RoadGraph graph = GRAPH_MOCKER.graph;
        final Index index = GRAPH_MOCKER.gridIndex;
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
//    @Test
//    public void easyRoutingTest() {
//        final int startNode = 19;
//        final double startLatitude = graphMocker.nodeAccess.getLatitude(startNode);
//        final double startLongitude = graphMocker.nodeAccess.getLongitude(startNode);
//        final GHPoint startGhPoint = new GHPoint(startLatitude, startLongitude);
//        final int endNode = 4;
//        final double endLatitude = graphMocker.nodeAccess.getLatitude(endNode);
//        final double endLongitude = graphMocker.nodeAccess.getLongitude(endNode);
//        final GHPoint endGhPoint = new GHPoint(endLatitude, endLongitude);
//
//        final GHRequest request = buildRequest(startGhPoint, endGhPoint);
//        final GHResponse response = new GHResponse();
//        final RoutingTemplate routingTemplate = new PolygonAroundRoutingTemplate(request, response, this.graphMocker.locationIndex, this.graphMocker.encodingManager);
//        final RoutingAlgorithmFactory algorithmFactory = new RoutingAlgorithmFactorySimple();
//        final AlgorithmOptions algorithmOptions = graphMocker.algorithmOptions;
//        final QueryGraph queryGraph = createQueryGraph(request, routingTemplate);
//
//        List<Path> paths = routingTemplate.calcPaths(queryGraph, algorithmFactory, algorithmOptions);
//
//        printPath(paths);
//
//        System.out.println(paths.get(0).getDistance());
//        showAllEdgesWithIDs(routingTemplate);
//    }
//
//    private void showAllEdgesWithIDs(final RoutingTemplate template) {
//        AllEdgesIterator aei = this.graphMocker.graph.getAllEdges();
//        final PolygonRoutingTemplate polygonTemplate = (PolygonRoutingTemplate) template;
//
//        while (aei.next()) {
//            if (polygonTemplate.pathSkeletonEdgeFilter.accept(aei)) {
//                System.out.println(aei.toString());
//            } else {
//                System.out.print("\u001B[31m");
//                System.out.print(aei.toString());
//                System.out.println("\u001B[0m");
//            }
//        }
//    }
//
//    private GHRequest buildRequest(GHPoint... startViaEndPoints) {
//        List<GHPoint> startViaEndPointList = convertPointsToListFormat(startViaEndPoints);
//        String vehicleStr = "car";
//        String weighting = "fastest";
//        String algoStr = "";
//        String localeStr = "de-DE";
//        boolean calcPoints = true;
//        boolean instructions = true;
//        double minPathPrecision = 1.0;
//
//        GHRequest request = new GHRequest(startViaEndPointList);
//        request.setVehicle(vehicleStr).
//                setWeighting(weighting).
//                       setAlgorithm(algoStr).
//                       setLocale(localeStr).
//                       setPointHints(new ArrayList<String>()).
//                       setSnapPreventions(new ArrayList<String>()).
//                       setPathDetails(new ArrayList<String>()).
//                       setPolygon(this.graphMocker.polygon).
//                       getHints().
//                       put(CALC_POINTS, calcPoints).
//                       put(INSTRUCTIONS, instructions).
//                       put(WAY_POINT_MAX_DISTANCE, minPathPrecision);
//        return request;
//    }
//
//    private static List<GHPoint> convertPointsToListFormat(GHPoint[] startViaEndPoints) {
//        List<GHPoint> startViaEndPointList = new ArrayList<>(Arrays.asList(startViaEndPoints));
//
//        return startViaEndPointList;
//    }
//
//    private QueryGraph createQueryGraph(GHRequest request, RoutingTemplate routingTemplate) {
//        final QueryGraph queryGraph = new QueryGraph(this.graphMocker.graph);
//        List<QueryResult> results = routingTemplate.lookup(request.getPoints(), this.graphMocker.flagEncoder);
//        queryGraph.lookup(results);
//        return queryGraph;
//    }
//
//    private void printPath(List<Path> paths) {
//        System.out.println(paths.get(0).getNodesInPathOrder());
//        System.out.println(paths.toString());
//    }
}
