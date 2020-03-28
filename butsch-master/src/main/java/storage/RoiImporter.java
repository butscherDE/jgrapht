package storage;

import org.locationtech.jts.geom.Polygon;

import java.util.List;

public interface RoiImporter {
    @SuppressWarnings("SameReturnValue")
    List<Polygon> importPolygons();
}
