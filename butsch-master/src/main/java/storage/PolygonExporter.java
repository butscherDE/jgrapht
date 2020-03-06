package storage;

import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.util.Collection;

public interface PolygonExporter {
    void export(Collection<Polygon> polygons) throws IOException;
}
