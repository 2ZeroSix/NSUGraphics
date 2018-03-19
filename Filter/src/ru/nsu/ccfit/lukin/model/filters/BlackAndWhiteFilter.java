package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class BlackAndWhiteFilter extends PixelFilter {
    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int p = image.getRGB(x, y);
        int average = (((p >> 16) & 0xff + (p >> 8) & 0xff + (p) & 0xff) / 3) & 0xFF;
        return (p & 0xFF000000) | (average << 16) | (average << 8) | average;
    }
}
