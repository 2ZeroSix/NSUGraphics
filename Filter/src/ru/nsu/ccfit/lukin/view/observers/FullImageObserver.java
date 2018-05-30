package ru.nsu.ccfit.lukin.view.observers;

import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;

public interface FullImageObserver extends ImageObserver {
    void updateSelection(FullImageObservable observable);
    void updateSelectable(FullImageObservable observable);
    default void updateImage(FullImageObservable observable) {
        updateImage((ImageObservable)observable);
    }
}
