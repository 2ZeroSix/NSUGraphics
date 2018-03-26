package ru.nsu.ccfit.lukin.model.filters.options;

public class IntegerFilterOption extends FilterOption<Integer> {
    private int min;
    private int max;
    public IntegerFilterOption(int min, int max) {
        super(min);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public boolean isValid(Integer value) {
        if (super.isValid(value)) {
            return getMin() <= value && value <= getMax();
        } else {
            return false;
        }
    }
}
