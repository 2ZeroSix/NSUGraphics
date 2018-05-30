package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;

public class SobelFilter extends PixelFilterWithTemp {
    private final static int[][] GX = new int[][] {
            {-1,  0,  1},
            {-2,  0,  2},
            {-1,  0,  1}
    };
    private int threshold;

    public SobelFilter(int threshold) {
        super("Sobel filter");
        addOption("threshold", new NumberFilterOption(1, 255).setValue(threshold));
    }

    @Override
    protected void assignOptions() {
        this.threshold = (Integer)getOption("threshold").getValue();
    }

    protected int filterPixel(BufferedImage image, int x, int y) {
        int[][] rgb = new int[2][3];
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    rgb[0][k] += GX[1 + i][1 + j] * component(image, x + i, y + j, k);
                    rgb[1][k] += GX[1 + j][1 + i] * component(image, x + i, y + j, k);
                }
            }
        }
        int max = 0;
        for (int k = 0; k <= 2; ++k) {
            max = Math.sqrt(rgb[0][k] * rgb[0][k] + rgb[1][k] * rgb[1][k]) > threshold ? 255 : 0;
        }
        for (int k = 0; k <= 2; ++k) {
            rgb[0][k] = max;
        }
        return 0xFF000000 | (rgb[0][2] << 16) | (rgb[0][1] << 8) | rgb[0][0];
    }

}
