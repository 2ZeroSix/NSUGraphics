package life_hexagon.view.observers;

import life_hexagon.model.observables.FieldObservable;

public interface FieldObserver {
    void updateField(FieldObservable fieldObservable);

    void updateState(FieldObservable fieldObservable, int row, int column);

    void updateImpact(FieldObservable fieldObservable, int row, int column);

    void updateSize(FieldObservable fieldObservable);

    void updateLifeBounds(FieldObservable fieldObservable);

    void updateImpact(FieldObservable fieldObservable);
}
