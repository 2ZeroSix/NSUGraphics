package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.ImageUtils;

import java.awt.image.BufferedImage;

public abstract class PixelFilterWithTemp extends PixelFilter {
    protected PixelFilterWithTemp(String name) {
        super(name);
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
