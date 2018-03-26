package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class BlackAndWhiteFilter extends PixelFilter {
    public BlackAndWhiteFilter() {
        super("Black and White filter");
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int p = image.getRGB(x, y);
        int average = ((int)(((p >> 16) & 0xff) * 0.299 + ((p >> 8) & 0xff) * 0.587 + (p & 0xff) * 0.114)) & 0xFF;
        return (p & 0xFF000000) | (average << 16) | (average << 8) | average;
    }
}
