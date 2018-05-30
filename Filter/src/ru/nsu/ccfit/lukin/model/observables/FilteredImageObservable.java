package ru.nsu.ccfit.lukin.model.observables;

import ru.nsu.ccfit.lukin.model.filters.Filter;

public interface FilteredImageObservable extends ImageObservable {
    Filter getFilter();
    void notifyAppliedFilter();
}
