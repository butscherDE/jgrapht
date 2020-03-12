package visualizations;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Collection;

public class GeometryVisualizer {
	public static final Color BACKGROUND = Color.WHITE;
	public static final Color EDGES = Color.BLACK;
	private static final Color EDGES_HIGHLIGHTET = Color.RED;
	public static final Color NODES = new Color(0, 0, 255);
	public static final int MARGIN = 10;
	public static final int NODE_SIZE = 4;

	private final Collection<Coordinate> coordinates;
	private final Collection<LineSegment> lineSegments;
	private final Collection<LineSegment> highlightedSegments;
	private Graphics2D g2d;
	private ScaledCoordinates scaledCoordinates;
	private double[] minMax;

	public GeometryVisualizer(Collection<Coordinate> coordinates, Collection<LineSegment> lineSegments, Collection<LineSegment> highlightedSegments) {
		this.coordinates = coordinates;
		this.lineSegments = lineSegments;
		this.highlightedSegments = highlightedSegments;
	}

	public void visualizeGraph() {
		// compute ranges of X and Y coordinates
		minMax = extractMinMax();
		final double spreadX = minMax[1] - minMax[0];
		final double spreadY = minMax[3] - minMax[2];
		final double originalRatio = spreadX / spreadY;

		// create window
		final JFrame frame = new JFrame(GeometryVisualizer.class.getSimpleName());

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
				System.out.println(scaledCoordinates.scale);

			}
		};

		main.setPreferredSize(new Dimension(1000, 1000));
		frame.setContentPane(main);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void drawCoordinates() {
		g2d.setColor(NODES);
		for (Coordinate coordinate : coordinates) {
			final double x = (coordinate.getX() - minMax[0]) * scaledCoordinates.scale + scaledCoordinates.padX;
			final double y = (coordinate.getY() - minMax[2]) * scaledCoordinates.scale + scaledCoordinates.padY;
			g2d.fill(new Ellipse2D.Double(x - NODE_SIZE / 2, y - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE));
//			g2d.drawString(coordinate.id + ":" + coordinate.level, (float) x, (float) y);
		}
	}

	private void drawLineSegments() {
		drawNormalLineSegments();
		drawHighlightedLineSegments();
//		g2d.setColor(EDGES);
	}

	private void drawNormalLineSegments() {
		for (LineSegment line : lineSegments) {
			drawLineSegment(line, EDGES);
		}
	}

	private void drawHighlightedLineSegments() {
		for (final LineSegment line : highlightedSegments) {
			drawLineSegment(line, EDGES_HIGHLIGHTET);
		}
	}

	private void drawLineSegment(LineSegment line, Color color) {
		g2d.setColor(color);

		final Coordinate startCoordinate = line.getCoordinate(0);
		final Coordinate endCoordinate = line.getCoordinate(1);
		final double xi = (startCoordinate.getX() - minMax[0]) * scaledCoordinates.scale + scaledCoordinates.padX;
		final double yi = (startCoordinate.getY() - minMax[2]) * scaledCoordinates.scale + scaledCoordinates.padY;
		final double xj = (endCoordinate.getX() - minMax[0]) * scaledCoordinates.scale + scaledCoordinates.padX;
		final double yj = (endCoordinate.getY() - minMax[2]) * scaledCoordinates.scale + scaledCoordinates.padY;
		g2d.draw(new Line2D.Double(xi, yi, xj, yj));
	}

	private double[] extractMinMax() {
		double[] minMax = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE };

		for (Coordinate coordinate : coordinates) {
			updateMinMaxWithCoordinate(minMax, coordinate);
		}

		for (final LineSegment lineSegment : lineSegments) {
			updateMinMaxWithCoordinate(minMax, lineSegment.p0);
			updateMinMaxWithCoordinate(minMax, lineSegment.p1);
		}

		for (final LineSegment lineSegment : highlightedSegments) {
			updateMinMaxWithCoordinate(minMax, lineSegment.p0);
			updateMinMaxWithCoordinate(minMax, lineSegment.p1);
		}

		return minMax;
	}

	private void updateMinMaxWithCoordinate(final double[] minMax, final Coordinate coordinate) {
		minMax[0] = Math.min(minMax[0], coordinate.getX());
		minMax[1] = Math.max(minMax[1], coordinate.getX());
		minMax[2] = Math.min(minMax[2], coordinate.getY());
		minMax[3] = Math.max(minMax[3], coordinate.getY());
	}

	private static class ScaledCoordinates {
		private int w;
		private int h;
		private double ratio;
		private double originalRatio;
		private double spreadY;
		private double spreadX;
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

		public double getPadX() {
			return padX;
		}

		public double getPadY() {
			return padY;
		}

		public double getScale() {
			return scale;
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
}
