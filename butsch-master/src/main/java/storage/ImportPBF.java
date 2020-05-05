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
                .onComplete(new Completer())
                .onNode(onNodes)
                .onWay(onWays)
                .onRelation(onRelations)
                .onChangeset(new DummyChangeSet())
                .parse();
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
        final Map<Long, Node> nodes = Collections.synchronizedMap(new HashMap<>());

        @Override
        public void accept(final com.wolt.osm.parallelpbf.entity.Node node) {
            final data.Node internalNodeFormat = new Node(node.getId(), node.getLon(), node.getLat(), 0);

            nodes.put(internalNodeFormat.id, internalNodeFormat);
        }
    }

    private class RoadGraphEdgeAdder implements Consumer<Way> {
        private final List<Pair<Long, Long>> edges = Collections.synchronizedList(new LinkedList<>());
        private final Map<Long, Way> ways = Collections.synchronizedMap(new HashMap<>());

        @Override
        public void accept(final Way way) {
            ways.put(way.getId(), way);
            final List<Long> nodeIds = way.getNodes();

            if (isRoad(way)) {
                rememberEdgesToAddToGraph(nodeIds);
            }
        }

        public boolean isRoad(final Way way) {
            return way.getTags().get("highway") != null;
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
        final Map<Long, Relation> relations = Collections.synchronizedMap(new HashMap<>());

        @Override
        public void accept(final Relation relation) {
            final Map<String, String> tags = relation.getTags();
            final String type = tags.get("type");
            final String landuse = tags.get("landuse");
            if ((type != null && type.equals("boundary")) ||
                (landuse != null) && landuse.equals("forest") ||
                (type != null) && type.equals("multipolygon")) {
                relations.put(relation.getId(), relation);
            }
        }

        public List<NodeRelation> getNodeRelations() {
            final List<NodeRelation> nodeRelations = new LinkedList<>();

            for (final Map.Entry<Long, Relation> relationEntry : relations.entrySet()) {
                try {
                    final NodeRelation relation = getRelation(relationEntry);
                    nodeRelations.add(relation);
                } catch (NullPointerException | NoSuchElementException e) {
                    System.err.println("Relation " + relationEntry.getValue().getId() + " could not be processed because of invalid data.");
                }
            }

            return nodeRelations;
        }

        private NodeRelation getRelation(final Map.Entry<Long, Relation> relationEntry) {
            final Relation relation = relationEntry.getValue();
            final List<Long> nodeIds = recurseToFindNodes(relation);

            final NodeRelation newRelation = NodeRelation.createFromNodeIds(relation.getId(),
                                                                            relation.getInfo().toString(),
                                                                            relation.getTags(), nodeIds, onNodes.nodes);
            return newRelation;
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

        private void addEmptyRelation(final Map.Entry<Long, Relation> relationEntry) {
            final Long relationId = relationEntry.getKey();
            final String description = "INVALID";
            @SuppressWarnings("unchecked") final Map<String, String> dummyMap = Collections.EMPTY_MAP;
            @SuppressWarnings("unchecked") final List<Node> dummyNodes = Collections.EMPTY_LIST;
            nodeRelations.add(new NodeRelation(relationId, description, dummyMap, dummyNodes));
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
