package storage;


import data.NodeRelation;
import data.RoadGraph;
import data.Node;
import evalutation.Config;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ImportPBFTest {
    @Test
    public void importTest() {
        final ImportPBF importPBF = new ImportPBF(Config.PBF_LUXEMBOURG);

        final RoadGraph graph;
        try {
            graph = importPBF.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
            throw new IllegalStateException("Could not read data in");
        }

        System.out.println(graph.vertexSet().size());
        System.out.println(graph.edgeSet().size());

        // TODO find out how to verify a graph
    }

    @Test
    public void regionFeulenLuxembourg() {
        final long id = 1113660;


        final ImportPBF importPBF = new ImportPBF(Config.PBF_LUXEMBOURG);
        try {
            importPBF.createGraph();
        } catch (FileNotFoundException e) {
            fail();
        }

        final List<NodeRelation> nodeRelations = importPBF.getNodeRelations();
        System.out.println(nodeRelations.size());
//        assertTrue(nodeRelations.contains(new NodeRelation(id, "", Collections.EMPTY_MAP, Collections.EMPTY_LIST)));

        for (final NodeRelation nodeRelation : nodeRelations) {
            if (nodeRelation.id == id) {
                System.out.println("found");
                checkNodes(nodeRelation);
            }
        }
    }

    private void checkNodes(final NodeRelation nodeRelation) {
        final List<Node> nodes = nodeRelation.nodes;
        final List<Node> expectedNodes = getFeulenExpectedNodes();

        Iterator<Node> nodesIterator = nodes.iterator();
        Iterator<Node> expectedNodesIterator = nodes.iterator();

        while (expectedNodesIterator.hasNext()) {
            final Node node = nodesIterator.next();
            final Node expectedNode = expectedNodesIterator.next();
            assertEquals(expectedNode, node);
        }
    }

    private List<Node> getFeulenExpectedNodes() {
        return new ArrayList<Node>(Arrays.asList(new Node(848528636, 0, 0, 0), new Node(1618956487, 0, 0, 0),
                                                 new Node(1618956464, 0, 0, 0), new Node(1618956461, 0, 0, 0),
                                                 new Node(1618956445, 0, 0, 0), new Node(1618956415, 0, 0, 0),
                                                 new Node(1618956401, 0, 0, 0), new Node(1618956392, 0, 0, 0),
                                                 new Node(1618956370, 0, 0, 0), new Node(1618956363, 0, 0, 0),
                                                 new Node(1618956351, 0, 0, 0), new Node(1618956339, 0, 0, 0),
                                                 new Node(1618956333, 0, 0, 0), new Node(1618956320, 0, 0, 0),
                                                 new Node(1618956316, 0, 0, 0), new Node(1618956306, 0, 0, 0),
                                                 new Node(1618956273, 0, 0, 0), new Node(1618956268, 0, 0, 0),
                                                 new Node(1618956265, 0, 0, 0), new Node(1618956249, 0, 0, 0),
                                                 new Node(1618956245, 0, 0, 0), new Node(1618956240, 0, 0, 0),
                                                 new Node(1618956237, 0, 0, 0), new Node(1618956235, 0, 0, 0),
                                                 new Node(1618956233, 0, 0, 0), new Node(1618956221, 0, 0, 0),
                                                 new Node(1618956219, 0, 0, 0), new Node(1618956217, 0, 0, 0),
                                                 new Node(1618956215, 0, 0, 0), new Node(1618956208, 0, 0, 0),
                                                 new Node(1618956200, 0, 0, 0), new Node(1618956192, 0, 0, 0),
                                                 new Node(1618956188, 0, 0, 0), new Node(1618956178, 0, 0, 0),
                                                 new Node(1618956173, 0, 0, 0), new Node(1618956168, 0, 0, 0),
                                                 new Node(1618956166, 0, 0, 0), new Node(1618956161, 0, 0, 0),
                                                 new Node(1618956131, 0, 0, 0), new Node(1618956129, 0, 0, 0),
                                                 new Node(1618956124, 0, 0, 0), new Node(1618956120, 0, 0, 0),
                                                 new Node(1618956116, 0, 0, 0), new Node(1618956115, 0, 0, 0),
                                                 new Node(1618956114, 0, 0, 0), new Node(1618956113, 0, 0, 0),
                                                 new Node(1618956110, 0, 0, 0), new Node(1618956099, 0, 0, 0),
                                                 new Node(1618956093, 0, 0, 0), new Node(1618956091, 0, 0, 0),
                                                 new Node(1618956084, 0, 0, 0), new Node(1618956080, 0, 0, 0),
                                                 new Node(1618956078, 0, 0, 0), new Node(1618956076, 0, 0, 0),
                                                 new Node(1618956049, 0, 0, 0), new Node(1618956046, 0, 0, 0),
                                                 new Node(1618956043, 0, 0, 0), new Node(1618956029, 0, 0, 0),
                                                 new Node(1618955999, 0, 0, 0), new Node(1618955973, 0, 0, 0),
                                                 new Node(1618955959, 0, 0, 0), new Node(1618955937, 0, 0, 0),
                                                 new Node(1618955934, 0, 0, 0), new Node(1618955932, 0, 0, 0),
                                                 new Node(1618955930, 0, 0, 0), new Node(1618955919, 0, 0, 0),
                                                 new Node(1618955918, 0, 0, 0), new Node(1618955915, 0, 0, 0),
                                                 new Node(1618955892, 0, 0, 0), new Node(1618955886, 0, 0, 0),
                                                 new Node(1618955884, 0, 0, 0), new Node(1618955877, 0, 0, 0),
                                                 new Node(1618955875, 0, 0, 0), new Node(1618955871, 0, 0, 0),
                                                 new Node(1618955866, 0, 0, 0), new Node(1618955863, 0, 0, 0),
                                                 new Node(1618955854, 0, 0, 0), new Node(1618955851, 0, 0, 0),
                                                 new Node(1618955847, 0, 0, 0), new Node(1618955843, 0, 0, 0),
                                                 new Node(1618955819, 0, 0, 0), new Node(1618955815, 0, 0, 0),
                                                 new Node(1618955805, 0, 0, 0), new Node(1618955804, 0, 0, 0),
                                                 new Node(1618955802, 0, 0, 0), new Node(1618955794, 0, 0, 0),
                                                 new Node(1618955792, 0, 0, 0), new Node(1618955785, 0, 0, 0),
                                                 new Node(1618955776, 0, 0, 0), new Node(1618955760, 0, 0, 0),
                                                 new Node(1618955755, 0, 0, 0), new Node(1618955754, 0, 0, 0),
                                                 new Node(1618955752, 0, 0, 0), new Node(1618955749, 0, 0, 0),
                                                 new Node(1618955747, 0, 0, 0), new Node(1618955745, 0, 0, 0),
                                                 new Node(1618955739, 0, 0, 0), new Node(1618955698, 0, 0, 0),
                                                 new Node(1618955682, 0, 0, 0), new Node(1618955679, 0, 0, 0),
                                                 new Node(1618955677, 0, 0, 0), new Node(1618955667, 0, 0, 0),
                                                 new Node(1618955664, 0, 0, 0), new Node(1618955663, 0, 0, 0),
                                                 new Node(1618955666, 0, 0, 0), new Node(1618955678, 0, 0, 0),
                                                 new Node(1618955681, 0, 0, 0), new Node(1618955684, 0, 0, 0),
                                                 new Node(1618955695, 0, 0, 0), new Node(1618955711, 0, 0, 0),
                                                 new Node(1618955727, 0, 0, 0), new Node(1618955732, 0, 0, 0),
                                                 new Node(1618955734, 0, 0, 0), new Node(1618955738, 0, 0, 0),
                                                 new Node(1618955736, 0, 0, 0), new Node(1618955729, 0, 0, 0),
                                                 new Node(1618955710, 0, 0, 0), new Node(1618955709, 0, 0, 0),
                                                 new Node(1618955708, 0, 0, 0), new Node(1618955700, 0, 0, 0),
                                                 new Node(1618955696, 0, 0, 0), new Node(1618955694, 0, 0, 0),
                                                 new Node(1618955688, 0, 0, 0), new Node(1618955658, 0, 0, 0),
                                                 new Node(1618955645, 0, 0, 0), new Node(1618955633, 0, 0, 0),
                                                 new Node(1618955639, 0, 0, 0), new Node(1618955637, 0, 0, 0),
                                                 new Node(1618955629, 0, 0, 0), new Node(1618955598, 0, 0, 0),
                                                 new Node(1618955585, 0, 0, 0), new Node(1618955581, 0, 0, 0),
                                                 new Node(1618955565, 0, 0, 0), new Node(1618955562, 0, 0, 0),
                                                 new Node(1618955556, 0, 0, 0), new Node(1618955549, 0, 0, 0),
                                                 new Node(1618955547, 0, 0, 0), new Node(1618955545, 0, 0, 0),
                                                 new Node(1618955530, 0, 0, 0), new Node(1618955527, 0, 0, 0),
                                                 new Node(1618955532, 0, 0, 0), new Node(1618955538, 0, 0, 0),
                                                 new Node(1618955542, 0, 0, 0), new Node(1618955540, 0, 0, 0),
                                                 new Node(1618955536, 0, 0, 0), new Node(1618955525, 0, 0, 0),
                                                 new Node(1618955523, 0, 0, 0), new Node(1618955521, 0, 0, 0),
                                                 new Node(1618955519, 0, 0, 0), new Node(1618955517, 0, 0, 0),
                                                 new Node(1618955510, 0, 0, 0), new Node(1618955508, 0, 0, 0),
                                                 new Node(1618955503, 0, 0, 0), new Node(1618955500, 0, 0, 0),
                                                 new Node(1618955499, 0, 0, 0), new Node(1618955498, 0, 0, 0),
                                                 new Node(1618955495, 0, 0, 0), new Node(1618955485, 0, 0, 0),
                                                 new Node(1618955483, 0, 0, 0), new Node(1618955482, 0, 0, 0),
                                                 new Node(1618955479, 0, 0, 0), new Node(1618955474, 0, 0, 0),
                                                 new Node(1618955469, 0, 0, 0), new Node(1618955468, 0, 0, 0),
                                                 new Node(1618955464, 0, 0, 0), new Node(1618955460, 0, 0, 0),
                                                 new Node(1618955456, 0, 0, 0), new Node(1618955454, 0, 0, 0),
                                                 new Node(1618955452, 0, 0, 0), new Node(1618955450, 0, 0, 0),
                                                 new Node(1618955448, 0, 0, 0), new Node(1618955446, 0, 0, 0),
                                                 new Node(1618955445, 0, 0, 0), new Node(1618955443, 0, 0, 0),
                                                 new Node(1618955442, 0, 0, 0), new Node(1618955441, 0, 0, 0),
                                                 new Node(1618955439, 0, 0, 0), new Node(1618955437, 0, 0, 0),
                                                 new Node(1618955436, 0, 0, 0), new Node(1618955434, 0, 0, 0),
                                                 new Node(1618955432, 0, 0, 0), new Node(1618955431, 0, 0, 0),
                                                 new Node(1618955429, 0, 0, 0), new Node(1618955428, 0, 0, 0),
                                                 new Node(1618955426, 0, 0, 0), new Node(1618955424, 0, 0, 0),
                                                 new Node(1618955422, 0, 0, 0), new Node(1618955419, 0, 0, 0),
                                                 new Node(1618955418, 0, 0, 0), new Node(1618955417, 0, 0, 0),
                                                 new Node(1618955416, 0, 0, 0), new Node(1618955414, 0, 0, 0),
                                                 new Node(1618955412, 0, 0, 0), new Node(1618955411, 0, 0, 0),
                                                 new Node(1618955408, 0, 0, 0), new Node(1618955407, 0, 0, 0),
                                                 new Node(1618955402, 0, 0, 0), new Node(1618955393, 0, 0, 0),
                                                 new Node(1618955381, 0, 0, 0), new Node(1618955380, 0, 0, 0),
                                                 new Node(1618955375, 0, 0, 0), new Node(1618955383, 0, 0, 0),
                                                 new Node(1618955379, 0, 0, 0), new Node(1618955377, 0, 0, 0),
                                                 new Node(1618955364, 0, 0, 0), new Node(1618955358, 0, 0, 0),
                                                 new Node(1618955347, 0, 0, 0), new Node(1618955331, 0, 0, 0),
                                                 new Node(1618955324, 0, 0, 0), new Node(1618955321, 0, 0, 0),
                                                 new Node(1618955316, 0, 0, 0), new Node(1618955310, 0, 0, 0),
                                                 new Node(1618955307, 0, 0, 0), new Node(1618955296, 0, 0, 0),
                                                 new Node(1618955297, 0, 0, 0), new Node(1618955298, 0, 0, 0),
                                                 new Node(1618955299, 0, 0, 0), new Node(1618955301, 0, 0, 0),
                                                 new Node(1618955302, 0, 0, 0), new Node(1618955308, 0, 0, 0),
                                                 new Node(1618955315, 0, 0, 0), new Node(1618955317, 0, 0, 0),
                                                 new Node(1618955320, 0, 0, 0), new Node(1618955326, 0, 0, 0),
                                                 new Node(1618955350, 0, 0, 0), new Node(1618955344, 0, 0, 0),
                                                 new Node(1618955309, 0, 0, 0), new Node(1618955292, 0, 0, 0),
                                                 new Node(1618955283, 0, 0, 0), new Node(1618955275, 0, 0, 0),
                                                 new Node(1618955270, 0, 0, 0), new Node(1618955260, 0, 0, 0),
                                                 new Node(1618955251, 0, 0, 0), new Node(1618955248, 0, 0, 0),
                                                 new Node(1618955243, 0, 0, 0), new Node(1618955241, 0, 0, 0),
                                                 new Node(1618955239, 0, 0, 0), new Node(1618955236, 0, 0, 0),
                                                 new Node(1618955235, 0, 0, 0), new Node(1618955233, 0, 0, 0),
                                                 new Node(1618955231, 0, 0, 0), new Node(1618955228, 0, 0, 0),
                                                 new Node(1618955220, 0, 0, 0), new Node(1618955214, 0, 0, 0),
                                                 new Node(1618955213, 0, 0, 0), new Node(1618955204, 0, 0, 0),
                                                 new Node(1618955186, 0, 0, 0), new Node(1618955181, 0, 0, 0),
                                                 new Node(1618955178, 0, 0, 0), new Node(1618955175, 0, 0, 0),
                                                 new Node(1618955173, 0, 0, 0), new Node(1618955169, 0, 0, 0),
                                                 new Node(1618955167, 0, 0, 0), new Node(1618955164, 0, 0, 0),
                                                 new Node(1618955160, 0, 0, 0), new Node(1618955158, 0, 0, 0),
                                                 new Node(1618955153, 0, 0, 0), new Node(1618955151, 0, 0, 0),
                                                 new Node(1618955149, 0, 0, 0), new Node(1618955148, 0, 0, 0),
                                                 new Node(1618955133, 0, 0, 0), new Node(1618955132, 0, 0, 0),
                                                 new Node(1618955131, 0, 0, 0), new Node(1618955119, 0, 0, 0),
                                                 new Node(1618955117, 0, 0, 0), new Node(1618955116, 0, 0, 0),
                                                 new Node(1618955112, 0, 0, 0), new Node(1618955104, 0, 0, 0),
                                                 new Node(1618955136, 0, 0, 0), new Node(1618955172, 0, 0, 0),
                                                 new Node(1618955180, 0, 0, 0), new Node(1618955184, 0, 0, 0),
                                                 new Node(1618955188, 0, 0, 0), new Node(1618955194, 0, 0, 0),
                                                 new Node(1618955205, 0, 0, 0), new Node(1618955198, 0, 0, 0),
                                                 new Node(1618955217, 0, 0, 0), new Node(1618955216, 0, 0, 0),
                                                 new Node(1618955211, 0, 0, 0), new Node(1618955208, 0, 0, 0),
                                                 new Node(1618955195, 0, 0, 0), new Node(1618955189, 0, 0, 0),
                                                 new Node(1618955192, 0, 0, 0), new Node(1618955187, 0, 0, 0),
                                                 new Node(1618955185, 0, 0, 0), new Node(1618955171, 0, 0, 0),
                                                 new Node(1618955168, 0, 0, 0), new Node(1618955152, 0, 0, 0),
                                                 new Node(1618955135, 0, 0, 0), new Node(1618955115, 0, 0, 0),
                                                 new Node(1618955113, 0, 0, 0), new Node(1618955105, 0, 0, 0),
                                                 new Node(1618955098, 0, 0, 0), new Node(1618955096, 0, 0, 0),
                                                 new Node(1618955091, 0, 0, 0), new Node(1618955087, 0, 0, 0),
                                                 new Node(1618955083, 0, 0, 0), new Node(1618955075, 0, 0, 0),
                                                 new Node(1618955072, 0, 0, 0), new Node(1618955071, 0, 0, 0),
                                                 new Node(1618955057, 0, 0, 0), new Node(1618955046, 0, 0, 0),
                                                 new Node(1618955047, 0, 0, 0), new Node(1618955050, 0, 0, 0),
                                                 new Node(1618955052, 0, 0, 0), new Node(1618955054, 0, 0, 0),
                                                 new Node(1618955055, 0, 0, 0), new Node(1618955056, 0, 0, 0),
                                                 new Node(1618955058, 0, 0, 0), new Node(1618955061, 0, 0, 0),
                                                 new Node(1618955064, 0, 0, 0), new Node(1618955066, 0, 0, 0),
                                                 new Node(1618955069, 0, 0, 0), new Node(1618955070, 0, 0, 0),
                                                 new Node(1618955067, 0, 0, 0), new Node(1618955065, 0, 0, 0),
                                                 new Node(1618955062, 0, 0, 0), new Node(1618955059, 0, 0, 0),
                                                 new Node(1618955051, 0, 0, 0), new Node(1618955048, 0, 0, 0),
                                                 new Node(1618955043, 0, 0, 0), new Node(1618955041, 0, 0, 0),
                                                 new Node(1618955040, 0, 0, 0), new Node(1618955037, 0, 0, 0),
                                                 new Node(1618955035, 0, 0, 0), new Node(1618955031, 0, 0, 0),
                                                 new Node(1618955027, 0, 0, 0), new Node(1618955026, 0, 0, 0),
                                                 new Node(1618955024, 0, 0, 0), new Node(1618955020, 0, 0, 0),
                                                 new Node(1618955015, 0, 0, 0), new Node(1618955011, 0, 0, 0),
                                                 new Node(1618955007, 0, 0, 0), new Node(1618955005, 0, 0, 0),
                                                 new Node(1618955003, 0, 0, 0), new Node(1618954999, 0, 0, 0),
                                                 new Node(1618954991, 0, 0, 0), new Node(1618954988, 0, 0, 0),
                                                 new Node(848546242, 0, 0, 0)));
    }
}
