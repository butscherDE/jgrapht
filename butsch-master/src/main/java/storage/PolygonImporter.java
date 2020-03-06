package storage;

import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.List;

public interface PolygonImporter {
    List<Polygon> importPolygons() throws IOException;
}
