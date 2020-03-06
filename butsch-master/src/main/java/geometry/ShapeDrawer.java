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

public class ShapeDrawer extends JPanel {
    private final Polygon polygon;
    private static JFrame f;

    public ShapeDrawer(final Polygon polygon) {
        this.polygon = polygon;
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

    public static ShapeDrawer paint(final Polygon polygon) {
        f = new JFrame();
        ShapeDrawer comp = new ShapeDrawer(polygon);
        f.getContentPane().add(comp);
        f.setSize(700, 700);
        f.setVisible(true);

        return comp;
    }

    public void save(final String path, final String fileName) {
        BufferedImage imagebuf = new BufferedImage(2000, 2000, BufferedImage.TYPE_3BYTE_BGR);
//        try {
//            imagebuf = new Robot().createScreenCapture(this.bounds());
//        } catch (AWTException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
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
        f.getContentPane().remove(0);
        f.getContentPane().add(new ShapeDrawer(polygon));
    }

    public void close() {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.dispose();
    }

    public static Polygon reshapePolygon(final Polygon polygon, final double factor) {
        final Coordinate[] coordinates = polygon.getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            Coordinate oldCoordinate = coordinates[i];

            double newX = oldCoordinate.getX() * factor;
            double newY = oldCoordinate.getY() * factor;
            coordinates[i] = new Coordinate(newX, newY);
        }

        return new GeometryFactory().createPolygon(coordinates);
    }
}
