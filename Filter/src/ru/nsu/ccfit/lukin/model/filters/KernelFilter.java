package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.ImageUtils;

import java.awt.image.BufferedImage;

public class KernelFilter extends PixelFilterWithTemp {
    protected int[][] matrix;
    protected double divCoefficient = 1.0;
    protected double shiftCoefficient = 0.0;
    protected KernelFilter(String name) {
        super(name);
        this.matrix = matrix;
    }

    protected int filterPixel(BufferedImage image, int x, int y) {
        int[] rgb = new int[3];
        for (int i = -(matrix.length - 1) / 2; i <= matrix.length / 2; ++i) {
            for (int j = -(matrix[0].length - 1) / 2; j <= (matrix[0].length / 2); ++j) {
                for (int k = 0; k <= 2; ++k) {
                    rgb[k] += matrix[(matrix.length - 1) / 2 + i][(matrix[0].length - 1) / 2 + j]
                            * component(image, x + i, y + j, k);
                }
            }
        }
        for (int k = 0; k <= 2; ++k) {
            rgb[k] = clamp((int) (rgb[k] * divCoefficient + shiftCoefficient));
        }

        return 0xFF000000 | (rgb[2] << 16) | (rgb[1] << 8) | rgb[0];
    }
}
