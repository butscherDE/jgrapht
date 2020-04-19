package geometry;

import org.locationtech.jts.geom.Coordinate;

/**
 * https://www.geodatasource.com/developers/java
 */
public class DistanceCalculator {
    private static double NAUTIC_FACTOR = 0.8684;
    private static double METRIC_FACTOR = 1.609344;

    private DistanceCalculator() {

    }

    private static double area(final BoundingBox boundingBox, final Unit unit) {
        final Coordinate lowerLeft = new Coordinate(boundingBox.minLongitude, boundingBox.minLatitude);
        final Coordinate lowerRight = new Coordinate(boundingBox.maxLongitude, boundingBox.minLatitude);
        final Coordinate upperLeft = new Coordinate(boundingBox.minLongitude, boundingBox.maxLatitude);

        final double lengthX = distance(lowerLeft, lowerRight, unit);
        final double lengthY = distance(lowerLeft, upperLeft, unit);

        return lengthX * lengthY;
    }

    private static double distance(final Coordinate start, final Coordinate end, final Unit unit) {
        if (start.equals(end)) {
            return 0;
        } else {
            double dist = getImperialDistance(start, end);
            dist = convertUnit(unit, dist);
            return (dist);
        }
    }

    private static double getImperialDistance(final Coordinate start, final Coordinate end) {
        double theta = start.getX() - end.getX();
        double dist = Math.sin(Math.toRadians(start.getY())) *
                      Math.sin(Math.toRadians(end.getY())) +
                      Math.cos(Math.toRadians(start.getY())) *
                      Math.cos(Math.toRadians(end.getY())) *
                      Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private static double convertUnit(final Unit unit, double dist) {
        if (unit.equals(Unit.IMPERIAL)) {
            return dist;
        } else if (unit.equals(Unit.METRIC)) {
            return convertToMetric(dist);
        } else if (unit.equals(Unit.NAUTIC)) {
            return convertToNautic(dist);
        } else {
            throw new IllegalArgumentException("Unit is not known");
        }
    }

    private static double convertToMetric(double dist) {
        dist = dist * METRIC_FACTOR;
        return dist;
    }

    private static double convertToNautic(double dist) {
        dist = dist * NAUTIC_FACTOR;
        return dist;
    }

    public static enum Unit {
        METRIC, IMPERIAL, NAUTIC;
    }
}