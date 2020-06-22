package routing.regionAware;

import data.*;
import geometry.BoundingBox;
import geometry.RedBlueSegmentIntersectionCrossProductFactory;
import geometry.SegmentIntersectionAlgorithm;
import index.GridIndex;
import index.vc.ReflectiveEdge;
import org.jgrapht.alg.shortestpath.ContractionHierarchyPrecomputation;
import org.locationtech.jts.geom.LineSegment;
import routing.regionAware.util.RegionSubGraphBuilder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegionThrough extends AbstractRegion {
    public RegionThrough(final RoadGraph globalGraph, final RoadCH globalCH, final GridIndex globalIndex,
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
