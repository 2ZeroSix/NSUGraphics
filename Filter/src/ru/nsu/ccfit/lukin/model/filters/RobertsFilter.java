package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;

public class RobertsFilter extends PixelFilterWithTemp {
    int[][] GX = new int[][] {
            {1, 0},
            {0, -1}
    };
    private int threshold;

    public RobertsFilter(int threshold) {
        super("Roberts filter");
        addOption("threshold", new NumberFilterOption(1, 255).setValue(threshold));
    }

    @Override
    protected void assignOptions() {
        this.threshold = (Integer)getOption("threshold").getValue();
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int[][] rgb = new int[2][3];
        for (int i = 0; i <= 1; ++i) {
            for (int j = 0; j <= 1; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    rgb[0][k] += GX[i][j] * component(image, x + i, y + j, k);
                    rgb[1][k] += GX[i][1 - j] * component(image, x + i, y + j, k);
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
