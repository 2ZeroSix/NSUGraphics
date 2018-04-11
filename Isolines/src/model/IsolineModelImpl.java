package model;

import model.observers.IsolineModelObserver;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IsolineModelImpl implements MutableIsolineModel {
    Set<IsolineModelObserver> observers = new HashSet<>();

    private FunctionZ function;
    private boolean interpolating;
    private boolean clicking;
    private boolean plot;
    private boolean isolines;
    private boolean grid;
    private boolean gridDots;
    private boolean showValue;
    private List<Color> colors = new ArrayList<>();
    private List<Double> keyValues = new ArrayList<>();
    private Color isolineColor = Color.BLACK;
    private Point.Double cursor;
    private Dimension gridSize = new Dimension(5, 5);
    private Set<Double> userIsolines = new HashSet<>();

    public IsolineModelImpl() {
        setFunction(getDefaultFunctionZ());
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        setKeyValuesCount(2);
        setPlot(true);
        setIsolines(true);
        setShowValue(true);
        setInterpolating(true);
    }

    @Override
    public void setInterpolating(boolean interpolating) {
        this.interpolating = interpolating;
        notifyObservers();
    }

    @Override
    public void setClicking(boolean clicking) {
        this.clicking = clicking;
        notifyObservers();
    }

    @Override
    public void setPlot(boolean plot) {
        this.plot = plot;
        notifyObservers();
    }

    @Override
    public void setIsolines(boolean isolines) {
        this.isolines = isolines;
        notifyObservers();
    }

    @Override
    public void setGrid(boolean grid) {
        this.grid = grid;
        notifyObservers();
    }

    @Override
    public void setGridSize(Dimension gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public void setGridDots(boolean gridDots) {
        this.gridDots = gridDots;
        notifyObservers();
    }

    @Override
    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
        notifyObservers();
    }

    @Override
    public void setColors(List<Color> colors) {
        this.colors = colors;
        notifyObservers();
    }

    @Override
    public void setIsolineColor(Color color) {
        this.isolineColor = color;
    }

    @Override
    public void setFunction(FunctionZ function) {
        this.function = function;
        notifyObservers();
    }

    @Override
    public void setKeyValuesCount(int count) {
        keyValues = new ArrayList<>();
        double step = (getFunction().getMax() - getFunction().getMin()) / (count + 1);
        for (int i = 1; i <= count; ++i) {
            keyValues.add(step * i);
        }
        notifyObservers();
    }

    @Override
    public void setCursor(Point.Double point) {
        this.cursor = point;
    }

    @Override
    public void addUserIsoline(double level) {
        userIsolines.add(level);
    }

    @Override
    public void cleanUserIsolines() {
        userIsolines.clear();
    }

    @Override
    public boolean isInterpolating() {
        return interpolating;
    }

    @Override
    public boolean isClicking() {
        return clicking;
    }

    @Override
    public boolean isPlot() {
        return plot;
    }

    @Override
    public boolean isIsolines() {
        return isolines;
    }

    @Override
    public boolean isGrid() {
        return grid;
    }

    @Override
    public Dimension getGridSize() {
        return gridSize;
    }

    @Override
    public boolean isGridDots() {
        return gridDots;
    }

    @Override
    public boolean isShowValue() {
        return showValue;
    }

    @Override
    public List<Color> getColors() {
        return colors;
    }

    @Override
    public Color getIsolineColor() {
        return isolineColor;
    }

    @Override
    public FunctionZ getFunction() {
        return function;
    }

    @Override
    public List<Double> getKeyValues() {
        return keyValues;
    }

    @Override
    public Point.Double getCursor() {
        return cursor;
    }


    @Override
    public void addObserver(IsolineModelObserver observer) {
        observers.add(observer);
        observer.update(this);
    }

    @Override
    public void removeObserver(IsolineModelObserver observer) {
        observers.remove(this);
    }

    @Override
    public void notifyObservers() {
        for (IsolineModelObserver observer : observers) {
            observer.update(this);
        }
    }

    @Override
    public Set<Double> getUserIsolines() {
        return userIsolines;
    }

    private static FunctionZ getDefaultFunctionZ() {
        return new FunctionZ() {
            @Override
            public Rectangle.Double getBounds() {
                return new Rectangle.Double(0, 0, Math.PI * 2., Math.PI * 2);
            }

            @Override
            public double calc(double x, double y) {
                return Math.sin(x) + Math.cos(y);
            }

            @Override
            public double getMax() {
                return 2.;
            }

            @Override
            public double getMin() {
                return -2;
            }
        };
    }
}
