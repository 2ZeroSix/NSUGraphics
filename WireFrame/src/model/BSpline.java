package model;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValueBase;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class BSpline extends java.util.Observable {
    {
        Random random = new Random();
        color = new SimpleObjectProperty<>(new Color(random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)));
    }
    final public Property<Color> color;
    final public ListProperty<Property<Point2D>> points = new SimpleListProperty<>(new ObservableListWrapper<>(new ArrayList<>(), param -> new Observable[]{param}));
    final public ListProperty<Segment> segments = new SimpleListProperty<>(new ObservableListWrapper<>(new ArrayList<>()));
    {
        color.addListener((observable, oldValue, newValue) -> {setChanged(); notifyObservers();});
    }


    class Segment {

        private final static int LENGTH_COUNT = 20;

        private int pos;
        private double length;

        Segment(int i) {
            this.pos = i;
        }

        Point2D getP(double t) {
            double[] k = new double[]{
                    -1 * t*t*t +  3 * t*t + -3 * t + 1,
                    3 * t*t*t + -6 * t*t +  0 * t + 4,
                    -3 * t*t*t +  3 * t*t +  3 * t + 1,
                    1 * t*t*t +  0 * t*t +  0 * t + 0,
            };

            Point2D.Double result = new Point2D.Double();
            for (int i = 0; i < 4; i++) {
                result.x += points.get(pos + i).getValue().getX() * k[i] / 6;
                result.y += points.get(pos + i).getValue().getY() * k[i] / 6;
            }

            return result;
        }

        double getLength() {
            length = 0;
            double step = 1. / (LENGTH_COUNT);
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

    public BSpline () {
    }

    public void addPoint(Point2D point) {
        points.add(new SimpleObjectProperty<>(point));
        points.get(points.getSize() - 1).addListener((observable, oldValue, newValue) -> {setChanged(); notifyObservers();});
        if (points.getValue().size() < 4) {
            setChanged(); notifyObservers();
            return;
        }
        int i = points.getValue().size() - 4;
        segments.add(new Segment(i));
        setChanged(); notifyObservers();
    }

    public void removePoint(Point2D p) {
        removePoint(find(p));
    }

    public void removePoint(int i) {
        if (i < 0) return;
        points.remove(i);
        if (segments.size() <= 0) {
            setChanged(); notifyObservers();
            return;

        }
        segments.remove(Integer.max(i - 3, 0));
        for (int j = Integer.max(i - 4, 0); j < segments.size(); ++j) {
            segments.get(j).pos = j;
        }
        setChanged(); notifyObservers();
    }


    public Point2D getPointAtLength(double t) {
        if (segments.size() <= 0) return  new Point2D.Double();
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
        this.color.setValue(color);
    }

    public Color getColor() {
        return color.getValue();
    }

    public int find(Point2D pos) {
        for (int i = 0; i < points.size(); ++i) {
            if (points.get(i).getValue().equals(pos))
                return i;
        }
        return -1;
    }
}
