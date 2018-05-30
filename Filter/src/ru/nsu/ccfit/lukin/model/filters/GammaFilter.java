package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;

public class GammaFilter extends PixelFilter {
    private double level;
    public GammaFilter(double level) {
        super("Gamma filter");
        addOption("level", new NumberFilterOption<>(0., 10.).setValue(level));
    }

    @Override
    protected void assignOptions() {
        this.level = (Double) getOption("level").getValue();
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = (int)(255 * Math.pow((((rgb >> 16)  & 0xFF) / 255.), level));
        int g = (int)(255 * Math.pow((((rgb >> 8)   & 0xFF) / 255.), level));
        int b = (int)(255 * Math.pow((((rgb)        & 0xFF) / 255.), level));
        return 0xFF000000 | (r << 16) | (g << 8) | (b);
    }
}
