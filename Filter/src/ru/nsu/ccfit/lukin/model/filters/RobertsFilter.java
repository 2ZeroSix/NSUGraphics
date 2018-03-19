package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class RobertsFilter extends PixelFilter {
    int[][] GX = new int[][] {
            {1, 0},
            {0, -1}
    };
    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        return 0;
    }
}
