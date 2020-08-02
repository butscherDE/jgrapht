package routing.regionAware;

import data.*;
import index.GridIndex;
import index.vc.ReflectiveEdge;
import org.jgrapht.alg.util.Pair;
import routing.DijkstraCH;
import routing.DijkstraCHFactory;
import routing.RPHAST;
import routing.RoutingAlgorithm;
import routing.regionAware.util.EntryExitPointExtractor;
import routing.regionAware.util.LOTNodeExtractor;
import routing.regionAware.util.RouteCandidate;
import routing.regionAware.util.RouteCandidateList;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractRegion implements RoutingAlgorithm {
    final RoadGraph globalGraph;
    private final RoadCH globalCH;
    final GridIndex index;
    final RegionOfInterest region;
    private final RoadCH regionCH;
    private final Map<Pair<Node, Node>, Path> allPathsNonBacktracked = new HashMap<>();
    final Set<Node> entryExitNodes;
    LOTNodeExtractor lotNodeExtractor;

    public AbstractRegion(final RoadGraph globalGraph, final RoadCH globalCH, final GridIndex globalIndex,
                          final RegionOfInterest region) {
        this.globalGraph = globalGraph;
        this.globalCH = globalCH;
        this.index = globalIndex;
        this.region = region;

        this.entryExitNodes = new EntryExitPointExtractor(region, globalIndex).extract();
        System.out.println("found ee");
        this.regionCH = getValidatedRegionCH();
        System.out.println("found region ch");
        this.allPathsNonBacktracked.putAll(getRegionInternalPaths(entryExitNodes, entryExitNodes, regionCH));
        System.out.println("found all paths non backtracked");
    }

    public RoadCH getValidatedRegionCH() {
        final RoadCH regionCH = getRegionCH();

        if (regionCH.getGraph().vertexSet().size() <= 1) {
            throw new IllegalArgumentException("Empty region");
        }

        return regionCH;
    }

    @Override
    public Path findPath(final Node source, final Node target) {
        return findPaths(Collections.singleton(source), Collections.singleton(target)).get(0);
    }

    @Override
    public List<Path> findPaths(final Set<Node> sources, final Set<Node> targets) {
        calcPathsWithoutBacktracking(sources, targets);
        // TODO clean!
        final int numPaths = sources.size() * targets.size();
        final List<RouteCandidate> optimalCandidates = new ArrayList<>(numPaths);
        for (final Node source : sources) {
            for (final Node target : targets) {
                lotNodeExtractor = new LOTNodeExtractor(globalGraph, source, target, entryExitNodes,
                                                        allPathsNonBacktracked);


                final RouteCandidateList<RouteCandidate> routeCandidates = new RouteCandidateList<>();
                for (final Node entryNode : lotNodeExtractor.getLotNodesFor(source)) {
                    for (final Node exitNode : lotNodeExtractor.getLotNodesFor(target)) {
                        final RouteCandidate candidate = new RouteCandidate(source, target, entryNode, exitNode,
                                                                            allPathsNonBacktracked);
                        routeCandidates.add(candidate);
                    }
                }

                routeCandidates.sortByTimeInROIDescending();
                routeCandidates.pruneDominatedCandidateRoutes();
                routeCandidates.pruneLowerQuantileInROI();
                routeCandidates.sortByGainNonAscending();
                final RouteCandidate bestCandidate = routeCandidates.getFirstN(1).get(0);
                optimalCandidates.add(bestCandidate);
            }
        }

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
        System.out.println("entry nodes size:" + entryNodes.size());
        final RPHAST rphastLocal = new RPHAST(regionCH, false);
        return rphastLocal.findPathsAsMap(entryNodes, exitNodes);
    }


}
