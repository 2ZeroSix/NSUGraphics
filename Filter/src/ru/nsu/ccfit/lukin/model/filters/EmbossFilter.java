package ru.nsu.ccfit.lukin.model.filters;

public class EmbossFilter extends KernelFilter {
    public EmbossFilter() {
        super("Emboss filter");
        divCoefficient = 1.;
        shiftCoefficient = 128.;
        matrix = new int[][] {
                {0,1,0},
                {-1,0,1},
                {0,-1,0}
        };
    }
}
