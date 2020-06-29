package storage;

import data.*;
import evalutation.DataInstance;
import geometry.BoundingBox;
import index.GridIndex;
import index.vc.ReflectiveEdge;
import org.locationtech.jts.geom.Coordinate;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataInstanceStorage {
    /**
     *
     * @param instance
     * @param paths graph, vcs, index, relations
     */
    public static void export(final DataInstance instance, final String[] paths) {
        exportGraph(instance.graph, paths[0]);
        exportVcs(instance.index, paths[1]);
        exportIndex(instance.index, paths[2]);
        exportRelations(instance.relations, paths[3]);
    }

    private static void exportGraph(final RoadGraph graph, final String path) {
        try {
            final ExportERPGraph exportERPGraph = new ExportERPGraph(graph, path);
            exportERPGraph.export();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exportVcs(final GridIndex index, final String path) {
        final VCLogger vcLogger = new VCLogger();
        index.queryVisibilityCells(new BoundingBox(-180, 180, -90, 90), vcLogger);

        try {
            final FileWriter fileWriter = new FileWriter(path);
            for (VisibilityCell cell : vcLogger.cells) {
                fileWriter.write(cell.dump() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void exportIndex(final GridIndex index, final String path) {
        final Stream<String> outputStream = index.dump();

        try {
            final FileWriter fileWriter = new FileWriter(path);
            outputStream.forEach(o -> {
                try {
                    fileWriter.write(o + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            });
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void exportRelations(final List<NodeRelation> relations, final String path) {
        try {
            final FileWriter fileWriter = new FileWriter(path);
            for (final NodeRelation relation : relations) {
                fileWriter.write(relation.dump() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static class VCLogger implements GridIndex.GridIndexVisitor<VisibilityCell> {
        private Set<VisibilityCell> cells = new LinkedHashSet<>();

        @Override
        public void accept(final VisibilityCell entity, final BoundingBox cell) {
            accept(entity);
        }

        @Override
        public void accept(final VisibilityCell entity) {
            cells.add(entity);
        }
    }

    public static DataInstance importInstance(final String[] paths) {
        final RoadGraph roadGraph = importGraph(paths[0]);
        final Map<Long, VisibilityCell> visibilityCells = importCells(paths[1], roadGraph);
        final GridIndex gridIndex = importIndex(paths[2], roadGraph, visibilityCells);
        final List<NodeRelation> relations = importRelations(paths[3], roadGraph);

        return DataInstance.createFromData(roadGraph, gridIndex, relations);
    }

    private static RoadGraph importGraph(final String path) {
        try {
            final ImportERPGraph importERPGraph = new ImportERPGraph(path);
            return importERPGraph.createGraph();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    private static Map<Long, VisibilityCell> importCells(final String path, final RoadGraph graph) {
        try {
            final FileReader fileReader = new FileReader(path);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final Map<Long, VisibilityCell> vcs = new HashMap<>();

            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                final String[] parts = line.split("\\|");
                final long id = Long.valueOf(parts[0]);
                final String coordinatesRaw = parts[1];
                final String edgesRaw = parts[2];

                final String[] coordSplit = coordinatesRaw.split(";");
                final Coordinate[] coordinates = Arrays.stream(coordSplit)
                        .map(c -> {
                            final String[] elements = c.split(",");
                            final Double longitude = Double.valueOf(elements[0]);
                            final Double latitude = Double.valueOf(elements[1]);
                            return new Coordinate(longitude, latitude);
                        }).toArray(size -> new Coordinate[coordSplit.length]);

                final List<ReflectiveEdge> edges = Arrays.stream(edgesRaw.split(";"))
                        .map(e -> {
                            final String[] elements = e.split(",");
                            final long edgeId = Long.valueOf(elements[0]);
                            final Node source = graph.getVertex(Long.valueOf(elements[1]));
                            final Node target = graph.getVertex(Long.valueOf(elements[2]));

                            return new ReflectiveEdge(edgeId, source, target, graph);
                        })
                        .collect(Collectors.toList());

                vcs.put(id, VisibilityCell.create(id, coordinates, edges));
            }
            return vcs;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    private static GridIndex importIndex(final String path, final RoadGraph graph, final Map<Long, VisibilityCell> vcs) {
        try {
            final FileReader fileReader = new FileReader(path);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new LinkedList<>();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

            return new GridIndex(graph, lines.iterator(), vcs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    private static List<NodeRelation> importRelations(final String path, final RoadGraph graph) {
        try {
            final FileReader fileReader = new FileReader(path);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            final List<NodeRelation> relations = new LinkedList<>();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                relations.add(NodeRelation.createFromDump(line, graph));
            }

            return relations;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
