package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;

public class FloydSteinbergDithering extends PixelFilter {

    private int countR;
    private int countG;
    private int countB;
    public FloydSteinbergDithering(int countR, int countG, int countB) {
        super("Floyd-Steinberg filter");
        addOption("count red", new NumberFilterOption(2, 256).setValue(countR));
        addOption("count green", new NumberFilterOption(2, 256).setValue(countG));
        addOption("count blue", new NumberFilterOption(2, 256).setValue(countB));
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
        int newP = getClosestPaletteColor(oldP, countR, countG, countB);
        int errorR = ((((oldP >> 16) & 0xFF) - ((newP >> 16)   & 0xFF)));
        int errorG = ((((oldP >> 8)  & 0xFF) - ((newP >> 8)    & 0xFF)));
        int errorB = ((((oldP)       & 0xFF) - ((newP)         & 0xFF)));
        if (x + 1 < w)
            image.setRGB(x + 1, y,         filterPixel(image.getRGB(x + 1, y), errorR, errorG, errorB, 7));
        if (x - 1 >= 0 && y + 1 < h)
            image.setRGB(x - 1, y + 1, filterPixel(image.getRGB(x - 1, y + 1), errorR, errorG, errorB, 3));
        if (y + 1< h)
            image.setRGB(x, y + 1,         filterPixel(image.getRGB(x, y + 1), errorR, errorG, errorB, 5));
        if (x + 1 < w && y + 1 < h)
            image.setRGB(x + 1, y + 1, filterPixel(image.getRGB(x + 1, y + 1), errorR, errorG, errorB, 1));
        return newP;
    }

    private int filterPixel(int rgb, int R, int G, int B, int multiplier) {
        int r = clamp(((rgb >> 16) & 0xFF) + R * multiplier / 16);
        int g = clamp(((rgb >> 8)  & 0xFF) + G * multiplier / 16);
        int b = clamp(((rgb)       & 0xFF) + B * multiplier / 16);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

}
