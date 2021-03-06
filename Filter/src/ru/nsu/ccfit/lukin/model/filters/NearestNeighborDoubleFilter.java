package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class NearestNeighborDoubleFilter extends AbstractFilter {
    public NearestNeighborDoubleFilter() {
        super("Double filter");
    }

    @Override
    protected void realApply(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        for (int x = 0; x < w / 2; ++x) {
            for (int y = 0; y < h / 2; ++y) {
                image.setRGB(x, y, image.getRGB(x / 2 + w / 4, y / 2 + h / 4));
                image.setRGB(w - 1 - x, y, image.getRGB(w - 1 - (x / 2 + (w + 3) / 4), y / 2 + h / 4));
            }
            for (int y = h - 1; y >= h / 2; --y) {
                image.setRGB(x, y, image.getRGB(x / 2 + w / 4, y / 2 + h / 4));
                image.setRGB(w - 1 - x, y, image.getRGB(w - 1 - (x / 2 + (w + 3) / 4), y / 2 + h / 4));
            }
        }
    }
}
