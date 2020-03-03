package data;

import java.util.Objects;

public class Node {
    public final int id;

    public final double longitude;
    public final double latitude;
    public final double elevation;

    public Node(int id, double longitude, double latitude, double elevation) {
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
        boolean longEqual = longitude == otherNode.longitude;
        boolean latiEqual = latitude == otherNode.latitude;
        boolean elevEqual = elevation == otherNode.elevation;

        return longEqual && latiEqual && elevEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + ", " + longitude + ", " + latitude + ", " + elevation;
    }
}
