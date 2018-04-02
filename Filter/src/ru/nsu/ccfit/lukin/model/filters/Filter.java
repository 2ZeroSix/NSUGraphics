package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.view.observers.FilterObserver;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface Filter {
    Map<String, FilterOption<?>> getOptions();
    default FilterOption<?> getOption(String name) {
        return getOptions().get(name);
    }
    void setOption(String name, Object value);
    String getName();
    void apply(BufferedImage image);

    void notifyOption(String name);
    void addFilterObserver(FilterObserver observer);
    void removeFilterObserver(FilterObserver observer);
}
