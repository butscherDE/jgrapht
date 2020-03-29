package routing.regionAware.util;

import data.Node;
import data.Path;
import org.jgrapht.alg.util.Pair;

import java.util.Map;

public class RouteCandidate {
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
}
