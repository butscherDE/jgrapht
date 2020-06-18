package storage;

import com.wolt.osm.parallelpbf.ParallelBinaryParser;
import com.wolt.osm.parallelpbf.entity.*;
import data.Edge;
import data.Node;
import data.NodeRelation;
import data.RoadGraph;
import evalutation.StopWatchVerbose;
import org.jgrapht.alg.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ImportPBF implements GraphImporter {
    private final RoadGraph graph = new RoadGraph(Edge.class);
    private final String path;
    private final List<NodeRelation> nodeRelations = new ArrayList<>();
    private RoadGraphNodeAdder onNodes;
    private RoadGraphEdgeAdder onWays;
    private NodeRelationAdder onRelations;

    public ImportPBF(final String path) {
        this.path = path;
    }

    @Override
    public RoadGraph createGraph() throws FileNotFoundException {
        final StopWatchVerbose swImport = new StopWatchVerbose("Import PBF");

        onNodes = new RoadGraphNodeAdder();
        onWays = new RoadGraphEdgeAdder();
        onRelations = new NodeRelationAdder();

        runParser();
        addGraphData();
        createNodeRelations();

        swImport.printTimingIfVerbose();
        return graph;
    }

    private void runParser() throws FileNotFoundException {
        final InputStream input = new FileInputStream(path);

        new ParallelBinaryParser(input, Runtime.getRuntime().availableProcessors() -2)
                .onHeader(new HeaderPrinter())
                .onBoundBox(new DummyBBox())
                .onNode(onNodes)
                .onWay(onWays)
                .onRelation(onRelations)
                .onChangeset(new DummyChangeSet())
                .onComplete(new Completer())
                .parse();

        // TODO This is super dirty, but necessary as .parse() doesn't wait for thread termination in executor pool.
        try {Thread.sleep(5000);} catch (Exception e) {}
    }

    private void addGraphData() {
        onWays.addEntitiesToGraph();
    }

    private void createNodeRelations() {
        nodeRelations.addAll(onRelations.getNodeRelations());
    }

    public List<NodeRelation> getNodeRelations() {
        if (graph.vertexSet().size() == 0) {
            throw new IllegalStateException("Call createGraph() first");
        }
        return nodeRelations;
    }

    private static class HeaderPrinter implements Consumer<Header> {
        @Override
        public void accept(final Header header) {
            System.out.println("Importing from source " + header.getSource());
            System.out.println("Features (required): " + header.getRequiredFeatures());
            System.out.println("Features (optional): " + header.getOptionalFeatures());
        }
    }

    private class RoadGraphNodeAdder implements Consumer<com.wolt.osm.parallelpbf.entity.Node> {
        private final ReentrantLock lock = new ReentrantLock();
        final Map<Long, Node> nodes = Collections.synchronizedMap(new HashMap<>());

        @Override
        public void accept(final com.wolt.osm.parallelpbf.entity.Node node) {
            lock.lock();
            final data.Node internalNodeFormat = new Node(node.getId(), node.getLon(), node.getLat(), 0);

            nodes.put(internalNodeFormat.id, internalNodeFormat);
            lock.unlock();
        }
    }

    private class RoadGraphEdgeAdder implements Consumer<Way> {
        private final Map<String, Boolean> isRoad = new HashMap<>();

        private final ReentrantLock lock = new ReentrantLock();
        private final List<Pair<Long, Long>> edges = Collections.synchronizedList(new LinkedList<>());
        private final Map<Long, Way> ways = Collections.synchronizedMap(new HashMap<>());

        public RoadGraphEdgeAdder() {
            isRoad.put("motorway", true);
            isRoad.put("trunk", true);
            isRoad.put("primary", true);
            isRoad.put("secondary", true);
            isRoad.put("tertiary", true);
            isRoad.put("unclassified", true);
            isRoad.put("residential", true);
            isRoad.put("motorway_link", true);
            isRoad.put("trunk_link", true);
            isRoad.put("primary_link", true);
            isRoad.put("secondary_link", true);
            isRoad.put("tertiary_link", true);
            isRoad.put("living_street", true);
            isRoad.put("service", false);
            isRoad.put("pedestrian", false);
            isRoad.put("track", false);
            isRoad.put("bus_guideway", false);
            isRoad.put("escape", false);
            isRoad.put("raceway", false);
            isRoad.put("road", false);
            isRoad.put("footway", false);
            isRoad.put("bridleway", false);
            isRoad.put("steps", false);
            isRoad.put("corridor", false);
            isRoad.put("path", false);
            isRoad.put("cycleway", false);
            isRoad.put("construction", false); //?
            isRoad.put("bus_stop", false);
            isRoad.put("crossing", false);
            isRoad.put("elevator", false);
            isRoad.put("emergency_access_point", false);
            isRoad.put("give_way", false);
            isRoad.put("milestone", false);
            isRoad.put("mini_roundabout", true);
            isRoad.put("passing_place", false);
            isRoad.put("platform", false);
            isRoad.put("rest_area", false);
            isRoad.put("speed_camera", false);
            isRoad.put("street_lamp", false);
            isRoad.put("services", false);
            isRoad.put("stop", false);
            isRoad.put("traffic_mirror", false);
            isRoad.put("traffic_signals", false);
            isRoad.put("trailhead", false);
            isRoad.put("turning_circle", true);
            isRoad.put("turning_loop", true);
            isRoad.put("toll_gantry", true);
        }

        @Override
        public void accept(final Way way) {
            lock.lock();
            ways.put(way.getId(), way);
            final List<Long> nodeIds = way.getNodes();
            if (isRoad(way)) {
                addRoadData(way, nodeIds);
            }
            lock.unlock();
        }

        public boolean isRoad(final Way way) {
            final String tag = way.getTags().get("highway");
            return tag != null && isRoad.get(tag);
//            return way.getTags().get("highway") != null;
        }

        private void addRoadData(Way way, List<Long> nodeIds) {
            final String onewayTag = way.getTags().get("oneway");
            final String junctionTag = way.getTags().get("junction");
            if (onewayTag == null || onewayTag.equals("no")) {
                addEdgesBidirectional(nodeIds);
            } else if (onewayTag.equals("yes") || (junctionTag != null && junctionTag.equals("roundabout"))) {
                addEdgesForward(nodeIds);
            } else if (onewayTag.equals("-1")) {
                addEdgesOnlyReverse(nodeIds);
            } else if (onewayTag.equals("reversible")) {
                reversibleTagFoundError();
                return;
            } else {
                unknownTagError(onewayTag);
                return;
            }
        }

        private void addEdgesBidirectional(List<Long> nodeIds) {
            rememberEdgesToAddToGraph(nodeIds);
            Collections.reverse(nodeIds);
            rememberEdgesToAddToGraph(nodeIds);
        }

        private void addEdgesForward(List<Long> nodeIds) {
            rememberEdgesToAddToGraph(nodeIds);
        }

        private void addEdgesOnlyReverse(List<Long> nodeIds) {
            Collections.reverse(nodeIds);
            rememberEdgesToAddToGraph(nodeIds);
        }

        private void reversibleTagFoundError() {
            throw new InputMismatchException("Cannot handle reversible edges (tag oneway:reversible)");
        }

        private void unknownTagError(String onewayTag) {
            throw new InputMismatchException("Value \"" + onewayTag + "\" is not known for oneway tag");
        }

        public void rememberEdgesToAddToGraph(final List<Long> nodeIds) {
            final Iterator<Long> nodeIdIterator = nodeIds.iterator();
            long lastNodeId = nodeIdIterator.next();
            final List<Pair<Long, Long>> edgesOnThisWay = new LinkedList<>();
            while (nodeIdIterator.hasNext()) {
                final long currentNodeId = nodeIdIterator.next();

                edgesOnThisWay.add(new Pair<>(lastNodeId, currentNodeId));
                lastNodeId = currentNodeId;
            }
            edges.addAll(edgesOnThisWay);
        }

        public void addEntitiesToGraph() {
            for (final Pair<Long, Long> edge : edges) {
                final Node baseNode = onNodes.nodes.get(edge.getFirst());
                final Node adjNode = onNodes.nodes.get(edge.getSecond());

                graph.addVertex(baseNode);
                graph.addVertex(adjNode);
                graph.addEdge(baseNode, adjNode);
            }
        }
    }

    private class NodeRelationAdder implements Consumer<Relation> {
        private final ReentrantLock lock = new ReentrantLock();
        final Map<Long, Relation> relations = Collections.synchronizedMap(new HashMap<>());
        private List<Long> invalidRelations;

        @Override
        public void accept(final Relation relation) {
            lock.lock();
            final Map<String, String> tags = relation.getTags();
            final String type = tags.get("type");
            final String landuse = tags.get("landuse");
            if ((type != null && type.equals("boundary")) ||
                (landuse != null) && landuse.equals("forest") ||
                (type != null) && type.equals("multipolygon")) {
                relations.put(relation.getId(), relation);
            }
            lock.unlock();
        }

        public List<NodeRelation> getNodeRelations() {
            final List<NodeRelation> nodeRelations = new LinkedList<>();
            invalidRelations  = new LinkedList<>();

            synchronized (relations) {
                addAllRelationsSynced(nodeRelations);
            }

            System.err.println(invalidRelations.size() + " invalid Relations: " + invalidRelations);

            return nodeRelations;
        }

        private void addAllRelationsSynced(List<NodeRelation> nodeRelations) {
            for (final Map.Entry<Long, Relation> relationEntry : relations.entrySet()) {
                addRelationEntry(nodeRelations, relationEntry);
            }
        }

        private void addRelationEntry(List<NodeRelation> nodeRelations, Map.Entry<Long, Relation> relationEntry) {
            try {
                final NodeRelation relation = getRelation(relationEntry);
                nodeRelations.add(relation);
            } catch (NullPointerException | NoSuchElementException e) {
                final long invalidRelationId = relationEntry.getValue().getId();
                invalidRelations.add(invalidRelationId);
            }
        }

        private NodeRelation getRelation(final Map.Entry<Long, Relation> relationEntry) {
            final Relation relation = relationEntry.getValue();
            final List<Long> nodeIds = recurseToFindNodes(relation);

            return NodeRelation.createFromNodeIds(relation.getId(),
                                                  relation.getInfo().toString(),
                                                  relation.getTags(),
                                                  nodeIds,
                                                  onNodes.nodes);
        }

        private List<Long> recurseToFindNodes(final Relation relation) {
            final List<Long> nodeIds = new LinkedList<>();

            final List<RelationMember> members = relation.getMembers();
            for (final RelationMember member : members) {
                final Long memberId = member.getId();
                final RelationMember.Type type = member.getType();

                if (type == RelationMember.Type.RELATION) {
                    nodeIds.addAll(recurseToFindNodes(relations.get(memberId)));
                } else if (type == RelationMember.Type.NODE) {
                    nodeIds.add(memberId);
                } else if (type == RelationMember.Type.WAY) {
                    nodeIds.addAll(recurseToFindNodes(onWays.ways.get(memberId)));
                }
            }

            return nodeIds;
        }

        private List<Long> recurseToFindNodes(final Way way) {
            return way.getNodes();
        }
    }

    private static class Completer implements Runnable {
        final StopWatchVerbose sw = new StopWatchVerbose("PBF Read");

        @Override
        public void run() {
            sw.printTimingIfVerbose();
        }
    }

    private static class DummyBBox implements Consumer<BoundBox> {
        @Override
        public void accept(final BoundBox boundBox) {

        }
    }

    private static class DummyChangeSet implements Consumer<Long> {
        @Override
        public void accept(final Long aLong) {

        }
    }
}
