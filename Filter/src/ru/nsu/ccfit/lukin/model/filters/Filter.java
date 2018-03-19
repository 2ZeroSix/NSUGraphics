package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public interface Filter {
    void apply(BufferedImage image);
}
