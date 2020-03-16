package storage;

import org.locationtech.jts.geom.Polygon;

import java.util.List;

public interface RoiImporter {
    List<Polygon> importPolygons();
}
