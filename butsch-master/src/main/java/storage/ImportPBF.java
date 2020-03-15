package storage;

import com.wolt.osm.parallelpbf.ParallelBinaryParser;
import com.wolt.osm.parallelpbf.entity.BoundBox;
import com.wolt.osm.parallelpbf.entity.Header;
import com.wolt.osm.parallelpbf.entity.Relation;
import com.wolt.osm.parallelpbf.entity.Way;
import data.Edge;
import data.Node;
import data.RoadGraph;
import evalutation.StopWatchVerbose;
import org.jgrapht.alg.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

        new ParallelBinaryParser(input, 6)
                .onHeader(new HeaderPrinter())
                .onBoundBox(new Consumer<BoundBox>() {
                    @Override
                    public void accept(final BoundBox boundBox) {

                    }
                })
                .onComplete(new Completer())
                .onNode(onNodes)
                .onWay(onWays)
                .onRelation(new RoadGraphRelationAdded())
                .onChangeset(new Consumer<Long>() {
                    @Override
                    public void accept(final Long aLong) {

                    }
                })
                .parse();

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

    private class RoadGraphRelationAdded implements Consumer<Relation> {

        @Override
        public void accept(final Relation relation) {
//            System.out.println("######################");
//            System.out.println(relation.getMembers());
//            System.out.println(relation.getInfo());
//            System.out.println(relation.getTags());
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
