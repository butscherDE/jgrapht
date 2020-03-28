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
    private Path mergedPath = null;

    public RouteCandidate(final Node startNode, final Node endNode, final Node regionEntryNode,
                          final Node regionExitNode, final Map<Pair<Node, Node>, Path> allPaths) {
        this.startToRegionEntry = allPaths.get(new Pair<>(startNode, regionEntryNode));
        this.regionEntryToRegionExit = allPaths.get(new Pair<>(regionEntryNode, regionExitNode));
        this.regionExitToEnd = allPaths.get(new Pair<>(regionExitNode, endNode));
        this.directRouteStartEnd = allPaths.get(new Pair<>(startNode, endNode));
    }

    public Path getMergedPath() {
//        mergePathIfNotDone();

        return this.mergedPath;
    }

    private void mergePath() {
//        Path completePathCandidate = new PathMerge(queryGraph, algoOpts.getWeighting());
//
//        completePathCandidate.addPath(startToDetourEntry);
//        completePathCandidate.addPath(detourEntryToDetourExit);
//        completePathCandidate.addPath(detourExitToEnd);
//
//        completePathCandidate.setFromNode(startNodeID);
//        completePathCandidate.extract();
//
//        this.mergedPath = completePathCandidate;
    }
}
