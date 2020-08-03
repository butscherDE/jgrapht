package geometry;

import org.locationtech.jts.awt.ShapeWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ShapeDrawer extends JPanel {
    private static JFrame f;
    private final Polygon polygon;

    public ShapeDrawer(final Polygon polygon) {
        this.polygon = polygon;
    }

    public static ShapeDrawer paint(final Polygon polygon) {
        f = new JFrame();
        ShapeDrawer comp = new ShapeDrawer(polygon);
        f.getContentPane()
                .add(comp);
        f.setSize(700, 700);
        f.setVisible(true);

        return comp;
    }

    public static Polygon reshapePolygon(final Polygon polygon, final double factor) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        final double minX = Arrays.stream(coordinates)
                .min(Comparator.comparingDouble(a -> a.x))
                .get().x;
        final double maxX = Arrays.stream(coordinates)
                .max(Comparator.comparingDouble(a -> a.x))
                .get().x - minX;
        final double minY = Arrays.stream(coordinates)
                .min(Comparator.comparingDouble(a -> a.y))
                .get().y;
        final double maxY = Arrays.stream(coordinates)
                .max(Comparator.comparingDouble(a -> a.y))
                .get().y - minY;

        for (int i = 0; i < coordinates.length; i++) {
            final Coordinate coordinate = coordinates[i];
            final double newX = ((coordinate.x - minX) / maxX) * (factor * 0.9) + (factor * 0.05);
            final double newY = ((coordinate.y - minY) / maxY) * (factor * 0.9) + (factor * 0.05);
            coordinates[i] = new Coordinate(newX, newY);
            if (coordinate.x > 2000) {
                System.out.println(coordinate.x);
                System.exit(-1);
            }
        }

        return new GeometryFactory().createPolygon(coordinates);
    }

    public void paint(final Graphics g) {
        ShapeWriter sw = new ShapeWriter();
        Shape polyShape = sw.toShape(polygon);

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.draw(polyShape);

//        drawCoordinates(graphics2D);
    }

    private void drawCoordinates(final Graphics2D graphics2D) {
        Coordinate[] coordinates = polygon.getCoordinates();
        for (int i = 0; i < coordinates.length - 1; i++) {
            drawCoordinateMark(graphics2D, coordinates, i);
        }
    }

    private void drawCoordinateMark(Graphics2D graphics2D, final Coordinate[] coordinates, final int i) {
        Coordinate coordinate = coordinates[i];
        graphics2D.drawString(i + ": " + coordinate.toString(), (float) coordinate.getX(), (float) coordinate.getY());
    }

    public void save(final String path, final String fileName) {
        BufferedImage imagebuf = new BufferedImage(2000, 2000, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = imagebuf.createGraphics();

        this.paint(graphics2D);
        try {
            ImageIO.write(imagebuf, "jpeg", new File(path + fileName + ".jpeg"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("error");
        }
    }

    public void repaint(final Polygon polygon) {
        f.getContentPane()
                .remove(0);
        f.getContentPane()
                .add(new ShapeDrawer(polygon));
    }

    public void close() {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.dispose();
    }
}
