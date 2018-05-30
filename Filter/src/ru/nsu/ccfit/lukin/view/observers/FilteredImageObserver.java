package ru.nsu.ccfit.lukin.view.observers;

import ru.nsu.ccfit.lukin.model.observables.FilteredImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;

public interface FilteredImageObserver extends ImageObserver {
    void updateAppliedFilter(FilteredImageObservable observable);
    default void updateImage(FilteredImageObservable observable) {
        updateImage((ImageObservable)observable);
    }

}
