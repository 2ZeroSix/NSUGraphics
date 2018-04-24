package model;

import java.awt.*;
import java.util.Observable;

public abstract class FunctionZ extends Observable {
    private Rectangle.Double bounds;
    private Dimension grid = new Dimension(0, 0);
    private Point.Double range = new Point.Double(0, 0);

    public Rectangle.Double getBounds() {
        return bounds;
    }

    public synchronized void setBounds(Rectangle.Double bounds) {
        this.bounds = bounds;
        updateRange();
        setChanged();
        notifyObservers();
    }

    public double getMinX() {
        return getBounds().getX();
    }

    public double getMinY() {
        return getBounds().getY();
    }

    public double getMaxX() {
        return getMinX() + getWidth();
    }

    public double getMaxY() {
        return getMinY() + getHeight();
    }

    public double getWidth() {
        return getBounds().getWidth();
    }

    public double getHeight() {
        return getBounds().getHeight();
    }

    public abstract double calc(double x, double y);

    public double calc(Point.Double point) {
        return calc(point.x, point.y);
    }

    public double calcByGrid(Point.Double point) {
        return calcByGrid(point.x, point.y);
    }

    private double calcByGrid(double x, double y) {
        double gridX = getWidth() / (grid.getWidth());
        double gridY = getHeight() / (grid.getHeight());
        double interpolatedValue = 0;
        for (int i = 0; i <= 1; ++i) {
            double posX = ((int)(x / gridX) + i) * gridX;
            double subInterpolatedValue = 0;
            for (int j = 0; j <= 1; ++j) {
                double posY = ((int)(y / gridY) + j) * gridY;
                double coefficient = 1 - Math.abs(posY - y) / Math.abs(gridY);
                subInterpolatedValue += calc(posX, posY) * coefficient;
            }
            double coefficient = 1 - Math.abs(posX - x) / Math.abs(gridX);
            interpolatedValue += subInterpolatedValue * coefficient;
        }
        return interpolatedValue;
    }

    private double calcByGrid(Point p) {
        double gridX = getWidth() / grid.getWidth();
        double gridY = getHeight() / grid.getHeight();
        return calc(p.x * gridX, p.y * gridY);
    }



    public double getMax() {
        return range.y;
    }

    public double getMin() {
        return range.x;
    }

    public double getRange() {
        return getMax() - getMin();
    }

    public Dimension getGrid() {
        return grid;
    }

    public void setGrid(Dimension grid) {
        this.grid = grid;
        updateRange();
    }

    private void updateRange() {
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        double xStep = getWidth() / grid.getWidth();
        double yStep = getHeight() / grid.getHeight();
        if (getWidth() > 0)
            for (double x = getMinX(); x <= getMaxX(); x += xStep) {
                if (getHeight() > 0)
                    for (double y = getMinY(); y < getMaxY(); y += yStep) {
                        double value = calc(x, y);
                        min = Double.min(value, min);
                        max = Double.max(value, max);
                    }
            }
        range.x = min;
        range.y = max;
    }

    public Point.Double cast(Point p, Dimension dimension) {
        return cast(p.x, p.y, dimension);
    }


    public Point.Double cast(int x, int y, Dimension dimension) {
        return new Point.Double(x * getWidth() / dimension.getWidth() + getMinX(),
                getMaxY() - y * getHeight() / dimension.getHeight() );
    }

    public Point cast(Point.Double point, Dimension dimension) {
        return new Point((int) ((point.x - getMinX()) * dimension.getWidth() / getWidth()),
                (int) ((getMaxY() - point.y) * dimension.getHeight() / getHeight()));
    }
}
