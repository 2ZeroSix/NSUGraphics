package life_hexagon.view.observers;

import life_hexagon.model.observables.DisplayModelObservable;

public interface DisplayModelObserver {
    void updateDisplay(DisplayModelObservable displayModel);
    void updateBorderWidth(DisplayModelObservable displayModel);
    void updateHexagonSize(DisplayModelObservable displayModel);
    void updateDisplayImpact(DisplayModelObservable displayModel);
}
