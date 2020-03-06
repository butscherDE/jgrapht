package storage;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CircularPolygonImporter implements PolygonImporter{
    private final BufferedReader fileReader;
    private GeometryFactory geometryFactory = new GeometryFactory();

    public CircularPolygonImporter(final String path) throws IOException {
        fileReader = new BufferedReader(new FileReader(path));
    }

    @Override
    public List<Polygon> importPolygons() throws IOException {
        final List<Polygon> polygons = new LinkedList<>();

        skipHaeder();
        final int numPolygons = readNumPolygons();
        for (int i = 0; i < numPolygons; i++) {
            polygons.add(readPolygon());
        }

        return polygons;
    }

    private void skipHaeder() throws IOException {
        fileReader.readLine();
        fileReader.readLine();
        fileReader.readLine();
    }

    private int readNumPolygons() throws IOException {
        return Integer.valueOf(fileReader.readLine());
    }

    private Polygon readPolygon() throws IOException {
        String line = fileReader.readLine();

        final Coordinate[] coordinates = extractPolygonCoordinates(line);

        return geometryFactory.createPolygon(coordinates);
    }

    private Coordinate[] extractPolygonCoordinates(final String line) {
        final int numPolygonPoints = getNumPolygonPoints(line);
        String[] strCoordinates = getCoordinateSubstrings(line);
        return fillCoordinates(strCoordinates, numPolygonPoints);
    }

    private int getNumPolygonPoints(final String line) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(line);
        matcher.find();
        return Integer.valueOf(line.substring(matcher.start(), matcher.end()));
    }

    private String[] getCoordinateSubstrings(String line) {
        int startOfPolygonChain = line.indexOf('{');
        line = line.substring(startOfPolygonChain + 2, line.length() - 2);
        return line.split("\\],\\[");
    }

    private Coordinate[] fillCoordinates(final String[] strCoordinates, final int numPolygonPoints) {
        final Coordinate[] coordinates = new Coordinate[numPolygonPoints];
        for (int i = 0; i < strCoordinates.length; i++) {
            final String strCoordinate = strCoordinates[i];
            String[] components = strCoordinate.split(",");

            final double x = Double.valueOf(components[0]);
            final double y = Double.valueOf(components[1]);

            coordinates[i] = new Coordinate(x, y);
        }
        return coordinates;
    }
}
