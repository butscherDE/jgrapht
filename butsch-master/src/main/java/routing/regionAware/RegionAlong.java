package routing.regionAware;

import data.RoadCH;
import data.RoadGraph;
import index.Index;
import org.locationtech.jts.geom.Polygon;

public class RegionAlong extends AbstractRegion {
    public RegionAlong(final RoadGraph globalGraph, final RoadCH globalCH, final Index globalIndex,
                       final Polygon region) {
        super(globalGraph, globalCH, globalIndex, region);
    }

    @Override
    RoadCH getRegionCH() {
        return null;
    }
}
