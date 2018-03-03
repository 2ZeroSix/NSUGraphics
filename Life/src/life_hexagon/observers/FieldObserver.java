package life_hexagon.observers;

import life_hexagon.observables.FieldObservable;

public interface FieldObserver {
    void updateFull(FieldObservable fieldFieldObservable);
    void updateField(FieldObservable fieldFieldObservable);
    void updateState(FieldObservable fieldFieldObservable, int row, int column);
    void updateImpact(FieldObservable fieldFieldObservable, int row, int column);
    void updateImpact(FieldObservable fieldFieldObservable);
    void updateLifeBounds(FieldObservable fieldFieldObservable);
}
