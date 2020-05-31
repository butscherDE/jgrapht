package routing.regionAware.util;

import data.Node;
import data.RegionOfInterest;
import data.RoadGraph;
import index.GridIndex;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Polygon;
import util.CircularList;
import util.PolygonRoutingTestGraph;

import javax.swing.plaf.synth.Region;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimplerPolygonContractionSetBuilder {
    private final static GeometryFactory GF = new GeometryFactory();

    private final GridIndex gridIndex;
    private final Polygon polygon;
    private final Set<Node> entryExitNodes;
    private PolygonLineContractor plc;


    public SimplerPolygonContractionSetBuilder(final GridIndex gridIndex, Polygon polygon) {
        this.gridIndex = gridIndex;
        this.polygon = polygon;

        final RegionOfInterest roi = new RegionOfInterest(polygon);
        final EntryExitPointExtractor eepe = new EntryExitPointExtractor(roi, gridIndex);
        entryExitNodes = eepe.extract();
    }

    public int getContractionSetSize(final int startCoordinateIndex) {
        plc = new PolygonLineContractor(polygon, startCoordinateIndex);
        int forwardMax = maxContractions(PolygonLineContractor::removeForward);
        plc = plc.restartAt(forwardMax);
        int backwardMax = maxContractions(PolygonLineContractor::removeBackward);

        return forwardMax + backwardMax;
    }

    private int maxContractions(Function<PolygonLineContractor, CircularList<LineSegment>> removeFunction) {
        int contracted = 0;
        int maxValidContractions = 0;
        while (plc.isReducable()) {
            final CircularList<LineSegment> lineSegments = removeFunction.apply(plc);
            final RegionOfInterest roi = toRoi(lineSegments);
            final boolean isValid = producesEqualEENodeSet(roi);

            contracted++;
            maxValidContractions = isValid ? contracted : maxValidContractions;
        }

        return maxValidContractions;
    }

    private RegionOfInterest toRoi(final List<LineSegment> segments) {
        final Coordinate[] coordinates = new Coordinate[segments.size() + 1];
        coordinates[0] = segments.get(0).p0;

        final Iterator<LineSegment> segmentsIt = segments.iterator();
        for (int i = 1; segmentsIt.hasNext(); i++) {
            coordinates[i] = segmentsIt.next().p1;
        }

        return new RegionOfInterest(GF.createPolygon(coordinates));
    }

    private boolean producesEqualEENodeSet(final RegionOfInterest roi) {
        final EntryExitPointExtractor eepe = new EntryExitPointExtractor(roi, gridIndex);

        final Set<Node> newEntryExitNodes = eepe.extract();
        return newEntryExitNodes.equals(entryExitNodes);
    }
}
