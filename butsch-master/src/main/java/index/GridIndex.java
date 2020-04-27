package index;

import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.StopWatchVerbose;
import geometry.BoundingBox;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.*;
import java.util.stream.Collectors;

public class GridIndex implements Index {
    private final static double LONGITUDE_RANGE = 360d;
    private final static double LATITUDE_RANGE = 180d;

    private final RoadGraph graph;
    private final BoundingBox indexBounds;
    private final double longitudeCellSize;
    private final double latitudeCellSize;
    private final GridCell[][] cells;


    public GridIndex(final RoadGraph graph, final int longitudeGranularity, final int latitudeGranularity) {
        this.graph = graph;
        this.indexBounds = graph.getBoundingBox();
        final double longitudeRange = (indexBounds.maxLongitude + LONGITUDE_RANGE) - (indexBounds.minLongitude + LONGITUDE_RANGE);
        final double latitudeRange = (indexBounds.maxLatitude + LATITUDE_RANGE) - (indexBounds.minLatitude + LATITUDE_RANGE);
        this.longitudeCellSize = longitudeRange / longitudeGranularity;
        this.latitudeCellSize = latitudeRange / latitudeGranularity;

        this.cells = new GridCell[longitudeGranularity][latitudeGranularity];

        StopWatchVerbose sw = new StopWatchVerbose("Index creation");
        initCells();
        sw.printTimingIfVerbose();
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
        if (longitude < indexBounds.minLongitude) {
            return getOutOfLowerBoundLongitudeIndex();
        } else if (longitude >= indexBounds.maxLongitude) {
            return getOutOfUpperBoundLongitudeIndex();
        } else {
            return getInBoundsLongitudeIndex(longitude);
        }
    }

    private int getOutOfLowerBoundLongitudeIndex() {
        return 0;
    }

    private int getOutOfUpperBoundLongitudeIndex() {
        return cells.length - 1;
    }

    private int getInBoundsLongitudeIndex(final double longitude) {
        double longitudeNonNegative = longitude + (-1) * indexBounds.minLongitude;
        return (int) (longitudeNonNegative / longitudeCellSize);
    }

    private int getLatitudeIndex(final double latitude) {
        if (latitude < indexBounds.minLatitude) {
            return getOutOfLowerBoundLatitudeIndex();
        } else if (latitude >= indexBounds.maxLatitude) {
            return getOutOfUpperBoundLatitudeIndex();
        } else {
            return getInBoundsLatitudeIndex(latitude);
        }
    }

    private int getOutOfLowerBoundLatitudeIndex() {
        return 0;
    }

    private int getOutOfUpperBoundLatitudeIndex() {
        return cells[0].length - 1;
    }

    private int getInBoundsLatitudeIndex(final double latitude) {
        double latitudeNonNegative = latitude + (-1) * indexBounds.minLatitude;
        return (int) (latitudeNonNegative / latitudeCellSize);
    }

    private void addToIntersectingCells(final Edge edge) {
        final List<GridCell> intersectingCells = getIntersectingCells(edge);

        for (final GridCell intersectingCell : intersectingCells) {
            intersectingCell.edges.add(edge);
        }
    }

