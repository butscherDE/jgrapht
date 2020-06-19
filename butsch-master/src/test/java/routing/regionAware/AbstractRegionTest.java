package routing.regionAware;

import data.RegionOfInterest;
import data.RoadCH;
import data.RoadGraph;
import index.GridIndex;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import util.PolygonRoutingTestGraph;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractRegionTest {
    final static PolygonRoutingTestGraph GRAPH_MOCKER = new PolygonRoutingTestGraph();

    @Test
    public void emptyRegion() {
        final RoadGraph globalGraph = GRAPH_MOCKER.graph;
        final RoadCH globalCh = GRAPH_MOCKER.ch;
        final GridIndex gridIndex = GRAPH_MOCKER.gridIndex;

        final Coordinate[] roiCoordinates = {
                new Coordinate(-180, -90),
                new Coordinate(-179, -90),
                new Coordinate(-179, -89),
                new Coordinate(-180, -89),
                new Coordinate(-180, -90)
        };
        final RegionOfInterest roi = new RegionOfInterest(new GeometryFactory().createPolygon(roiCoordinates));

        assertThrows(IllegalArgumentException.class, () -> getInstance(globalGraph, globalCh, gridIndex, roi));
    }

    abstract AbstractRegion getInstance(final RoadGraph globalGraph, final RoadCH globalCh, final GridIndex index, final RegionOfInterest roi);
}
