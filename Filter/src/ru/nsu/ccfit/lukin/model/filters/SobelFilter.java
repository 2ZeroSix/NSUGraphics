package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.options.IntegerFilterOption;

import java.awt.image.BufferedImage;

public class SobelFilter extends PixelFilter {
    private final static int[][] GX = new int[][] {
            {-1,  0,  1},
            {-2,  0,  2},
            {-1,  0,  1}
    };
    private int threshold;

    public SobelFilter(int threshold) {
        super("Sobel filter");
        addOption("threshold", new IntegerFilterOption(1, 255).setValue(threshold));
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
        for (int k = 0; k <= 2; ++k) { // TODO узнать про порог
            rgb[0][k] = Integer.max(Integer.min((int)Math.sqrt(rgb[0][k] * rgb[0][k] + rgb[1][k] + rgb[1][k]), 0xFF), 0);
            rgb[0][k] = rgb[0][k] > threshold ? rgb[0][k] : 0;
//            rgb[0][k] = Math.sqrt(rgb[0][k] * rgb[0][k] + rgb[1][k] + rgb[1][k]) > threshold ? 255 : 0;
        }
        return 0xFF000000 | (rgb[0][0] << 16) | (rgb[0][1] << 8) | rgb[0][2];
    }

    private int component(BufferedImage image, int x, int y, int offset) {
        x = x < 0 ? -x : (x >= image.getWidth()  ? 2 * image.getWidth() - 1 - x : x);
        y = y < 0 ? -y : (y >= image.getHeight() ? 2 * image.getHeight() - 1 - y: y);
        return (image.getRGB(x, y) >> (8 * offset)) & 0xFF;
    }

    @Override
    protected void assignOptions() {
        this.threshold = (Integer)getOption("threshold").getValue();
    }

    @Override
    protected void realApply(BufferedImage image) {
        BufferedImage tmpImage = ImageUtils.copy(image);
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                image.setRGB(x, y, filterPixel(tmpImage, x, y));
            }
        }
    }
}
