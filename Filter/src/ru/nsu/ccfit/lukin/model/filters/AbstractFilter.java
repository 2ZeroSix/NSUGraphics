package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

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
        --countR;
        --countG;
        --countB;
        int r = clamp(Math.round(((float)((rgb >> 16) & 0xFF)) * countR / 256) * 256 / countR);
        int g = clamp(Math.round(((float)((rgb >> 8)  & 0xFF)) * countG / 256) * 256 / countG);
        int b = clamp(Math.round(((float)((rgb)       & 0xFF)) * countB / 256) * 256 / countB);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
