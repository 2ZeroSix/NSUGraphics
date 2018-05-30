package life_hexagon.view.observers;

import life_hexagon.model.EditModel;
import life_hexagon.model.observables.EditModelObservable;

public interface EditModelObserver {
    void updateXOR(EditModelObservable editModel);
    void updateLoop(EditModelObservable editModel);
    void updateEdit(EditModelObservable editModel);
}
