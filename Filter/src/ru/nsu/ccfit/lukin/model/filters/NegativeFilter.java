package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class NegativeFilter extends PixelFilter {
    public NegativeFilter() {
        super("Negative filter");
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int p = image.getRGB(x, y);
        return (p & 0xFF000000) | neg(p, 16) | neg(p, 8) | neg(p, 0);
    }
    private int neg(int p, int o) {
        return (255 - (p >> o) & 0xFF) << o;
    }
}
