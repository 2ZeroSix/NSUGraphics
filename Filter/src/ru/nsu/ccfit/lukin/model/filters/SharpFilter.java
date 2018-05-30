package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;

public class SharpFilter extends KernelFilter {
    public SharpFilter() {
        super("Sharp filter");
        divCoefficient = 1.;
        shiftCoefficient = 0.;
        matrix = new int[][] {
                {0,-1,0},
                {-1,5,-1},
                {0,-1,0}
        };
    }
}
