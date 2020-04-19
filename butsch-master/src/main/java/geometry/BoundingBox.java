package geometry;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BoundingBox extends Polygon {
    public final double minLongitude, maxLongitude, minLatitude, maxLatitude;

    public BoundingBox(final double minLongitude, final double maxLongitude, final double minLatitude, final double maxLatitude) {
        super(new LinearRing(
                new CoordinateArraySequence(
                        new Coordinate[] {
                                new Coordinate(minLongitude, minLatitude),
                                new Coordinate(minLongitude, maxLatitude),
                                new Coordinate(maxLongitude, maxLatitude),
                                new Coordinate(maxLongitude, minLatitude),
                                new Coordinate(minLongitude, minLatitude)}),
                new GeometryFactory()),
              new LinearRing[] {},
              new GeometryFactory());

        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
    }

    public static BoundingBox createFrom(final Polygon polygon) {
        final MinMaxLongLat minMaxLongLat = new MinMaxLongLat();

        for (final Coordinate coordinate : polygon.getCoordinates()) {
            minMaxLongLat.accept(coordinate);
        }

        return new BoundingBox(minMaxLongLat.minLongitude, minMaxLongLat.maxLongitude, minMaxLongLat.minLatitude, minMaxLongLat.maxLatitude);
    }

    public boolean isOverlapping(final Geometry otherGeometry) {
        if (intersects(otherGeometry)) {
            return true;
        } else if (contains(otherGeometry)) {
            return true;
        } else if (otherGeometry.contains(this)) {
            return true;
        } else {
            return false;
        }
    }

    public List<LineSegment> getLineSegmentRepresentation() {
        final Coordinate[] coordinates = getCoordinates();
        final List<LineSegment> lineSegments = new ArrayList<>(Arrays.asList(
                new LineSegment[] {
                        new LineSegment(coordinates[0], coordinates[1]),
                        new LineSegment(coordinates[1], coordinates[2]),
                        new LineSegment(coordinates[2], coordinates[3]),
                        new LineSegment(coordinates[3], coordinates[4])
                }
        ));

        return lineSegments;
    }

    public boolean isContainedBy(final Polygon polygon) {
        final GeometryFactory gf = new GeometryFactory();

        final boolean minMinContained = polygon.contains(gf.createPoint(new Coordinate(minLongitude, minLatitude)));
        final boolean minMaxContained = polygon.contains(gf.createPoint(new Coordinate(minLongitude, maxLatitude)));
        final boolean maxMinContained = polygon.contains(gf.createPoint(new Coordinate(maxLongitude, minLatitude)));
        final boolean maxMaxContained = polygon.contains(gf.createPoint(new Coordinate(maxLongitude, maxLatitude)));

        return minMinContained && minMaxContained && maxMinContained && maxMaxContained;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final BoundingBox that = (BoundingBox) o;
        return Double.compare(that.minLongitude, minLongitude) == 0 && Double.compare(that.maxLongitude,
                                                                                      maxLongitude) == 0 && Double.compare(
                that.minLatitude, minLatitude) == 0 && Double.compare(that.maxLatitude, maxLatitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minLongitude, maxLongitude, minLatitude, maxLatitude);
    }

    private static class MinMaxLongLat implements Consumer<Coordinate> {
        double minLongitude = Double.MAX_VALUE;
        double maxLongitude = (-1) * Double.MAX_VALUE;
        double minLatitude = Double.MAX_VALUE;
        double maxLatitude = (-1) * Double.MAX_VALUE;

        @Override
        public void accept(final Coordinate coordinate) {
            minLongitude = Math.min(minLongitude, coordinate.getX());
            maxLongitude = Math.max(maxLongitude, coordinate.getX());
            minLatitude = Math.min(minLatitude, coordinate.getY());
            maxLatitude = Math.max(maxLatitude, coordinate.getY());
        }
    }
}
