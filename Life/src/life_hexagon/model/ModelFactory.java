package life_hexagon.model;

import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.model.observables.MutableFieldObservable;

public interface ModelFactory {
    MutableFieldObservable createField();
    MutableDisplayModelObservable createDisplayModel();
}