    private List<GridCell> getIntersectingCells(final Edge edge) {
        return getIntersectingCells(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
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

    private LineSegment[] getBoundingBoxLineSegments(final int gridCellLongitudeIndex, final int gridCellLatitudeIndex) {
        final LineSegment[] boundingBoxLineSegments = new LineSegment[4];

        double cellLowerLeftLongitude = gridCellLongitudeIndex * longitudeCellSize - (-1) * indexBounds.minLongitude;
        double cellLowerLeftLatitude = gridCellLatitudeIndex * latitudeCellSize - (-1) * indexBounds.minLatitude;

        double cellUpperLeftLongitude = cellLowerLeftLongitude;
        double cellUpperLeftLatitude = (gridCellLatitudeIndex + 1) * latitudeCellSize - (-1) * indexBounds.minLatitude;

        double cellLowerRightLongitude = (gridCellLongitudeIndex + 1) * longitudeCellSize - (-1) * indexBounds.minLongitude;
        double cellLowerRightLatitude = cellLowerLeftLatitude;

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
        while (minNode == null) {
            final List<GridCell> cellBlock = hullCreator.getSurroundingCells();

            final List<Node> minNodesPerCell = cellBlock
                    .stream()
                    .map(a -> saveMinNode(a.nodes, Comparator.comparingDouble(b -> getDistance(coordinate, b))))
                    .collect(Collectors.toList());
            minNode = Collections.min(minNodesPerCell, Comparator.comparingDouble(a -> getDistance(coordinate, a)));
        }

        return minNode;
    }

    private Node saveMinNode(final Collection collection, final Comparator<? extends Node> comparator) {
        try {
            return Collections.min(collection, comparator);
        } catch (NoSuchElementException e) {
            return RoadGraph.INVALID_NODE;
        }
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

        Edge minEdge = null;
        while (minEdge == null)
        {
            final List<GridCell> cellBlock = hullCreator.getSurroundingCells();

            final List<Edge> minDistanceEdgesPerCell = cellBlock
                    .stream()
                    .map(a -> saveMinEdge(a.edges, Comparator.comparingDouble(b -> getDistance(coordinate, b))))
                    .collect(Collectors.toList());
            minEdge = Collections.min(minDistanceEdgesPerCell, Comparator.comparingDouble(a -> getDistance(coordinate, a)));
        }

        return minEdge;
    }

    private Edge saveMinEdge(Collection collection, Comparator<? extends Edge> comparator) {
        try {
            return Collections.min(collection, comparator);
        } catch (NoSuchElementException e) {
            return RoadGraph.INVALID_EDGE;
        }
    }

    private double getDistance(final Coordinate coordinate, final Edge edge) {
        final LineSegment edgeRepresentingLine = getLineSegment(edge);

        return edgeRepresentingLine.distance(coordinate);
    }

    private Coordinate getCoordinate(final Node node) {
        return new Coordinate(node.longitude, node.latitude);
    }

    private LineSegment getLineSegment(final Edge edge) {
        final Node baseNode = graph.getEdgeSource(edge);
        final Node adjNode = graph.getEdgeTarget(edge);

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

    private static class GridCell {
        // Initial size = 0 because in various szenarios most cells are empty.
        final List<Node> nodes = new ArrayList<>(0);
        final List<Edge> edges = new ArrayList<>(0);
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
            longitudeStartIndex = Math.max(0, getLongitudeIndex(longitude) - layer);
            latitudeStartIndex = Math.max(0, getLatitudeIndex(latitude) - layer);
            longitudeEndIndex = Math.min(cells.length - 1, getLongitudeIndex(longitude) + layer);
            latitudeEndIndex = Math.min(cells[0].length - 1, getLatitudeIndex(latitude) + layer);
        }

        private void addCenterNode() {
            if (layer == 0) {
                cellBlock.add(getCell(longitude, latitude));
                layer++;
                updateBoundingIndicesBasedOnLayer();
            }
        }

        private void getTopAndBottomRows() {
            for (int x = longitudeStartIndex; x < longitudeEndIndex + 1; x++) {
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
            for (int y = latitudeStartIndex + 1; y < latitudeEndIndex; y++) {
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

    @Override
    public void queryNodes(final BoundingBox limiter, final IndexVisitor visitor) {
        final Set<GridCell> cellsOverlappingLimiter = getNodesInLimiter(limiter);
        final Set<Node> allNodesInCells = cellsOverlappingLimiter.stream().flatMap(a -> a.nodes.stream()).collect(Collectors.toSet());

        if (visitor instanceof GridIndexVisitor) {
            final GridIndexVisitor gridIndexVisitor = (GridIndexVisitor) visitor;
            for (final Node node : allNodesInCells) {
                final BoundingBox boundingBox = getBoundingBoxOfCell(node);

                gridIndexVisitor.accept(node, boundingBox);
            }
        } else {
            for (final Node node : allNodesInCells) {
                visitor.accept(node);
            }
        }
    }

    public BoundingBox getBoundingBoxOfCell(final Node node) {
        final int longitudeIndex = getLongitudeIndex(node.longitude);
        final int latitudeIndex = getLatitudeIndex(node.latitude);

        final double minLongitude = longitudeIndex * longitudeCellSize;
        final double maxLongitude = (longitudeIndex + 1) * longitudeCellSize;
        final double minLatitude = latitudeIndex * longitudeCellSize;
        final double maxLatitude = (latitudeIndex + 1) * longitudeCellSize;

        return new BoundingBox(minLongitude, maxLongitude, minLatitude, maxLatitude);
    }

    public Set<GridCell> getNodesInLimiter(final BoundingBox limiter) {
        final int minLongitudeIndex = getLongitudeIndex(limiter.minLongitude);
        final int maxLongitudeIndex = getLongitudeIndex(limiter.maxLongitude);
        final int minLatitudeIndex = getLatitudeIndex(limiter.minLatitude);
        final int maxLatitudeIndex = getLatitudeIndex(limiter.maxLatitude);

        final Set<GridCell> gridCellsOverlappingLimiter = new LinkedHashSet<>();
        addBorderCellNodes(limiter, minLongitudeIndex, maxLongitudeIndex, minLatitudeIndex, maxLatitudeIndex,
                           gridCellsOverlappingLimiter);
        addInnerCellNodes(minLongitudeIndex, maxLongitudeIndex, minLatitudeIndex, maxLatitudeIndex, gridCellsOverlappingLimiter);
        return gridCellsOverlappingLimiter;
    }

    public void addBorderCellNodes(final BoundingBox limiter, final int minLongitudeIndex, final int maxLongitudeIndex,
                                   final int minLatitudeIndex, final int maxLatitudeIndex,
                                   final Set<GridCell> gridCellsOverlappingLimiter) {
        for (int x = minLongitudeIndex; x <= maxLongitudeIndex; x++) {
            gridCellsOverlappingLimiter.add(cells[x][minLatitudeIndex]);
            gridCellsOverlappingLimiter.add(cells[x][maxLatitudeIndex]);
        }
        for (int y = minLatitudeIndex; y <= maxLatitudeIndex; y++) {
            gridCellsOverlappingLimiter.add(cells[minLongitudeIndex][y]);
            gridCellsOverlappingLimiter.add(cells[maxLongitudeIndex][y]);
        }
    }

    private Set<Node> getNodesInLimiter(final List<Node> nodesToFilter, final BoundingBox limiter) {
        final Set<Node> nodesInLimiter = new LinkedHashSet<>();

        for (final Node node : nodesToFilter) {
            if (limiter.contains(node.getPoint())) {
                nodesInLimiter.add(node);
            }
        }

        return nodesInLimiter;
    }

    public void addInnerCellNodes(final int minLongitudeIndex, final int maxLongitudeIndex, final int minLatitudeIndex,
                                  final int maxLatitudeIndex, final Set<GridCell> gridCellsOverlappingLimiter) {
        for (int x = minLongitudeIndex + 1; x < maxLongitudeIndex; x++) {
            for (int y = minLatitudeIndex + 1; y < maxLatitudeIndex; y++) {
                gridCellsOverlappingLimiter.add(cells[x][y]);
            }
        }
    }

    public void queryEdges(final BoundingBox limit, final IndexVisitor visitor) {

    }

    public interface GridIndexVisitor<T> extends IndexVisitor<T> {
        void accept(T entity, BoundingBox cell);
    }
}
