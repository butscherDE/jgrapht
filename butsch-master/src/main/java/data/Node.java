package data;

import org.locationtech.jts.geom.Coordinate;

import java.util.Objects;

public class Node {
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
}
