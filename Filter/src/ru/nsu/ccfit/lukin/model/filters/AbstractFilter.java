package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.view.observers.FilterObserver;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFilter implements Filter {
    private Map<String, FilterOption<?>> options = new HashMap<>();
    private String name;

    protected AbstractFilter(String name) {
        this.name = name;
    }

    @Override
    public void apply(BufferedImage image) {
        if (!checkArgs(image)) throw new IllegalArgumentException("wrong image given");
        assignOptions();
        realApply(image);
    }

    protected boolean checkArgs(BufferedImage image) {
        return image != null;
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
            notifyOption(name);
        } else {
            throw new IllegalArgumentException("No such option: " + name);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected void assignOptions() {
        // default without options
    }

    protected abstract void realApply(BufferedImage image);

    protected static int clamp(int comp) {
        return Integer.min(Integer.max(comp, 0), 255);
    }

    protected int getClosestPaletteColor(int rgb, int countR, int countG, int countB) {
        int rd = 255 / (countR - 1);
        int gd = 255 / (countG - 1);
        int bd = 255 / (countB - 1);
        int r = clamp(((((rgb >> 16) & 0xFF) + rd / 2) / rd) * rd);
        int g = clamp(((((rgb >> 8) & 0xFF) + gd / 2) / gd) * gd);
        int b = clamp(((((rgb) & 0xFF) + bd / 2) / bd) * bd);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    protected int component(BufferedImage image, int x, int y, int offset) {
        x = x < 0 ? -x : (x >= image.getWidth() ? 2 * image.getWidth() - 1 - x : x);
        y = y < 0 ? -y : (y >= image.getHeight() ? 2 * image.getHeight() - 1 - y : y);
        if (offset == -1) return image.getRGB(x, y);
        return (image.getRGB(x, y) >> (8 * offset)) & 0xFF;
    }

    Set<FilterObserver> observers = new HashSet<>();

    @Override
    public void notifyOption(String name) {
        for (FilterObserver observer : observers)
            observer.updateOption(name);
    }

    @Override
    public void addFilterObserver(FilterObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeFilterObserver(FilterObserver observer) {
        observers.remove(observer);
    }
}
