package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class SmoothFilter extends KernelFilter {
    public SmoothFilter() {
        super("Smooth filter");
        matrix = new int[][]{
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
        };
        divCoefficient = 1. / 16.;
    }
}
