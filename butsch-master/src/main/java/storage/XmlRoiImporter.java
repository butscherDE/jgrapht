package storage;

import org.locationtech.jts.geom.Polygon;

import java.util.List;

public class XmlRoiImporter implements RoiImporter {
    private final String path;

    public XmlRoiImporter(final String path) {
        this.path = path;
    }

    @Override
    public List<Polygon> importPolygons() {
        return null;
    }
}
