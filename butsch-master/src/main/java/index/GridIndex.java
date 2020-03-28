package index;

import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.StopWatchVerbose;
import org.jgrapht.Graph;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GridIndex implements Index {
    private final RoadGraph graph;
    private final double longitudeCellSize;
    private final int longitudePrecision;
    private final double latitudeCellSize;
    private final int latitudePrecision;
    private final GridCell[][] cells;

    public GridIndex(final RoadGraph graph, final int longitudeGranularity, final int latitudeGranularity) {
        this.graph = graph;
        this.longitudeCellSize = 360d / longitudeGranularity;
        this.longitudePrecision = (int) Math.pow(10, precision(longitudeGranularity));
        this.latitudeCellSize = 180d / latitudeGranularity;
        this.latitudePrecision = (int) Math.pow(10, precision(latitudeGranularity));

        System.out.println(longitudeGranularity);
        System.out.println(longitudeCellSize);
        System.out.println(latitudeGranularity);
        System.out.println(latitudeCellSize);

        this.cells = new GridCell[longitudeGranularity][latitudeGranularity];

        StopWatchVerbose sw = new StopWatchVerbose("Index creation");
        initCells();
        sw.printTimingIfVerbose();

//        int c = 0;
//        int min = Integer.MAX_VALUE;
//        int max = Integer.MIN_VALUE;
//        final Set<Edge> allEdgesUnique = new LinkedHashSet<>();
//        for (final GridCell[] cell : cells) {
//            for (final GridCell gridCell : cell) {
//                final int numEdges = gridCell.edges.size();
//                c += numEdges;
//                min = Math.min(min, numEdges);
//                max = Math.max(max, numEdges);
//                for (final Edge edge : gridCell.edges) {
//                    allEdgesUnique.add(edge);
//                }
//            }
//        }
//        System.out.println("Num unique edges " + allEdgesUnique.size());
//        System.out.println("Num edges in index: " + c);
//        System.out.println("Smallest cell: " + min);
//        System.out.println("Largest cell: " + max);
    }

    private void initCells() {
        instantiateCellObjects();
        addNodesToIntersectingCells();
        addEdgesToIntersectingCells();
    }

    private void instantiateCellObjects() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new GridCell();
            }
        }
    }

    private void addNodesToIntersectingCells() {
        for (final Object o : graph.vertexSet()) {
            final Node node = (Node) o;

            final double longitude = node.longitude;
            final double latitude = node.latitude;

            getCell(longitude, latitude).nodes.add(node);
        }
    }

    private void addEdgesToIntersectingCells() {
        for (final Object o : graph.edgeSet()) {
            final Edge edge = (Edge) o;

            addToIntersectingCells(edge);
        }
    }

    private GridCell getCell(final Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    private GridCell getCell(final double longitude, final double latitude) {
        final int longitudeIndex = getLongitudeIndex(longitude);
        final int latitudeIndex = getLatitudeIndex(latitude);

        return getCellByIndex(longitudeIndex, latitudeIndex);
    }

    private GridCell getCellByIndex(final int longitudeIndex, final int latitudeIndex) {
        return cells[longitudeIndex][latitudeIndex];
    }

    private int getLongitudeIndex(final double longitude) {
        double longitudeNonNegative = longitude + 180;
        return (int) (longitudeNonNegative / longitudeCellSize);
    }

    private int getLatitudeIndex(final double latitude) {
        double latitudeNonNegative = latitude + 90;
        return (int) (latitudeNonNegative / latitudeCellSize);
    }

    private void addToIntersectingCells(final Edge edge) {
        final List<GridCell> intersectingCells = getIntersectingCells(edge);

        for (final GridCell intersectingCell : intersectingCells) {
            intersectingCell.edges.add(edge);
        }
    }

    private List<GridCell> getIntersectingCells(final Edge edge) {
        return getIntersectingCells((Node) graph.getEdgeSource(edge), (Node) graph.getEdgeTarget(edge));
    }

    private List<GridCell> getIntersectingCells(final Node start, final Node end) {
        return getIntersectingCells(start.longitude, start.latitude, end.longitude, end.latitude);
    }

    private List<GridCell> getIntersectingCells(final double startLongitude, final double startLatitude,
                                                final double endLongitude, final double endLatitude) {
        return getIntersectingCells(new LineSegment(startLongitude, startLatitude, endLongitude, endLatitude));
    }

    private List<GridCell> getIntersectingCells(final LineSegment edgeRepresentingLine) {
        final Coordinate startCoordinate = edgeRepresentingLine.getCoordinate(0);
        final Coordinate endCoordinate = edgeRepresentingLine.getCoordinate(1);

        if (isEdgeCompletelyContainedInOneCell(startCoordinate, endCoordinate)) {
            return Collections.singletonList(getCell(startCoordinate));
        } else {
            return getAllIntersectingCells(edgeRepresentingLine);
        }
    }

    private boolean isEdgeCompletelyContainedInOneCell(final Coordinate startCoordinate, final Coordinate endCoordinate) {
        final int startCoordinateLongitudeIndex = getLongitudeIndex(startCoordinate.getX());
        final int endCoordinateLongitudeIndex = getLongitudeIndex(endCoordinate.getX());
        final int startCoordinateLatitudeIndex = getLatitudeIndex(startCoordinate.getY());
        final int endCoordinateLatitudeIndex = getLatitudeIndex(endCoordinate.getY());

        final boolean longitudeIndexEqual = startCoordinateLongitudeIndex == endCoordinateLongitudeIndex;
        final boolean latitudeIndexEqual = startCoordinateLatitudeIndex == endCoordinateLatitudeIndex;

        return longitudeIndexEqual && latitudeIndexEqual;
    }

    private List<GridCell> getAllIntersectingCells(final LineSegment edgeRepresentingLine) {

        final Coordinate startCoordinate = edgeRepresentingLine.getCoordinate(0);
        final Coordinate endCoordinate = edgeRepresentingLine.getCoordinate(1);

        final int startIndexLongitude = getLongitudeIndex(startCoordinate.getX());
        final int startIndexLatitude = getLatitudeIndex(startCoordinate.getY());
        final int endIndexLongitude = getLongitudeIndex(endCoordinate.getX());
        final int endIndexLatitude = getLatitudeIndex(endCoordinate.getY());

        final int minIndexLongitude = Math.min(startIndexLongitude, endIndexLongitude);
        final int maxIndexLongitude = Math.max(startIndexLongitude, endIndexLongitude);
        final int minIndexLatitude = Math.min(startIndexLatitude, endIndexLatitude);
        final int maxIndexLatitude = Math.max(startIndexLatitude, endIndexLatitude);


        final List<GridCell> intersectingCells = new LinkedList<>();

        for (int x = minIndexLongitude; x < maxIndexLongitude + 1; x++) {
            for (int y = minIndexLatitude; y < maxIndexLatitude + 1; y++) {
                final LineSegment[] boundingBoxLineSegments = getBoundingBoxLineSegments(x, y);
                for (final LineSegment boundingBoxLineSegment : boundingBoxLineSegments) {
                    if (isIntersecting(edgeRepresentingLine, boundingBoxLineSegment)) {
                        intersectingCells.add(cells[x][y]);
                        break;
                    }
                }
            }
        }

        return intersectingCells;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private LineSegment[] getBoundingBoxLineSegments(final int gridCellLongitudeIndex, final int gridCellLatitudeIndex) {
        final LineSegment[] boundingBoxLineSegments = new LineSegment[4];

        double cellLowerLeftLongitude = roundLongitude(gridCellLongitudeIndex * longitudeCellSize - 180);
        double cellLowerLeftLatitude = roundLatitude(gridCellLatitudeIndex * latitudeCellSize - 90);

        double cellUpperLeftLongitude = cellLowerLeftLongitude;
        double cellUpperLeftLatitude = roundLatitude((gridCellLatitudeIndex + 1) * latitudeCellSize - 90);

        double cellLowerRightLongitude = roundLongitude((gridCellLongitudeIndex + 1) * longitudeCellSize -180);
        @SuppressWarnings("UnnecessaryLocalVariable") double cellLowerRightLatitude = cellLowerLeftLatitude;

        double cellUpperRightLongitude = cellLowerRightLongitude;
        double cellUpperRightLatitude = cellUpperLeftLatitude;

        boundingBoxLineSegments[0] = new LineSegment(cellLowerLeftLongitude, cellLowerLeftLatitude, cellUpperLeftLongitude, cellUpperLeftLatitude);
        boundingBoxLineSegments[1] = new LineSegment(cellUpperLeftLongitude, cellUpperLeftLatitude, cellUpperRightLongitude, cellUpperRightLatitude);
        boundingBoxLineSegments[2] = new LineSegment(cellUpperRightLongitude, cellUpperRightLatitude, cellLowerRightLongitude, cellLowerRightLatitude);
        boundingBoxLineSegments[3] = new LineSegment(cellLowerRightLongitude, cellLowerRightLatitude, cellLowerLeftLongitude, cellLowerLeftLatitude);

        return boundingBoxLineSegments;
    }

    @Override
    public Node getClosestNode(final double longitude, final double latitude) {
        return getClosestNode(new Coordinate(longitude, latitude));
    }

    private Node getClosestNode(final Coordinate coordinate) {
        final CellHullCreator hullCreator = new CellHullCreator(coordinate);

        Node minNode = null;
        double minDistance = Double.POSITIVE_INFINITY;
        while (minNode == null) {
            final List<GridCell> cellBlock = hullCreator.getSurroundingCells();

            for (final GridCell cell : cellBlock) {
                for (final Node node : cell.nodes) {
                    final double distance = getDistance(coordinate, node);

                    if (isDistanceSmaller(minDistance, distance)) {
                        minDistance = distance;
                        minNode = node;
                    }
                }
            }
        }

        return minNode;
    }

    private double getDistance(final Coordinate coordinate, final Node node) {
        final Coordinate nodeCoordinate = new Coordinate(node.longitude, node.latitude);

        return coordinate.distance(nodeCoordinate);
    }

    @Override
    public Edge getClosestEdge(final double longitude, final double latitude) {
        return getClosestEdge(new Coordinate(longitude, latitude));
    }

    private Edge getClosestEdge(final Coordinate coordinate) {
        final CellHullCreator hullCreator = new CellHullCreator(coordinate);

        double minDistance = Double.POSITIVE_INFINITY;
        Edge minEdge = null;
        while (minEdge == null) //noinspection DuplicatedCode
        {
            final List<GridCell> cellBlock = hullCreator.getSurroundingCells();

            for (final GridCell gridCell : cellBlock) {
                for (final Edge edge : gridCell.edges) {
                    final double distance = getDistance(coordinate, edge);

                    if (distance < minDistance) {
                        minDistance = distance;
                        minEdge = edge;
                    }
                }
            }
        }

        // reassure closest found
        for (int i = 0; i < 100; i++) //noinspection DuplicatedCode
        {
            final List<GridCell> cellBlock = hullCreator.getSurroundingCells();

            for (final GridCell gridCell : cellBlock) {
                for (final Edge edge : gridCell.edges) {
                    final double distance = getDistance(coordinate, edge);

                    if (distance < minDistance) {
                        minDistance = distance;
                        minEdge = edge;
                    }
                }
            }
        }

        return minEdge;
    }

    private double getDistance(final Coordinate coordinate, final Edge edge) {
        final LineSegment edgeRepresentingLine = getLineSegment(edge);

        return edgeRepresentingLine.distance(coordinate);
    }

    private boolean isDistanceSmaller(final Double minDistance, final double distance) {
        return distance < minDistance;
    }

    private Coordinate getCoordinate(final Node node) {
        return new Coordinate(node.longitude, node.latitude);
    }

    private LineSegment getLineSegment(final Edge edge) {
        final Node baseNode = (Node) graph.getEdgeSource(edge);
        final Node adjNode = (Node) graph.getEdgeTarget(edge);

        final Coordinate baseCoordinate = getCoordinate(baseNode);
        final Coordinate adjCoordinate = getCoordinate(adjNode);

        return new LineSegment(baseCoordinate, adjCoordinate);
    }

    @Override
    public List<Edge> getIntersectingEdges(final LineSegment lineSegment) {
        final List<Edge> intersectingEdges = new LinkedList<>();
        final List<GridCell> intersectingCells = getIntersectingCells(lineSegment);

        for (final GridCell intersectingCell : intersectingCells) {
            for (final Edge edge : intersectingCell.edges) {
                addEdgeIfIntersecting(lineSegment, intersectingEdges, edge);
            }
        }

        return intersectingEdges;
    }

    private void addEdgeIfIntersecting(final LineSegment lineSegment, final List<Edge> intersectingEdges,
                                       final Edge edge) {
        if (isIntersecting(lineSegment, edge)) {
            intersectingEdges.add(edge);
        }
    }

    private boolean isIntersecting(final LineSegment lineSegment, final Edge edge) {
        final LineSegment edgeRepresentingLineSegment = getLineSegment(edge);

        return isIntersecting(lineSegment, edgeRepresentingLineSegment);
    }

    private boolean isIntersecting(final LineSegment lineSegment, final LineSegment lineSegment2) {
        return lineSegment2.intersection(lineSegment) != null;
    }

    private double roundLongitude(final double longitude) {
        final double leftShiftedLongitude = longitude * longitudePrecision;
        final double roundedLongitude = Math.round(leftShiftedLongitude);
        return roundedLongitude / longitudePrecision;
    }

    private double roundLatitude(final double latitude) {
        final double leftShiftedLatitude = latitude * latitudePrecision;
        final double roundedLatitude = Math.round(leftShiftedLatitude);
        return roundedLatitude / latitudePrecision;
    }

    private int precision(final int integer) {
        return (int) (Math.log10(integer) + 1);
    }

    private static class GridCell {
        final List<Node> nodes = new LinkedList<>();
        final List<Edge> edges = new LinkedList<>();
    }

    private class CellHullCreator {
        private int layer = 0;
        private final double longitude;
        private final double latitude;
        private int longitudeStartIndex;
        private int latitudeStartIndex;
        private int longitudeEndIndex;
        private int latitudeEndIndex;
        private List<GridCell> cellBlock;

        public CellHullCreator(final double longitude, final double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public CellHullCreator(final Coordinate coordinate) {
            this(coordinate.getX(), coordinate.getY());
        }

        private List<GridCell> getSurroundingCells() {
            cellBlock = new ArrayList<>();

            updateBoundingIndicesBasedOnLayer();

            addCenterNode();
            getTopAndBottomRows();
            getSideColumns();

            layer++;

            return cellBlock;
        }

        private void updateBoundingIndicesBasedOnLayer() {
            longitudeStartIndex = getLongitudeIndex(longitude) - (layer);
            latitudeStartIndex = getLatitudeIndex(latitude) - (layer);
            longitudeEndIndex = getLongitudeIndex(longitude) + (layer);
            latitudeEndIndex = getLatitudeIndex(latitude) + (layer);
        }

        private void addCenterNode() {
            if (layer == 0) {
                cellBlock.add(getCell(longitude, latitude));
                layer++;
                updateBoundingIndicesBasedOnLayer();
            }
        }

        private void getTopAndBottomRows() {
            for (int x = longitudeStartIndex; x < longitudeEndIndex + 1; x = (x + 1) % cells.length) {
                addTopRow(x);
                addBottomRow(x);
            }
        }

        private void addTopRow(final int x) {
            cellBlock.add(getCellByIndex(x, latitudeStartIndex));
        }

        private void addBottomRow(final int x) {
            cellBlock.add(getCellByIndex(x, latitudeEndIndex));
        }

        private void getSideColumns() {
            for (int y = latitudeStartIndex + 1; y < latitudeEndIndex; y = (y + 1) % cells[0].length) {
                addLeftColumn(y);
                addRightColumn(y);
            }
        }

        private void addLeftColumn(final int y) {
            cellBlock.add(getCellByIndex(longitudeStartIndex, y));
        }

        private void addRightColumn(final int y) {
            cellBlock.add(getCellByIndex(longitudeEndIndex, y));
        }
    }
}
