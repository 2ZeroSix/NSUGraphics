package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public abstract class PixelFilter extends AbstractFilter {
    @Override
    protected void realApply(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.setRGB(x, y, filterPixel(image, x, y));
            }
        }
    }

    protected abstract int filterPixel(BufferedImage image, int x, int y);
}
