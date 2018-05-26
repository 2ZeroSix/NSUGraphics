package view.panels;

import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import model.BSpline;
import model.Matrix4x4;
import model.SceneSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static javax.management.Query.value;

public class SplineEditor extends JPanel implements MouseListener, MouseMotionListener {
    private final SceneSettings settings;

    public BSpline getSpline() {
        return spline;
    }

    private final BSpline spline;
    private final double step = 0.05;
    private double squareFieldSize = 1;
    private final Point2D.Double fieldSize = new Point2D.Double(squareFieldSize, squareFieldSize);
    private final int radius = 5;
    private Property<Point2D> selected = null;


    public SplineEditor(SceneSettings settings, BSpline spline) {
        this.settings = settings;
        this.spline = spline;
        settings.a.addListener((observable, oldValue, newValue) -> repaint());
        settings.b.addListener((observable, oldValue, newValue) -> repaint());
        settings.legendColor.addListener((observable, oldValue, newValue) -> repaint());
        settings.pointsColor.addListener((observable, oldValue, newValue) -> repaint());
        settings.selectColor.addListener((observable, oldValue, newValue) -> repaint());
        settings.backgroundColor.addListener((observable, oldValue, newValue) -> repaint());
        spline.color.addListener((observable, oldValue, newValue) -> repaint());
        spline.points.addListener((ListChangeListener<? super Property<Point2D>>) (c) -> repaint());
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private Point2D cast(Point g2) {
        return new Point2D.Double((g2.x) * (2 * fieldSize.x) / (getWidth()) - fieldSize.x,
                -(g2.y) * (2 * fieldSize.y) / (getHeight()) + fieldSize.y);
    }

    private Point cast(Point2D g2) {
        return new Point((int) (((g2.getX() + fieldSize.x) / (2 * fieldSize.x)) * (getWidth())),
                (int) ((-g2.getY() + fieldSize.y) / (2 * fieldSize.y) * (getHeight())));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        updateFieldSize();
        paintLegend(g2);
        paintLines(g2);
        paintBSpline(g2);
        paintPoints(g2);
    }

    private void updateFieldSize() {
        double wMult = getWidth() / (double)Integer.min(getWidth(), getHeight());
        double hMult = getHeight() / (double)Integer.min(getWidth(), getHeight());
        squareFieldSize = 1;
        spline.points.stream().map(Property::getValue).forEach(p -> squareFieldSize = Double.max(squareFieldSize, Double.max(Math.abs(p.getY() / hMult), Math.abs(p.getX() / wMult)) * 1.2));
        fieldSize.x = squareFieldSize * wMult;
        fieldSize.y = squareFieldSize * hMult;
    }

    private void paintLines(Graphics2D g2) {
        g2.setColor(new Color((settings.legendColor.getValue().getRGB() | 0xFF000000) & 0x7FFFFFFF, true));
        spline.points.stream().map(Property::getValue).map(this::cast).reduce((start, end) -> {
            g2.drawLine(start.x, start.y, end.x, end.y);
            return end;
        });
    }

    private void paintLegend(Graphics2D g2) {
        g2.setColor(settings.backgroundColor.getValue());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(settings.legendColor.getValue());
        g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
        Rectangle2D xSize = g2.getFontMetrics().getStringBounds(String.format("%.3f", fieldSize.x), g2);
        Rectangle2D ySize = g2.getFontMetrics().getStringBounds(String.format("%.3f", fieldSize.y), g2);
        g2.drawString(String.format("-%.3f", fieldSize.x), 2, getHeight() / 2 - 2);
        g2.drawString(String.format("-%.3f", fieldSize.y), getWidth() / 2 + 2, getHeight() - 3);
        g2.drawString(String.format("%.3f", fieldSize.x), (int) (getWidth() - 1 - xSize.getWidth()), getHeight() / 2 - 2);
        g2.drawString(String.format("%.3f", fieldSize.y), getWidth() / 2 + 2, (int) ySize.getHeight());
    }

    private void paintBSpline(Graphics2D g2) {
        if (spline.points.size() < 4) return;
        Color color = spline.getColor();
        Color negative = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        g2.setColor(negative);
        double step = this.step / (spline.points.size() - 2);
        for (double pos = 0; pos < settings.a.get(); pos += step) {
            Point start = cast(spline.getPointAtLength(pos));
            Point end = cast(spline.getPointAtLength(pos + step));
            g2.drawLine(start.x, start.y, end.x, end.y);
        }
        for (double pos = settings.b.get(); pos < 1; pos += step) {
            Point start = cast(spline.getPointAtLength(pos));
            Point end = cast(spline.getPointAtLength(pos + step));
            g2.drawLine(start.x, start.y, end.x, end.y);
        }
        g2.setColor(color);
        for (double pos = settings.a.get(); pos < settings.b.get(); pos += step) {
            Point start = cast(spline.getPointAtLength(pos));
            Point end = cast(spline.getPointAtLength(pos + step));
            g2.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    private void paintPoints(Graphics2D g2) {
        g2.setColor(settings.pointsColor.getValue());
        int diameter = this.radius * 2;
        for (Property<Point2D> point : spline.points) {
            Point center = cast(point.getValue());
            if (point.equals(selected)) {
                g2.setColor(settings.selectColor.getValue());
                g2.drawOval(center.x - diameter / 2, center.y - diameter / 2, diameter, diameter);
                g2.setColor(settings.pointsColor.getValue());
            } else {
                g2.drawOval(center.x - diameter / 2, center.y - diameter / 2, diameter, diameter);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            spline.addPoint(cast(e.getPoint()));
        } else if (e.getButton() == MouseEvent.BUTTON3 && selected != null) {
            spline.removePoint(selected.getValue());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK && selected != null) {
            Point p = new Point(Integer.min(getWidth() - 1, Integer.max(0, e.getX())), Integer.min(getHeight() - 1, Integer.max(0, e.getY())));
            selected.setValue(cast(p));
        } else {
            mouseMoved(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        selected = null;
        int distance = radius;
        for (Property<Point2D> p : spline.points) {
            int local_distance = (int) e.getPoint().distance(cast(p.getValue()));
            if (local_distance <= distance) {
                selected = p;
                distance = local_distance;
            }
        }
        repaint();
    }
}
