package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFilter implements Filter {
    private Map<String, FilterOption<?>> options = new HashMap<>();

    @Override
    public void apply(BufferedImage image) {
        if (!checkArgs(image)) throw new IllegalArgumentException("wrong image given");
        assignOptions();
        realApply(image);
    }

    protected boolean checkArgs(BufferedImage image) {
        return  image != null;
    }

    protected AbstractFilter addOption(String name, FilterOption<?> option) {
        options.put(name, option);
        return this;
    }

    @Override
    public Map<String, FilterOption<?>> getOptions() {
        return options;
    }

    @Override
    public void setOption(String name, Object value) {
        FilterOption option = options.get(name);
        if (option != null) {
            option.setValue(value);
        } else {
            throw new IllegalArgumentException("No such option: " + name);
        }
    }

    protected void assignOptions() {
        // default without options
    }
    protected abstract void realApply(BufferedImage image);
}
