package routing.regionAware.util;

import data.Node;
import data.Path;
import org.jgrapht.alg.util.Pair;

import java.util.Map;

public class RouteCandidate implements Comparable<RouteCandidate> {
    final Path startToRegionEntry;
    final Path regionEntryToRegionExit;
    final Path regionExitToEnd;
    final Path directRouteStartEnd;
    final Path mergedPath;

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
        return mergedPath.getTime();
    }

    public double getTimeInROI() {
        return regionEntryToRegionExit.getTime();
    }

    public double getGain() {
        return getTimeInROI() / (getDetourTime() + 1);
    }

    public double getDetourTime() {
        return getTime() - directRouteStartEnd.getTime();
    }

    @Override
    public int compareTo(RouteCandidate o) {
        return Double.compare(this.getGain(), o.getGain());
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