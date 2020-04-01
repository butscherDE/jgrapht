package routing.regionAware;

import data.Node;
import data.Path;
import data.RoadCH;
import data.RoadGraph;
import index.Index;
import org.jgrapht.alg.util.Pair;
import org.locationtech.jts.geom.Polygon;
import routing.DijkstraCH;
import routing.RPHAST;
import routing.RoutingAlgorithm;
import routing.regionAware.util.EntryExitPointExtractor;
import routing.regionAware.util.RouteCandidate;
import routing.regionAware.util.RouteCandidateList;

import java.util.*;

public abstract class AbstractRegion implements RoutingAlgorithm {
    final RoadGraph globalGraph;
    private final RoadCH globalCH;
    private final Index globalIndex;
    final Polygon region;
    private final RoadCH regionCH;
    private final Map<Pair<Node, Node>, Path> allPathsNonBacktracked = new HashMap();
    final Set<Node> entryExitNodes;

    public AbstractRegion(final RoadGraph globalGraph, final RoadCH globalCH, final Index globalIndex, final Polygon region) {
        this.globalGraph = globalGraph;
        this.globalCH = globalCH;
        this.globalIndex = globalIndex;
        this.region = region;

        this.entryExitNodes = new EntryExitPointExtractor(region, globalGraph, globalIndex).extract();
        this.regionCH = getRegionCH();
        this.allPathsNonBacktracked.putAll(getRegionInternalPaths(entryExitNodes, entryExitNodes, regionCH));
    }

    @Override
    public Path findPath(final Node source, final Node target) {
        return findPaths(Collections.singleton(source), Collections.singleton(target)).get(0);
    }

    @Override
    public List<Path> findPaths(final Set<Node> sources, final Set<Node> targets) {
        calcPathsWithoutBacktracking(sources, targets);

        final int numPaths = sources.size() * targets.size();
        final List<RouteCandidate> optimalCandidates = new ArrayList<>(numPaths);
        for (final Node source : sources) {
            for (final Node target : targets) {
                final RouteCandidateList<RouteCandidate> routeCandidates = new RouteCandidateList<>();
                for (final Node entryNode : entryExitNodes) {
                    for (final Node exitNode : entryExitNodes) {
                        final RouteCandidate candidate = new RouteCandidate(source, target, entryNode, exitNode, allPathsNonBacktracked);
                        if ((candidate.regionEntryNode.id == 28 && candidate.regionExitNode.id == 29) ||
                             candidate.regionEntryNode.id == 45 && candidate.regionExitNode.id == 30) {
                            System.out.println("Gain: " + candidate.getGain());
                            System.out.println("In ROI: " + candidate.getTimeInROI());
                            System.out.println("Time: " + candidate.getTime());
                            System.out.println("Detour Time: " + candidate.getDetourTime());
                        }
                        routeCandidates.add(candidate);
                    }
                }

                routeCandidates.pruneDominatedCandidateRoutes();
                routeCandidates.sortByGainAscending();
                final RouteCandidate bestCandidate = routeCandidates.get(0);
                optimalCandidates.add(bestCandidate);
            }
        }

        System.out.println("Gain: " + optimalCandidates.get(0).getGain());
        System.out.println("In ROI: " + optimalCandidates.get(0).getTimeInROI());
        System.out.println("Time: " + optimalCandidates.get(0).getTime());
        System.out.println("Detour Time: " + optimalCandidates.get(0).getDetourTime());

        // TODO use rphast or dijkstra depending on how many sources and targets. Where is the break even point?
        final DijkstraCH dijkstraCHGlobal = new DijkstraCH(globalCH, true);
        final DijkstraCH dijkstraCHRegion = new DijkstraCH(regionCH, true);
        final List<Path> paths = new ArrayList<>(numPaths);
        for (final RouteCandidate candidate : optimalCandidates) {
            final Path startToEntry = dijkstraCHGlobal.findPath(candidate.startNode, candidate.regionEntryNode);
            final Path entryToExit = dijkstraCHRegion.findPath(candidate.regionEntryNode, candidate.regionExitNode);
            final Path exitToEnd = dijkstraCHGlobal.findPath(candidate.regionExitNode, candidate.endNode);

            final Path startToExit = startToEntry.createMergedPath(entryToExit);
            final Path startToEnd = startToExit.createMergedPath(exitToEnd);

            paths.add(startToEnd);
        }

        return paths;
    }

    @Override
    public double getWeight(final Node source, final Node target) {
        return findPath(source, target).getWeight();
    }

    abstract RoadCH getRegionCH();

    private void calcPathsWithoutBacktracking(final Set<Node> source, final Set<Node> target) {
        final Map<Pair<Node, Node>, Path> viaPointsToEntryExitNodes = getViaPointsToEntryExitNodes(source, target);
        allPathsNonBacktracked.putAll(viaPointsToEntryExitNodes);
    }

    private Map<Pair<Node, Node>, Path> getViaPointsToEntryExitNodes(final Set<Node> source, final Set<Node> target) {
        final RPHAST rphastGlobal = new RPHAST(globalCH, false);
        final Set<Node> allSources = getGlobalSources(source);
        final Set<Node> allTargets = getGlobalTargets(target);
        return rphastGlobal.findPathsAsMap(allSources, allTargets);
    }

    private Set<Node> getGlobalSources(final Set<Node> source) {
        final Set<Node> allSources = new LinkedHashSet<>();
        allSources.addAll(source);
        allSources.addAll(entryExitNodes);
        return allSources;
    }

    private Set<Node> getGlobalTargets(final Set<Node> target) {
        final Set<Node> allTargets = new LinkedHashSet<>();
        allTargets.addAll(entryExitNodes);
        allTargets.addAll(target);
        return allTargets;
    }

    private Map<Pair<Node, Node>, Path> getRegionInternalPaths(final Set<Node> entryNodes, final Set<Node> exitNodes,
                                                               final RoadCH regionCH) {
        final RPHAST rphastLocal = new RPHAST(regionCH, false);
        return rphastLocal.findPathsAsMap(entryNodes, exitNodes);
    }


}
