package life_hexagon.model.observables;

import life_hexagon.view.observers.EditModelObserver;

public interface EditModelObservable {
    boolean isXOR();
    boolean isLoop();
//    boolean getFill();

    void notifyEdit();
    void notifyLoop();
    void notifyXOR();
    void addObserver(EditModelObserver observer);
    void removeObserver(EditModelObserver observer);
}
