package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class OrderedDithering extends PixelFilter {
    private static int[][] matrix = new int[][] {
            { 0, 48, 12, 60,  3, 51, 15, 63},
            {32, 16, 44, 28, 35, 19, 47, 31},
            { 8, 56,  4, 52, 11, 59,  7, 55},
            {40, 24, 36, 20, 43, 27, 39, 23},
            { 2, 50, 14, 62,  1, 49, 13, 61},
            {34, 18, 46, 30, 33, 17, 45, 29},
            {10, 58,  6, 54,  9, 57,  5, 53},
            {42, 26, 38, 22, 41, 25, 37, 21},
    };
    private final int[][][] ditherMatrix;

    private final int countR;
    private final int countG;
    private final int countB;
    public OrderedDithering(int countR, int countG, int countB) {
        this.countR = countR;
        this.countG = countG;
        this.countB = countB;
        ditherMatrix = new int[3][8][8];
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[0].length; ++j) {
                int dithR = ((256 * matrix[j][j]) - 256) / 64 / countR;
                int dithG = ((256 * matrix[i][j]) - 256) / 64 / countG;
                int dithB = ((256 * matrix[i][j]) - 256) / 64 / countB;
                ditherMatrix[0][i][j] = dithR;
                ditherMatrix[1][i][j] = dithG;
                ditherMatrix[2][i][j] = dithB;
            }
        }
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = Math.abs((rgb >> 16) & 0xFF + ditherMatrix[0][x][y]) & 0xFF;
        int g = Math.abs((rgb >> 8)  & 0xFF + ditherMatrix[1][x][y]) & 0xFF;
        int b = Math.abs((rgb)       & 0xFF + ditherMatrix[2][x][y]) & 0xFF;
        return getClosestPaletteColor((0xFF000000) | (r << 16) | (g << 8) | (b));
    }

    private int getClosestPaletteColor(int rgb) {
        int r = Math.round(((float)((rgb >> 16) & 0xFF)) / countR);
        int g = Math.round(((float)((rgb >> 8)  & 0xFF)) / countG);
        int b = Math.round(((float)((rgb)       & 0xFF)) / countB);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
