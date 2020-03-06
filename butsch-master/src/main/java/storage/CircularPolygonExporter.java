package storage;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class CircularPolygonExporter implements PolygonExporter {
    private final FileWriter fileWriter;

    public CircularPolygonExporter(final String path) throws IOException {
        fileWriter = new FileWriter(path);
    }

    @Override
    public void export(final Collection<Polygon> polygons) throws IOException {
        writeHeader(polygons.size());

        for (final Polygon polygon : polygons) {
            writePolygon(polygon);
        }

        fileWriter.close();
    }
    private void writeHeader(final int numPolygons) throws IOException {
        fileWriter.write("Consecutive list of polygons with cooresponding coordinates.\n");
        fileWriter.write("Start and end point are always equal\n");
        fileWriter.write("Number represents number of points inclusive start/endpoint duplicate\n");
        fileWriter.write(numPolygons);
    }

    private void writePolygon(final Polygon polygon) throws IOException {
        final Coordinate[] coordinates = polygon.getCoordinates();

        writePolygonHeader(polygon.getNumPoints());
        writeAllButTheLastPoint(coordinates);
        writeLastPoint(coordinates);

        writePolygonEnd();
    }

    // Number of points inclusive end points
    private void writePolygonHeader(final int numPoints) throws IOException {
        fileWriter.write("polygon(" + numPoints + "){");
    }

    private void writeAllButTheLastPoint(final Coordinate[] coordinates) throws IOException {
        for (int i = 0; i < coordinates.length - 1; i++) {
            final Coordinate coordinate = coordinates[i];
            writePoint(coordinate.getX(), coordinate.getY());
            writePointSeparator();
        }
    }

    private void writeLastPoint(final Coordinate[] coordinates) throws IOException {
        final Coordinate lastCoordinate = coordinates[coordinates.length - 1];
        writePoint(lastCoordinate.getX(), lastCoordinate.getY());
    }

    private void writePoint(final double x, final double y) throws IOException {
        fileWriter.write("[" + x + "," + y + "]");
    }

    private void writePointSeparator() throws IOException {
        fileWriter.write(",");
    }

    private void writePolygonEnd() throws IOException {
        fileWriter.write("}\n");
    }


}
