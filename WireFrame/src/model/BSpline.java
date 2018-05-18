package model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class BSpline {
    {
        Random random = new Random();
        color = new Color(random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255));
    }
    private Color color;
    private ArrayList<Point2D> points = new ArrayList<>();
    private ArrayList<Segment> segments = new ArrayList<>();

    static class Segment {

        private final static int LENGTH_COUNT = 20;

        private Point2D[] points;
        private double length;

        Segment(Point2D[] points) {
            this.points = points;
        }

        Point2D getP(double t) {
            double[] k = new double[]{
                    -1 * t*t*t +  3 * t*t + -3 * t + 1,
                    3 * t*t*t + -6 * t*t +  0 * t + 4,
                    -3 * t*t*t +  3 * t*t +  3 * t + 1,
                    1 * t*t*t +  0 * t*t +  0 * t + 0,
            };

            Point2D.Double result = new Point2D.Double();
            for (int i = 0; i < points.length; i++) {
                result.x += points[i].getX() * k[i] / 6;
                result.y += points[i].getY() * k[i] / 6;
            }

            return result;
        }

        double getLength() {
            length = 0;
            double step = 1 / (LENGTH_COUNT);
            Point2D p1 = getP(0);
            Point2D p2 = p1;
            for (int i = 1; i <= LENGTH_COUNT; ++i) {
                p1 = p2;
                p2 = getP(step * i);
                length += p1.distance(p2);
            }

            return length;
        }
    }

    public static BSpline getEmptySpline() {
        BSpline spline = new BSpline ();
        spline.points.clear();
        spline.segments.clear();

        return spline;
    }

    public BSpline () {
        addPoint(new Point2D.Double(-0.5, -0.25));
        addPoint(new Point2D.Double(0.5, -0.5));
        addPoint(new Point2D.Double(-0.5, -0.75));
        addPoint(new Point2D.Double(0.5, -1));
    }

    public void addPoint(Point2D point) {
        points.add(point);

        if (points.size() < 4)
            return;

        Point2D[] segPoints = points.stream()
                .skip(points.size() - 4)
                .toArray(Point2D[]::new);
        segments.add(new Segment(segPoints));
    }

    void removePoint() {
        if (points.size() <= 4)
            return;

        points.remove(points.size() - 1);
        segments.remove(segments.size() - 1);
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public Point2D getPointAtLength(double t) {
        double[] lengths = segments.stream()
                .mapToDouble(Segment::getLength)
                .toArray();

        double pos = t * Arrays.stream(lengths).sum();

        int k = 0;
        while (k < lengths.length - 1 && pos > lengths[k])
            pos -= lengths[k++];

        Segment segment = segments.get(k);
        return segment.getP(pos / lengths[k]);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
