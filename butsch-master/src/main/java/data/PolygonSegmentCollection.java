package data;

import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;

import java.util.List;

public interface PolygonSegmentCollection {
    List<LineSegment> getSortedLineSegments();

    Polygon getPolygon();
}
