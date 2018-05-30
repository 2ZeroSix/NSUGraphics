package life_hexagon.model;

import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.view.observers.DisplayModelObserver;

import java.util.HashSet;
import java.util.Set;

public class DisplayModel implements MutableDisplayModelObservable{
    private int borderWidth;
    private int hexagonSize;
    private boolean displayImpact;
    private boolean fullColor;
    private Set<DisplayModelObserver> observers = new HashSet<>();

    public DisplayModel(int borderWidth, int hexagonSize, boolean displayImpact, boolean fullColor) {
        this.borderWidth = borderWidth;
        this.hexagonSize = hexagonSize;
        this.displayImpact = displayImpact;
        this.fullColor = fullColor;
    }

    @Override
    public MutableDisplayModelObservable setBorderWidth(int width) {
        if (this.borderWidth != width) {
            this.borderWidth = width;
            notifyBorderWidth();
        }
        return this;
    }

    @Override
    public MutableDisplayModelObservable setHexagonSize(int size) {
        if (this.hexagonSize != size) {
            this.hexagonSize = size;
            notifyHexagonSize();
        }
        return this;
    }

    @Override
    public int getBorderWidth() {
        return borderWidth;
    }

    @Override
    public int getHexagonSize() {
        return hexagonSize;
    }

    @Override
    public boolean isDisplayImpact() {
        return displayImpact;
    }

    @Override
    public MutableDisplayModelObservable setDisplayImpact(boolean displayImpact) {
        if (displayImpact != this.displayImpact) {
            this.displayImpact = displayImpact;
            notifyDisplayImpact();
        }
        return this;
    }

    @Override
    public void notifyBorderWidth() {
        for (DisplayModelObserver observer : observers) {
            observer.updateBorderWidth(this);
        }
    }

    @Override
    public void notifyHexagonSize() {
        for (DisplayModelObserver observer : observers) {
            observer.updateHexagonSize(this);
        }
    }

    @Override
    public void notifyDisplayImpact() {
        for (DisplayModelObserver observer : observers) {
            observer.updateDisplayImpact(this);
        }
    }

    @Override
    public void notifyFullColor() {
        for (DisplayModelObserver observer : observers) {
            observer.updateFullColor(this);
        }
    }

    @Override
    public void notifyDisplay() {
        for (DisplayModelObserver observer : observers) {
            observer.updateDisplay(this);
        }
    }

    @Override
    public void addObserver(DisplayModelObserver observer) {
        observers.add(observer);
        observer.updateDisplay(this);
    }

    @Override
    public void removeObserver(DisplayModelObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean isFullColor() {
        return fullColor;
    }

    @Override
    public DisplayModel setFullColor(boolean fullColor) {
        if (this.fullColor != fullColor) {
            this.fullColor = fullColor;
            notifyFullColor();
        }
        return this;
    }
}
