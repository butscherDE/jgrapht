package routing.regionAware;

import data.CHPreprocessing;
import data.RegionOfInterest;
import data.RoadCH;
import data.RoadGraph;
import index.Index;
import org.locationtech.jts.geom.Polygon;
import routing.regionAware.util.RegionSubGraphBuilder;

public class RegionThrough extends AbstractRegion {
    public RegionThrough(final RoadGraph globalGraph, final RoadCH globalCH, final Index globalIndex,
                         final RegionOfInterest region) {
        super(globalGraph, globalCH, globalIndex, region);
    }

    @Override
    RoadCH getRegionCH() {
        final RegionSubGraphBuilder subGraphBuilder = new RegionSubGraphBuilder();
        final RoadGraph subGraph = subGraphBuilder.getSubGraph(globalGraph, region, entryExitNodes);

        return new CHPreprocessing(subGraph).createCHGraph();
    }
}
