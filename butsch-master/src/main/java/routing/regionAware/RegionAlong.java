package routing.regionAware;

import data.RegionOfInterest;
import data.RoadCH;
import data.RoadGraph;
import index.GridIndex;

public class RegionAlong extends AbstractRegion {
    public RegionAlong(final RoadGraph globalGraph, final RoadCH globalCH, final GridIndex globalIndex,
                       final RegionOfInterest region) {
        super(globalGraph, globalCH, globalIndex, region);
    }

    @Override
    RoadCH getRegionCH() {
        return null;
    }
}
