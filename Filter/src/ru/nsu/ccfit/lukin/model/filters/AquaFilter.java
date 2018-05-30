package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class AquaFilter extends PixelFilterWithTemp {
    public AquaFilter() {
        super("Aqua filter");
    }

    @Override
    protected void realApply(BufferedImage image) {
        super.realApply(image);
        new SharpFilter().realApply(image);
    }

    private int[][] arr = new int[3][25];
    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = 0; k <= 2; ++k) {
                    arr[k][(i + 2) * 5 + (j + 2)] = component(image, x + i, y + j, k);
                }
            }
        }
        for (int k = 0; k <= 2; ++k) {
            Arrays.sort(arr[k]);
            arr[k][0] = (arr[k][arr[k].length / 2] + arr[k][(arr[k].length + 1) / 2]) / 2;
        }
        return 0xFF000000 | (arr[2][0] << 16) | (arr[1][0] << 8) | arr[0][0];
    }
}
