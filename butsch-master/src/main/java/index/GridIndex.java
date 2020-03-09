package index;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.jgrapht.Graph;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GridIndex implements Index {
    private final Graph graph;
    private final int longitudalGranularity;
    private final double longitudeCellSize;
    private final int latitudalGranularity;
    private final double latitudeCellSize;
    private final GridCell[][] cells;

    public GridIndex(final RoadGraph graph, final int longitudalGranularity, final int latitudalGranularity) {
        this.graph = graph;
        this.longitudalGranularity = longitudalGranularity;
        this.longitudeCellSize = 360 / longitudalGranularity;
        this.latitudalGranularity = latitudalGranularity;
        this.latitudeCellSize = 180 / latitudalGranularity;

        this.cells = new GridCell[longitudalGranularity][latitudalGranularity];

        initCells();
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

    private List<GridCell> getSurroundingCells(final Coordinate coordinate, final int layer) {
        return getSurroundingCells(coordinate.getX(), coordinate.getY(), layer);
    }

    private List<GridCell> getSurroundingCells(final double longitude, final double latitude, final int layer) {
        final List<GridCell> cellBlock = new ArrayList<>();

        final int longitudeStartIndex = getLongitudeIndex(longitude) - (layer + 1);
        final int latitudeStartIndex = getLatitudeIndex(latitude) - (layer + 1);;
        final int longitudeEndIndex = getLongitudeIndex(longitude) + (layer + 1);
        final int latitudeEndIndex = getLatitudeIndex(latitude) + (layer + 1);

        if (layer == 0) {
            cellBlock.add(getCell(longitude, latitude));
        }
        for (int x = longitudeStartIndex; x < longitudeEndIndex + 1; x++) {
            cellBlock.add(getCell(x, latitudeStartIndex));
            cellBlock.add(getCell(x, latitudeEndIndex));
        }
        for (int y = latitudeStartIndex + 1; y < latitudeEndIndex; y++) {
            cellBlock.add(getCell(longitudeStartIndex, y));
            cellBlock.add(getCell(longitudeEndIndex, y));
        }

        return cellBlock;
    }

    private GridCell getCell(final int longitudeStartIndex, final int latitudeStartIndex, final int x, final int y) {
        int longitudeIndex = (longitudeStartIndex + x) % (cells.length - 1);
        int latitudeIndex = (latitudeStartIndex + y) % (cells[0].length - 1);

        return getCell(longitudeIndex, latitudeIndex);
    }

    private GridCell getCell(final Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    private GridCell getCell(final double longitude, final double latitude) {
        final int longitudeIndex = getLongitudeIndex(longitude);
        final int latitudeIndex = getLatitudeIndex(latitude);

        return getGridCell(longitudeIndex, latitudeIndex);
    }

    private GridCell getGridCell(final int longitudeIndex, final int latitudeIndex) {
        return cells[longitudeIndex][latitudeIndex];
    }

    private int getLongitudeIndex(final double longitude) {
        return (int) ((longitude + 180) / longitudeCellSize);
    }

    private int getLatitudeIndex(final double latitude) {
        return (int) ((latitude + 90) / latitudeCellSize);
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
        final List<GridCell> intersectingCells = new LinkedList<>();

        final Coordinate startCoordinate = edgeRepresentingLine.getCoordinate(0);
        final Coordinate endCoordinate = edgeRepresentingLine.getCoordinate(1);

        final int startIndexLongitude = getLongitudeIndex(startCoordinate.getX());
        final int startIndexLatitude = getLatitudeIndex(startCoordinate.getY());
        final int endIndexLongitude = getLongitudeIndex(endCoordinate.getX());
        final int endIndexLatitude = getLatitudeIndex(endCoordinate.getY());

        for (int x = startIndexLongitude; x < endIndexLongitude + 1; x++) {
            for (int y = startIndexLatitude; y < endIndexLatitude + 1; y++) {
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

        double cellLowerLeftLongitude = gridCellLongitudeIndex * longitudeCellSize;
        double cellLowerLeftLatitude = gridCellLatitudeIndex * latitudeCellSize;

        double cellUpperLeftLongitude = cellLowerLeftLongitude;
        double cellUpperLeftLatitude = (gridCellLatitudeIndex + 1) * latitudeCellSize;

        double cellLowerRightLongitude = (gridCellLongitudeIndex + 1) * longitudeCellSize;
        double cellLowerRightLatitude = cellLowerLeftLatitude;

        double cellUpperRightLongitude = cellLowerRightLongitude;
        double cellUpperRightLatitude = cellUpperLeftLatitude;

        boundingBoxLineSegments[0] = new LineSegment(cellLowerLeftLongitude, cellLowerLeftLatitude, cellUpperLeftLongitude, cellUpperLeftLatitude);
        boundingBoxLineSegments[1] = new LineSegment(cellUpperLeftLongitude, cellUpperLeftLatitude, cellLowerRightLongitude, cellLowerRightLatitude);
        boundingBoxLineSegments[2] = new LineSegment(cellLowerRightLongitude, cellLowerLeftLatitude, cellUpperRightLongitude, cellUpperRightLatitude);
        boundingBoxLineSegments[3] = new LineSegment(cellUpperRightLongitude, cellUpperRightLatitude, cellLowerLeftLongitude, cellLowerLeftLatitude);

        return boundingBoxLineSegments;
    }

    @Override
    public Node getClosestNode(final double longitude, final double latitude) {
        return getClosestNode(new Coordinate(longitude, latitude));
    }

    private Node getClosestNode(final Coordinate coordinate) {
        int layer = 0;

        Node minNode = null;
        Double minDistance = Double.POSITIVE_INFINITY;
        while (minNode == null) {
            final List<GridCell> cellBlock = getSurroundingCells(coordinate, layer);

            for (final GridCell cell : cellBlock) {
                for (final Node node : cell.nodes) {
                    final double distance = getDistance(coordinate, node);

                    if (isDistanceSmaller(minDistance, distance)) {
                        minDistance = distance;
                        minNode = node;
                    }
                }
            }

            layer++;
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
        int layer = 0;

        Edge minEdge = null;
        Double minDistance = Double.POSITIVE_INFINITY;
        while (minEdge == null) {
            final List<GridCell> cellBlock = getSurroundingCells(coordinate, layer);

            for (final GridCell cell : cellBlock) {
                for (final Edge edge : cell.edges) {
                    final double distance = getDistance(coordinate, edge);
                    if (isDistanceSmaller(minDistance, distance)) {
                        minDistance = distance;
                        minEdge = edge;
                    }
                }
            }

            layer++;
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

    private LineSegment getLineSegment(final Edge edge) {
        final Node baseNode = (Node) graph.getEdgeSource(edge);
        final Node adjNode = (Node) graph.getEdgeTarget(edge);
        return new LineSegment(baseNode.longitude, baseNode.latitude, adjNode.longitude, adjNode.latitude);
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

    private class GridCell {
        final List<Node> nodes = new LinkedList<>();
        final List<Edge> edges = new LinkedList<>();
    }
}
