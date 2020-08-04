package storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.*;
import util.PolygonRoutingTestGraph;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class GeoJsonExporter {
    private final static GeometryFactory gf = new GeometryFactory();
    private final String path;
    private final List<Point> points = new LinkedList<>();
    private final List<LineString> lineStrings = new LinkedList<>();
    private final List<Polygon> polygons = new LinkedList<>();

    public GeoJsonExporter(final String path) {
        this.path = path;
    }

    public void addPoint(final Point point) {
        points.add(point);
    }

    public void addLineString(final LineString lineString) {
        lineStrings.add(lineString);
    }

    public void addPointSequence(final List<Point> pointSequence) {
        final List<Coordinate> coordinates = pointSequence.stream().map(p -> p.getCoordinate()).collect(Collectors.toList());
        addCoordinateSequence(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

    public void addCoordinateSequence(final Coordinate[] coordinates) {
        final LineString lineString = gf.createLineString(coordinates);
        addLineString(lineString);
    }

    public void addPolygon(final Polygon polygon) {
        polygons.add(polygon);
    }

    public void writeJson() {
        final JSONObject featureCollection = prepareGeoJsonString();


        if (path != null) {
            writeToFile(featureCollection);
        } else {
            writeToConsole(featureCollection);
        }
    }

    private void writeToFile(JSONObject featureCollection) {
        try {
            final FileWriter fileWriter = new FileWriter(path);
            featureCollection.writeJSONString(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToConsole(JSONObject featureCollection) {
        System.err.println("Not printing to file only to console");
        System.out.println(featureCollection.toJSONString());
    }

    private JSONObject prepareGeoJsonString() {
        final List<JSONObject> jsonPointFeatures = getJsonPoints();
        final List<JSONObject> jsonLineStringFeatures = getJsonLineStrings();
        final List<JSONObject> jsonPolygonFeatures = getJsonPolygons();

        final JSONArray features = new JSONArray();
        jsonPointFeatures.forEach(p -> features.add(p));
        jsonLineStringFeatures.forEach(ls -> features.add(ls));
        jsonPolygonFeatures.forEach(p -> features.add(p));

        final JSONObject featureCollection = new JSONObject();
        featureCollection.put("features", features);
        featureCollection.put("type", "FeatureCollection");
        return featureCollection;
    }

    private List<JSONObject> getJsonPoints() {
        final List<JSONObject> pointFeatures = points.stream().map(p -> {
            final JSONArray coordinates = new JSONArray();
            coordinates.add(p.getX());
            coordinates.add(p.getY());

            return coordinates;
        }).map(cArr -> {
            final JSONObject geometry = new JSONObject();
            geometry.put("coordinates", cArr);
            geometry.put("type", "Point");
            return geometry;
        }).map(geometry -> {
            final JSONObject feature = new JSONObject();
            feature.put("geometry", geometry);
            feature.put("type", "Feature");
            return feature;
        }).collect(Collectors.toList());

        return pointFeatures;
    }

    private List<JSONObject> getJsonLineStrings() {
        final List<JSONObject> lineStringFeatures = lineStrings.stream().map(ls -> {
            final JSONArray coordinates = new JSONArray();
            for (final Coordinate coordinate : ls.getCoordinates()) {
                final JSONArray coordinateJson = new JSONArray();
                coordinateJson.add(coordinate.x);
                coordinateJson.add(coordinate.y);

                coordinates.add(coordinateJson);
            }

            return coordinates;
        }).map(coordinates -> {
            final JSONObject geometry = new JSONObject();
            geometry.put("coordinates", coordinates);
            geometry.put("type", "LineString");

            return geometry;
        }).map(geometry -> {
            final JSONObject feature = new JSONObject();
            feature.put("geometry", geometry);
            feature.put("type", "Feature");

            return feature;
        }).collect(Collectors.toList());

        return lineStringFeatures;
    }

    private List<JSONObject> getJsonPolygons() {
        final List<JSONObject> polygonFeatures = polygons.stream().map(p -> p.getCoordinates()).map(coordinates -> {
            final JSONArray polygon1 = new JSONArray();
            for (final Coordinate coordinate : coordinates) {
                final JSONArray coordinateArray = new JSONArray();
                coordinateArray.add(coordinate.x);
                coordinateArray.add(coordinate.y);

                polygon1.add(coordinateArray);
            }

            final JSONArray polygonsCollection = new JSONArray();
            polygonsCollection.add(polygon1);

            final JSONObject geometry = new JSONObject();
            geometry.put("coordinates", polygonsCollection);
            geometry.put("type", "Polygon");

            return geometry;
        }).map(geometry -> {
            final JSONObject feature = new JSONObject();
            feature.put("geometry", geometry);
            feature.put("type", "Feature");

            return feature;
        }).collect(Collectors.toList());

        return polygonFeatures;
    }
}
