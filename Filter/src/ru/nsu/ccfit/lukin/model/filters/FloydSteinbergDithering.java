package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class FloydSteinbergDithering extends PixelFilter {

    private final int countR;
    private final int countG;
    private final int countB;
    public FloydSteinbergDithering(int countR, int countG, int countB) {
        this.countR = countR;
        this.countG = countG;
        this.countB = countB;
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
        int r = Math.round(((float)((rgb >> 16) & 0xFF)) / countR);
        int g = Math.round(((float)((rgb >> 8)  & 0xFF)) / countG);
        int b = Math.round(((float)((rgb)       & 0xFF)) / countB);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
