package life_hexagon.model.observables;

import life_hexagon.view.observers.DisplayModelObserver;

public interface DisplayModelObservable {
    int getBorderWidth();

    int getHexagonSize();

    boolean isDisplayImpact();

    void notifyDisplay();

    void notifyBorderWidth();

    void notifyHexagonSize();

    void notifyDisplayImpact();

    void addObserver(DisplayModelObserver observer);

    void removeObserver(DisplayModelObserver observer);
}
