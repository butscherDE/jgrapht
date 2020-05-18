package routing.regionAware.util;

import data.RegionOfInterest;
import data.VisibilityCell;
import geometry.RedBlueSegmentIntersectionCrossProductFactory;
import geometry.RedBlueSegmentIntersectionFactory;

import java.util.List;
import java.util.stream.Collectors;

public class VCROIIntersector {
    public List<VisibilityCell> getIntersectingCells(final List<VisibilityCell> inVC, final RegionOfInterest roi) {
        RedBlueSegmentIntersectionCrossProductFactory intersectionFactory = new RedBlueSegmentIntersectionCrossProductFactory();

        final List<VisibilityCell> intersectingVCs = inVC
                .stream()
                .filter(a -> intersectionFactory
                        .createInstance(a.getSortedLineSegments(), roi.getSortedLineSegments())
                        .isIntersectionPresent())
                .collect(Collectors.toList());

        return intersectingVCs;
    }

}
