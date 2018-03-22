package ru.nsu.ccfit.lukin.model.filters;

public interface FilterFactory {
    Filter getFilter(String filterName, String... args);

    String getFilterName(Filter filter);
}
