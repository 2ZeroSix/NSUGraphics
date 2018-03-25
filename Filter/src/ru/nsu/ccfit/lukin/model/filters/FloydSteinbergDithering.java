package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.filters.options.IntegerFilterOption;

import java.awt.image.BufferedImage;
import java.util.Map;

public class FloydSteinbergDithering extends PixelFilter {

    private int countR;
    private int countG;
    private int countB;
    public FloydSteinbergDithering(int countR, int countG, int countB) {
        addOption("count red", new IntegerFilterOption(1, 255).setValue(countR));
        addOption("count green", new IntegerFilterOption(1, 255).setValue(countG));
        addOption("count blue", new IntegerFilterOption(1, 255).setValue(countB));
    }

    @Override
    protected void assignOptions() {
        this.countR = (Integer) getOption("count red").getValue();
        this.countG = (Integer) getOption("count green").getValue();
        this.countB = (Integer) getOption("count blue").getValue();
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int h = image.getHeight();
        int w = image.getWidth();
        int oldP = image.getRGB(x, y);
        int newP = getClosestPaletteColor(oldP);
        int error = oldP - newP;
        if (x + 1 < w)
            image.setRGB(x + 1, y,         image.getRGB(x + 1, y) + error * 7 / 16);
        if (x - 1 >= 0 && y + 1 < h)
            image.setRGB(x - 1, y + 1, image.getRGB(x - 1, y + 1) + error * 3 / 16);
        if (y + 1< h)
            image.setRGB(x, y + 1,         image.getRGB(x, y + 1) + error * 5 / 16);
        if (x + 1 < w && y + 1 < h)
            image.setRGB(x + 1, y + 1, image.getRGB(x + 1, y + 1) + error / 16);
        return newP;
    }

    private int getClosestPaletteColor(int rgb) {
        int r = Math.round(((float)((rgb >> 16) & 0xFF)) * countR / 256) * 256 / countR;
        int g = Math.round(((float)((rgb >> 8)  & 0xFF)) * countG / 256) * 256 / countG;
        int b = Math.round(((float)((rgb)       & 0xFF)) * countB / 256) * 256 / countB;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
