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

    public ImportPBF(final String path) {
        this.path = path;
    }

    @Override
    public RoadGraph createGraph() throws FileNotFoundException {
        final StopWatchVerbose swImport = new StopWatchVerbose("Import PBF");

        final InputStream input = new FileInputStream(path);
        final RoadGraphNodeAdder onNodes = new RoadGraphNodeAdder();
        final RoadGraphEdgeAdder onWays = new RoadGraphEdgeAdder();

        new ParallelBinaryParser(input, 6).onHeader(new HeaderPrinter()).onBoundBox(new Consumer<BoundBox>() {
            @Override
            public void accept(final BoundBox boundBox) {

            }
        }).onComplete(new Completer()).onNode(onNodes).onWay(onWays).onRelation(new NodeRelationAdder())
                                          .onChangeset(new Consumer<Long>() {
                                              @Override
                                              public void accept(final Long aLong) {

                                              }
                                          }).parse();

        onNodes.addNodesToGraph();
        onWays.addEdgesToGraph();

        swImport.printTimingIfVerbose();
        return graph;
    }

    private class HeaderPrinter implements Consumer<Header> {

        @Override
        public void accept(final Header header) {
            System.out.println("Importing from source " + header.getSource());
            System.out.println("Features (required): " + header.getRequiredFeatures());
            System.out.println("Features (optional): " + header.getOptionalFeatures());
        }
    }

    private class RoadGraphNodeAdder implements Consumer<com.wolt.osm.parallelpbf.entity.Node> {
        final List<Node> nodes = Collections.synchronizedList(new LinkedList<>());

        @Override
        public void accept(final com.wolt.osm.parallelpbf.entity.Node node) {
            final data.Node internalNodeFormat = new Node(node.getId(), node.getLon(), node.getLat(), 0);

            nodes.add(internalNodeFormat);
        }

        public void addNodesToGraph() {
            for (final Node node : nodes) {
                graph.addVertex(node);
            }
        }
    }

    private class RoadGraphEdgeAdder implements Consumer<Way> {
        private final List<Pair<Long, Long>> edges = Collections.synchronizedList(new LinkedList<>());

        @Override
        public void accept(final Way way) {
            final List<Long> nodeIds = way.getNodes();

            final Iterator<Long> nodeIdIterator = nodeIds.iterator();
            long lastNodeId = nodeIdIterator.next();
            while (nodeIdIterator.hasNext()) {
                final long currentNodeId = nodeIdIterator.next();

                edges.add(new Pair<>(lastNodeId, currentNodeId));
                lastNodeId = currentNodeId;
            }
        }

        public void addEdgesToGraph() {
            for (final Pair<Long, Long> edge : edges) {
                final Node baseNode = graph.getVertex(edge.getFirst());
                final Node adjNode = graph.getVertex(edge.getSecond());

                graph.addEdge(baseNode, adjNode);
            }
        }
    }

    private class NodeRelationAdder implements Consumer<Relation> {
        final Map<Long, Relation> relations = Collections.synchronizedMap(new HashMap<>());

        @Override
        public void accept(final Relation relation) {
            final Map<String, String> tags = relation.getTags();
            if (tags.get("type").equals("boundary") || tags.get("landuse").equals("forest")) {
                relations.put(relation.getId(), relation);
            }
        }

        public List<NodeRelation> getNodeRelations(final RoadGraph graph) {
            final List<NodeRelation> nodeRelations = new LinkedList<>();

            for (final Map.Entry<Long, Relation> relationEntry : relations.entrySet()) {
                final Relation relation = relationEntry.getValue();
                final List<Long> nodeIds = recurseToFindNodes(relation);

                nodeRelations.add(NodeRelation.createFromNodeIds(relation.getId(), relation.getInfo().toString(),
                                                                 relation.getTags(), nodeIds, graph));
            }

            return nodeRelations;
        }

        private List<Long> recurseToFindNodes(final Relation relation) {
            final List<Long> nodeIds = new LinkedList<>();

            final List<RelationMember> members = relation.getMembers();
            for (final RelationMember member : members) {
                final RelationMember.Type type = member.getType();
                if (type == RelationMember.Type.RELATION) {
                    nodeIds.addAll(recurseToFindNodes(relations.get(member.getId())));
                } else if (type == RelationMember.Type.NODE) {
                    nodeIds.add(member.getId());
                }
            }

            return nodeIds;
        }

    }

    private class Completer implements Runnable {
        final StopWatchVerbose sw = new StopWatchVerbose("PBF Read");

        @Override
        public void run() {
            sw.printTimingIfVerbose();
        }
    }
}
