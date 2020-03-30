package data;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Objects;

public class Node implements Comparable<Node> {
    private static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    public final long id;

    public final double longitude;
    public final double latitude;
    public final double elevation;

    public Node(long id, double longitude, double latitude, double elevation) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Node) {
            final Node oAsNode = (Node) o;
            return id == oAsNode.id;
        } else {
            return false;
        }
    }

    public boolean equalPosition(final Node otherNode) {
        boolean longitudeEqual = longitude == otherNode.longitude;
        boolean latitudeEqual = latitude == otherNode.latitude;
        boolean elevationEqual = elevation == otherNode.elevation;

        return longitudeEqual && latitudeEqual && elevationEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + ", " + longitude + ", " + latitude + ", " + elevation;
    }

    public double euclideanDistance(final Node other) {
        final Coordinate thisCoordinate = new Coordinate(longitude, latitude, elevation);
        final Coordinate otherCoordinate = new Coordinate(other.longitude, other.latitude, other.elevation);

        return thisCoordinate.distance(otherCoordinate);
    }

    // TODO think about converting Node to a subclass of Coordinate
    public Geometry getPoint() {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude, elevation));
    }

    @Override
    public int compareTo(final Node o) {
        final int longitudeCompare = Double.compare(longitude, o.longitude);
        final int latitudeCompare = Double.compare(latitude, o.latitude);
        final int elevationCompare = Double.compare(elevation, o.elevation);

        if (longitudeCompare != 0) {
            return longitudeCompare;
        } else if (latitudeCompare != 0) {
            return latitudeCompare;
        } else {
            return elevationCompare;
        }
    }
}
