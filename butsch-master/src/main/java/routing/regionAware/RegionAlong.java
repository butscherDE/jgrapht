package routing.regionAware;

import data.RegionOfInterest;
import data.RoadCH;
import data.RoadGraph;
import index.Index;

public class RegionAlong extends AbstractRegion {
    public RegionAlong(final RoadGraph globalGraph, final RoadCH globalCH, final Index globalIndex,
                       final RegionOfInterest region) {
        super(globalGraph, globalCH, globalIndex, region);
    }

    @Override
    RoadCH getRegionCH() {
        return null;
    }
}
