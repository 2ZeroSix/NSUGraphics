package ru.nsu.ccfit.lukin.model.filters.options;

public class DoubleFilterOption extends FilterOption<Double> {
    private final double min;
    private final double max;

    public DoubleFilterOption(double min, double max) {
        super(min);
        this.min = min;
        this.max = max;
    }
}
