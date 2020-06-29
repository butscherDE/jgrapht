package visualizations;

import data.Edge;
import data.Node;
import data.RoadGraph;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GeometryVisualizer {
    public static final Color BACKGROUND = Color.WHITE;
    public static final Color NODES = new Color(0, 0, 255);
    public static final int MARGIN = 10;
    public static final int NODE_SIZE = 4;

    private final GeometryDrawCollection geometryDrawCollection;
    private Graphics2D g2d;
    private ScaledCoordinates scaledCoordinates;
    private double[] minMax;
    private JFrame frame;

    public GeometryVisualizer(final GeometryDrawCollection geometryDrawCollection) {
        this.geometryDrawCollection = geometryDrawCollection;
    }

    public void visualizeGraph() {
        visualizeGraph(0);
    }

    public void visualizeGraph(final long millisToSleep) {
        this.geometryDrawCollection.inverseY();
        // compute ranges of X and Y coordinates
        minMax = extractMinMax();
        final double spreadX = minMax[1] - minMax[0];
        final double spreadY = minMax[3] - minMax[2];
        final double originalRatio = spreadX / spreadY;

        // create window
        frame = new JFrame(GeometryVisualizer.class.getSimpleName());

        // panel which contains the actual graph
        final JPanel main = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                // white background
                g.setColor(BACKGROUND);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());

                // enable antialiasing
                g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // leave some gap around the edges
                final int w = this.getWidth() - 2 * MARGIN;
                final int h = this.getHeight() - 2 * MARGIN;
                final double ratio = 1.0 * w / h;

                // scale the coordinates so that they fit into the remaining rectangle
                scaledCoordinates = new ScaledCoordinates(w, h, ratio, originalRatio, spreadY, spreadX).invoke();

                drawCoordinates();
                drawLineSegments();
                drawCoordinates();

            }
        };

        main.setPreferredSize(new Dimension(1000, 1000));
        frame.setContentPane(main);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try {
            Thread.sleep(millisToSleep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.geometryDrawCollection.inverseY();
    }

    private void drawCoordinates() {
        g2d.setColor(NODES);
        for (final Map.Entry<Color, Collection<Node>> colorCollectionEntry : geometryDrawCollection.coordinates.entrySet()) {
            final Color color = colorCollectionEntry.getKey();
            final Collection<Node> nodes = colorCollectionEntry.getValue();

            g2d.setColor(color);
            for (Node node : nodes) {
                final double x = (node
                                          .getPoint()
                                          .getCoordinate().x - minMax[0]) * scaledCoordinates.scale + scaledCoordinates.padX;
                final double y = (node
                                          .getPoint()
                                          .getCoordinate().y - minMax[2]) * scaledCoordinates.scale + scaledCoordinates.padY;
                g2d.fill(new Ellipse2D.Double(x - NODE_SIZE / 2d, y - NODE_SIZE / 2d, NODE_SIZE, NODE_SIZE));
                g2d.drawString(String.valueOf(node.id), (float) x, (float) y);
            }
        }
    }

    private void drawLineSegments() {
        drawNormalLineSegments();
    }

    private void drawNormalLineSegments() {
        for (final Map.Entry<Color, Collection<LineSegment>> colorCollectionEntry : geometryDrawCollection.lineSegments.entrySet()) {
            final Color color = colorCollectionEntry.getKey();
            final Collection<LineSegment> lineSegments = colorCollectionEntry.getValue();

            g2d.setColor(color);
            for (LineSegment line : lineSegments) {
                drawLineSegment(line);
            }
        }
    }

    private void drawLineSegment(LineSegment line) {
        final Coordinate startCoordinate = line.getCoordinate(0);
        final Coordinate endCoordinate = line.getCoordinate(1);
        final double xi = (startCoordinate.getX() - minMax[0]) * scaledCoordinates.scale + scaledCoordinates.padX;
        final double yi = (startCoordinate.getY() - minMax[2]) * scaledCoordinates.scale + scaledCoordinates.padY;
        final double xj = (endCoordinate.getX() - minMax[0]) * scaledCoordinates.scale + scaledCoordinates.padX;
        final double yj = (endCoordinate.getY() - minMax[2]) * scaledCoordinates.scale + scaledCoordinates.padY;
        g2d.draw(new Line2D.Double(xi, yi, xj, yj));
    }

    private double[] extractMinMax() {
        double[] minMax = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};

        for (final Collection<Node> nodes : geometryDrawCollection.coordinates.values()) {
            for (Node node : nodes) {
                updateMinMaxWithCoordinate(minMax, node.getPoint().getCoordinate());
            }
        }

        for (final Collection<LineSegment> lineSegments : geometryDrawCollection.lineSegments.values()) {
            for (final LineSegment lineSegment : lineSegments) {
                updateMinMaxWithCoordinate(minMax, lineSegment.p0);
                updateMinMaxWithCoordinate(minMax, lineSegment.p1);
            }
        }

        return minMax;
    }

    private void updateMinMaxWithCoordinate(final double[] minMax, final Coordinate coordinate) {
        minMax[0] = Math.min(minMax[0], coordinate.getX());
        minMax[1] = Math.max(minMax[1], coordinate.getX());
        minMax[2] = Math.min(minMax[2], coordinate.getY());
        minMax[3] = Math.max(minMax[3], coordinate.getY());
    }

    public void save(final String path) {
        try
        {
            BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            frame.paint(graphics2D);
            ImageIO.write(image,"jpeg", new File(path));
        }
        catch(Exception exception)
        {
            //code
        }
    }

    private static class ScaledCoordinates {
        private final int w;
        private final int h;
        private final double ratio;
        private final double originalRatio;
        private final double spreadY;
        private final double spreadX;
        public double padX;
        public double padY;
        public double scale;

        public ScaledCoordinates(int w, int h, double ratio, double originalRatio, double spreadY, double spreadX) {
            this.w = w;
            this.h = h;
            this.ratio = ratio;
            this.originalRatio = originalRatio;
            this.spreadY = spreadY;
            this.spreadX = spreadX;
        }

        public ScaledCoordinates invoke() {
            if (originalRatio < ratio) {
                scale = h / spreadY;
                padX = (w - scale * spreadX) / 2 + MARGIN;
                padY = MARGIN;
            } else {
                scale = w / spreadX;
                padX = MARGIN;
                padY = (h - scale * spreadY) / 2 + MARGIN;
            }
            return this;
        }
    }

    public static class GeometryDrawCollection {
        private final HashMap<Color, Collection<Node>> coordinates = new HashMap<>();
        private final HashMap<Color, Collection<LineSegment>> lineSegments = new HashMap<>();

        public GeometryDrawCollection() {
        }

        public void inverseY() {
            coordinates.forEach((a, b) -> {
                final List<Node> nodesForColor = b.stream().collect(Collectors.toList());
//                coordinates.remove(a);
                final ArrayList<Node> newList = new ArrayList<>(nodesForColor.size());
                coordinates.put(a, newList);
                nodesForColor.forEach(n -> newList.add(new Node(n.id, n.longitude, n.latitude * (-1), n.elevation)));

            });

            lineSegments.forEach((a, b) -> {
                b.forEach(e -> {
                    e.p0.y *= -1;
                    e.p1.y *= -1;
                });
            });
        }

        public void addGraph(final Color color, final RoadGraph graph) {
            graph.vertexSet().stream().filter(a -> a.id >= 0).forEach(a -> addNode(color, a));
            graph.edgeSet().stream().forEach(a -> {
                final Node edgeSource = graph.getEdgeSource(a);
                final Node edgeTarget = graph.getEdgeTarget(a);

                if (edgeSource.id >= 0 && edgeTarget.id >= 0) {
                    final Coordinate startCoordinate = edgeSource.getPoint().getCoordinate();
                    final Coordinate endCoordinate = edgeTarget.getPoint().getCoordinate();
                    final LineSegment edgeAsLineSegment = new LineSegment(startCoordinate, endCoordinate);


                    addLineSegment(color, edgeAsLineSegment);
                }
            });
        }

        public void addPolygon(final Color color, final org.locationtech.jts.geom.Polygon polygon) {
            final Coordinate[] coordinates = polygon.getCoordinates();
            final List<LineSegment> lineSegments = new ArrayList<>(coordinates.length);
            for (int i = 0; i < coordinates.length - 1; i++) {
                lineSegments.add(new LineSegment(coordinates[i], coordinates[i + 1]));
            }

            addLineSegments(color, lineSegments);
        }

        public void addNode(final Color color, final Node coordinate) {
            addCoordinates(color, Collections.singletonList(coordinate));
        }

        public void addCoordinates(final Color color, final Collection<Node> coordinate) {
            Collection<Node> nodes = this.coordinates.get(color);

            if (nodes == null) {
                this.coordinates.put(color, new LinkedList<>());
                nodes = this.coordinates.get(color);
            }

            nodes.addAll(coordinate);
        }

        public void addLineSegment(final Color color, final LineSegment segment) {
            addLineSegments(color, Collections.singletonList(segment));
        }

        public void addLineSegments(final Color color, final Collection<LineSegment> segments) {
            Collection<LineSegment> lineSegments = this.lineSegments.get(color);

            if (lineSegments == null) {
                this.lineSegments.put(color, new LinkedList<>());
                lineSegments = this.lineSegments.get(color);
            }

            lineSegments.addAll(segments);
        }

        public void addLineSegmentsFromCoordinates(final Color color, final Collection<Coordinate> coordinates) {
            if (coordinates.size() < 2) {
                throw new IllegalArgumentException("Cannot create line segments from less than 2 coordinates");
            }

            final Collection<LineSegment> lineSegments = coordinatesToLineSegments(coordinates);

            addLineSegments(color, lineSegments);
        }

        public void addEdge(final Color color, final Edge edge, final RoadGraph graph) {
            final Node edgeSource = graph.getEdgeSource(edge);
            final Node edgeTarget = graph.getEdgeTarget(edge);

            final Coordinate sourceCoordinate = edgeSource.getPoint().getCoordinate();
            final Coordinate targetCoordinate = edgeTarget.getPoint().getCoordinate();
            final LineSegment lineSegment = new LineSegment(sourceCoordinate, targetCoordinate);

            addLineSegment(color, lineSegment);
        }

        private Collection<LineSegment> coordinatesToLineSegments(final Collection<Coordinate> coordinates) {
            final Collection<LineSegment> lineSegments = new LinkedList<>();

            final Iterator<Coordinate> coordinateIterator = coordinates.iterator();
            Coordinate lastCoordinate = coordinateIterator.next();
            while (coordinateIterator.hasNext()) {
                final Coordinate nextCoordinate = coordinateIterator.next();
                lineSegments.add(new LineSegment(lastCoordinate, nextCoordinate));
                lastCoordinate = nextCoordinate;
            }

            return lineSegments;
        }
    }
}
