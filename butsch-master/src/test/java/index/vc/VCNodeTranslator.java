package index.vc;

import data.VisibilityCell;
import index.Index;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VCNodeTranslator {
    public static List<Long> getNodeIDs(final VisibilityCell vc, final Index index) {
        final Coordinate[] coordinates = vc.getPolygon().getCoordinates();
        final List<Long> nodes = new ArrayList<>(coordinates.length - 1);
        Arrays.stream(coordinates).forEach(a -> nodes.add(index.getClosestNode(a.getX(), a.getY()).id));

        return nodes;
    }
}
