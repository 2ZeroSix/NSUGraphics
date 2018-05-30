package life_hexagon.model.observables;

import life_hexagon.model.DisplayModel;

public interface MutableDisplayModelObservable extends DisplayModelObservable {
    MutableDisplayModelObservable setBorderWidth(int width);
    MutableDisplayModelObservable setHexagonSize(int size);
    MutableDisplayModelObservable setDisplayImpact(boolean displayImpact);

    DisplayModel setFullColor(boolean fullColor);
}
