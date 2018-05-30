package life_hexagon.view.observers;

import life_hexagon.model.observables.FieldObservable;

public interface FieldObserver {
    void updateField(FieldObservable field);

    void updateState(FieldObservable field, int row, int column);

    void updateImpact(FieldObservable field, int row, int column);

    void updateSize(FieldObservable field);

    void updateLifeBounds(FieldObservable field);

    void updateImpact(FieldObservable field);
}
