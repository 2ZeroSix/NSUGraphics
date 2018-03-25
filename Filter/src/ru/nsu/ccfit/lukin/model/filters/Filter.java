package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface Filter {
    Map<String, FilterOption<?>> getOptions();
    default FilterOption<?> getOption(String name) {
        return getOptions().get(name);
    }
    void setOption(String name, Object value);
    void apply(BufferedImage image);
}
