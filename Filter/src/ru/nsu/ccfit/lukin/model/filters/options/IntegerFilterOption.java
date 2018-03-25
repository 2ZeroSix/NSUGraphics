package ru.nsu.ccfit.lukin.model.filters.options;

public class IntegerFilterOption extends FilterOption<Integer> {
    private int min;
    private int max;
    public IntegerFilterOption(int min, int max) {
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
    public boolean isValid(Object value) {
        if (super.isValid(value)) {
            int val = ((Integer)value);
            return getMin() <= val && val <= getMax();
        } else {
            return false;
        }
    }
}
