package life_hexagon.model;

import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.view.observers.DisplayModelObserver;

import java.util.HashSet;
import java.util.Set;

public class DisplayModel implements MutableDisplayModelObservable{
    private int borderWidth;
    private int hexagonSize;
    private boolean displayImpact;
    private Set<DisplayModelObserver> observers = new HashSet<>();

    public DisplayModel(int borderWidth, int hexagonSize, boolean displayImpact) {
        this.borderWidth = borderWidth;
        this.hexagonSize = hexagonSize;
        this.displayImpact = displayImpact;
    }

    @Override
    public MutableDisplayModelObservable setBorderWidth(int width) {
        this.borderWidth = width;
        return this;
    }

    @Override
    public MutableDisplayModelObservable setHexagonSize(int size) {
        this.hexagonSize = size;
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
        this.displayImpact = displayImpact;
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
    public void notifyDisplayMode() {
        for (DisplayModelObserver observer : observers) {
            observer.updateDisplayMode(this);
        }
    }

    @Override
    public void addObserver(DisplayModelObserver observer) {
        observers.add(observer);
        observer.updateDisplayMode(this);
    }

    @Override
    public void removeObserver(DisplayModelObserver observer) {
        observers.remove(observer);
    }
}
