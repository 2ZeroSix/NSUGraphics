package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.IntegerFilterOption;

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
    private int[][][] ditherMatrix;

    private int countR;
    private int countG;
    private int countB;
    public OrderedDithering(int countR, int countG, int countB) {
        addOption("count red", new IntegerFilterOption(1, 255).setValue(countR));
        addOption("count green", new IntegerFilterOption(1, 255).setValue(countG));
        addOption("count blue", new IntegerFilterOption(1, 255).setValue(countB));
    }

    @Override
    protected void assignOptions() {
        this.countR = (Integer) getOption("count red").getValue();
        this.countG = (Integer) getOption("count green").getValue();
        this.countB = (Integer) getOption("count blue").getValue();
        ditherMatrix = ditherMatrix == null ? new int[3][8][8] : ditherMatrix;
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[0].length; ++j) {
                ditherMatrix[0][i][j] = ((256 * matrix[j][j]) - 256) / 64 / countR;
                ditherMatrix[1][i][j] = ((256 * matrix[i][j]) - 256) / 64 / countG;
                ditherMatrix[2][i][j] = ((256 * matrix[i][j]) - 256) / 64 / countB;
            }
        }

    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int rgb = image.getRGB(x, y);
        int r = ((rgb >> 16) & 0xFF + ditherMatrix[0][x%8][y%8]) & 0xFF;
        int g = ((rgb >> 8)  & 0xFF + ditherMatrix[1][x%8][y%8]) & 0xFF;
        int b = ((rgb)       & 0xFF + ditherMatrix[2][x%8][y%8]) & 0xFF;
//        int r = Integer.min(Integer.max((rgb >> 16) & 0xFF + ditherMatrix[0][x%8][y%8], 0), 0xFF);
//        int g = Integer.min(Integer.max((rgb >> 8)  & 0xFF + ditherMatrix[1][x%8][y%8], 0), 0xFF);
//        int b = Integer.min(Integer.max((rgb)       & 0xFF + ditherMatrix[2][x%8][y%8], 0), 0xFF);
        return getClosestPaletteColor((0xFF000000) | (r << 16) | (g << 8) | (b));
    }

    private int getClosestPaletteColor(int rgb) {
        int r = Math.round(((float)((rgb >> 16) & 0xFF)) * countR / 256) * 256 / countR;
        int g = Math.round(((float)((rgb >> 8)  & 0xFF)) * countG / 256) * 256 / countG;
        int b = Math.round(((float)((rgb)       & 0xFF)) * countB / 256) * 256 / countB;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
