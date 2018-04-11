package model;

import model.observers.IsolineModelObserver;

import java.awt.*;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

public interface IsolineModel {
    boolean isInterpolating();
    boolean isClicking();
    boolean isPlot();
    boolean isIsolines();
    boolean isGrid();
    Dimension getGridSize();
    boolean isGridDots();
    boolean isShowValue();
    List<Color> getColors();
    Color getIsolineColor();
    FunctionZ getFunction();
    List<Double> getKeyValues();
    Point.Double getCursor();

    void addObserver(IsolineModelObserver observer);
    void removeObserver(IsolineModelObserver observer);
    void notifyObservers();

    Set<Double> getUserIsolines();
}
