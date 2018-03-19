package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public abstract class AbstractFilter implements Filter {
    @Override
    public void apply(BufferedImage image) {
        if (!checkArgs(image)) throw new IllegalArgumentException("wrong image given");
        realApply(image);
    }

    protected boolean checkArgs(BufferedImage image) {
        return  image != null;
    }

    protected abstract void realApply(BufferedImage image);
}
