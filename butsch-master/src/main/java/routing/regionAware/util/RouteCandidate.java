package routing.regionAware.util;

import data.Node;
import data.Path;
import org.jgrapht.alg.util.Pair;

import java.util.Map;

public class RouteCandidate implements Comparable<RouteCandidate> {
    private final Path startToRegionEntry;
    private final Path regionEntryToRegionExit;
    private final Path regionExitToEnd;
    private final Path directRouteStartEnd;
    private final Path mergedPath;

    public RouteCandidate(final Node startNode, final Node endNode, final Node regionEntryNode,
                          final Node regionExitNode, final Map<Pair<Node, Node>, Path> allPaths) {
        this.startToRegionEntry = allPaths.get(new Pair<>(startNode, regionEntryNode));
        this.regionEntryToRegionExit = allPaths.get(new Pair<>(regionEntryNode, regionExitNode));
        this.regionExitToEnd = allPaths.get(new Pair<>(regionExitNode, endNode));
        this.directRouteStartEnd = allPaths.get(new Pair<>(startNode, endNode));
        this.mergedPath = startToRegionEntry.createMergedPath(regionEntryToRegionExit).createMergedPath(regionExitToEnd);
    }

    public Path getMergedPath() {
        return this.mergedPath;
    }

    public boolean isDetourSelfIntersecting() {
        return mergedPath.isSelfIntersecting();
    }

    public double getTime() {
        return mergedPath.getWeight();
    }

    public double getTimeInROI() {
        return regionEntryToRegionExit.getWeight();
    }

    public double getGain() {
        return getTimeInROI() / (getDetourTime() + 1);
    }

    public double getDetourTime() {
        return getTime() - directRouteStartEnd.getWeight();
    }

    @Override
    public int compareTo(RouteCandidate o) {
        final double gainDifference = this.getGain() - o.getGain();
        if (gainDifference < 0) {
            return -1;
        } else if (gainDifference == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        String sb = "startNodeID: " + mergedPath.getStartVertex() + ", " +
                    "endNodeID: " + mergedPath.getEndVertex() + ", " +
                    "polygonEntryNodeID: " + regionEntryToRegionExit.getStartVertex() + ", " +
                    "polygonExitNodeID: " + regionEntryToRegionExit.getEndVertex() + ", " +
                    "Time: " + this.getTime() + ", " +
                    "TimeInROI: " + getTimeInROI() + ", " +
                    "TimeDetour: " + getDetourTime() + ", " +
                    "gain: " + this.getGain();
        return sb;
    }

    public boolean isLegalCandidate() {
        return isAllSubpathsValid();
    }

    private boolean isAllSubpathsValid() {
        boolean allValid = true;
        allValid &= isSubpathValid(this.startToRegionEntry);
        allValid &= isSubpathValid(this.regionEntryToRegionExit);
        allValid &= isSubpathValid(this.regionExitToEnd);

        return allValid;
    }

    private static boolean isSubpathValid(final Path path) {
        if (path != null) {
            return path.isFound();
        } else {
            throw new IllegalStateException("Calculate paths before validating them.");
        }
    }
}
