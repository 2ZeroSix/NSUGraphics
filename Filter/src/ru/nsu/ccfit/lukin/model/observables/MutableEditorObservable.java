package ru.nsu.ccfit.lukin.model.observables;

import ru.nsu.ccfit.lukin.model.filters.Filter;

import java.util.Iterator;

public interface MutableEditorObservable extends EditorObservable {
    void applyFilter(Filter filter);
    void removeFilter(Filter filter);
    default void removeLastFilter() {
        Iterator<Filter> filterIterator = getAppliedFilters().iterator();
        Filter filter = null;
        while (filterIterator.hasNext()) {
            filter = filterIterator.next();
        }
        if (filter != null) {
            removeFilter(filter);
        }
    }
    default void removeAllFilters() {
        for (Filter filter :
                getAppliedFilters()) {
            removeFilter(filter);
        }
    }

}
