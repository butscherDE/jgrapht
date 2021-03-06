package routing.regionAware.util;

import evalutation.Config;

import java.util.*;

public class RouteCandidateList<T extends RouteCandidate> {
    private List<T> candidates;

    public RouteCandidateList() {
        this.setCandidates(new ArrayList<T>());
    }

    public void sortByGainNonAscending() {
        Collections.sort(this.candidates);
    }

    public void sortByTimeInROIDescending() {
        Collections.sort(this.candidates, (Comparator<RouteCandidate>) (rc1, rc2) -> (-1) * Double.compare(rc1.getTimeInROI(), rc2.getTimeInROI()));
    }

    public List<RouteCandidate> getFirstN(final int numberOfFirstElements) {
        if (Config.VERBOSE) {
            System.out.println("Number of Route Candidates: " + candidates.size());
        }
        final List<RouteCandidate> paths = new ArrayList<>(numberOfFirstElements);

        final int endOfCandidates = this.candidates.size() - 1;
        paths.addAll(addNonIntersectedPaths(numberOfFirstElements, endOfCandidates));
        paths.addAll(fillWithIntersectedPaths(numberOfFirstElements - paths.size(), endOfCandidates));

        return paths;
    }

    private Collection<RouteCandidate> addNonIntersectedPaths(final int numberOfFirstElements,
                                                              final int endOfCandidates) {
        return addPathsBasedOnIntersectionStatus(numberOfFirstElements, endOfCandidates, false);
    }

    private Collection<RouteCandidate> fillWithIntersectedPaths(final int numberOfAdditionalElements, final int endOfCandidates) {
        return addPathsBasedOnIntersectionStatus(numberOfAdditionalElements, endOfCandidates, true);
    }

    private List<RouteCandidate> addPathsBasedOnIntersectionStatus(int nOfFirstElements, int endOfCandidates,
                                                         boolean addSelfIntersecting) {
        List<RouteCandidate> paths = new ArrayList<>(nOfFirstElements);
        int indexIntoCandidates = endOfCandidates;
        while (indexIntoCandidates >= 0 && paths.size() < nOfFirstElements) {
            final RouteCandidate candidate = this.candidates.get(indexIntoCandidates);

            if (candidate.isDetourSelfIntersecting() == addSelfIntersecting) {
                paths.add(candidate);
            }

            indexIntoCandidates--;
        }

        return paths;
    }

    public void pruneDominatedCandidateRoutes() {
        int currentPruningCandidateIndex = 1;
        while (indexInCandidateBounds(currentPruningCandidateIndex)) {
            RouteCandidate currentPruningCandidate = this.candidates.get(currentPruningCandidateIndex);

            boolean foundDominatingPath = isThisCandidateDominatedByAny(currentPruningCandidateIndex, currentPruningCandidate);

            currentPruningCandidateIndex = pruneOrUpdateIndex(currentPruningCandidateIndex, foundDominatingPath);
        }
    }

    private boolean isThisCandidateDominatedByAny(int currentPruningCandidateIndex, RouteCandidate currentPruningCandidate) {
        boolean foundDominatingPath = false;
        for (int i = currentPruningCandidateIndex - 1; i >= 0 && !foundDominatingPath; i--) {
            // routeCandidates must be sorted by now. Therefore dominators can only be found on lower indices than the current pruning candidate.
            RouteCandidate possiblyBetterRouteCandidate = this.candidates.get(i);

            if (isPruningCandidateDominated(currentPruningCandidate, possiblyBetterRouteCandidate)) {
                foundDominatingPath = true;
            }
        }
        return foundDominatingPath;
    }

    private int pruneOrUpdateIndex(int currentPruningCandidateIndex, boolean foundDominatingPath) {
        if (foundDominatingPath) {
            this.candidates.remove(currentPruningCandidateIndex);
        } else {
            currentPruningCandidateIndex++;
        }
        return currentPruningCandidateIndex;
    }

    private boolean isPruningCandidateDominated(RouteCandidate currentPruningCandidate, RouteCandidate possiblyBetterRouteCandidate) {
        return possiblyBetterRouteCandidate.getTime() < currentPruningCandidate.getTime() &&
               possiblyBetterRouteCandidate.getTimeInROI() > currentPruningCandidate.getTimeInROI();
    }

    private boolean indexInCandidateBounds(int currentPruningCandidateIndex) {
        return currentPruningCandidateIndex < this.candidates.size();
    }

    public int size() {
        return this.candidates.size();
    }

    public void remove(Object o) {
        this.candidates.remove(o);
    }

    public void remove(int i) { this.candidates.remove(i); }

    public void add(T o) {
        if (o.isLegalCandidate()) {
            this.candidates.add(o);
        }
    }

    public void clear() {
        this.candidates.clear();
    }

    public T get(int i) {
        return this.candidates.get(i);
    }

    void setCandidates(List<T> candidates) {
        this.candidates = candidates;
    }

    public RouteCandidate getMaxGainCandidate() {
        return Collections.max(candidates, Comparator.comparingDouble(RouteCandidate::getGain));
    }

    public void pruneLowerQuantileInROI() {
        // Assumes that routeCandidates was already sorted descending to roi distance after pruning dominated route candidates
        final int endIndex = (int) (size() * 0.75) + 1;

        candidates = candidates.subList(0, endIndex);
    }
}
