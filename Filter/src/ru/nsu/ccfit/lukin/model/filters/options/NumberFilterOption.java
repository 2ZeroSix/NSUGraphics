package ru.nsu.ccfit.lukin.model.filters.options;

import java.math.BigDecimal;

public class NumberFilterOption<T extends Number> extends FilterOption<T> {
    private T min;
    private T max;
    public NumberFilterOption(T min, T max) {
        super(min);
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public boolean isValid(T value) {
        if (super.isValid(value)) {
            return compare(getMin(), value) <= 0 && compare(value, getMax()) <= 0;
        } else {
            return false;
        }
    }

    private int compare(Number a, Number b) {
        String astr = a.toString();
        String bstr = b.toString();
        if (astr.equals("Infinity") || bstr.equals("-Infinity")) return astr.equals(bstr) ? 0 : 1;
        else if (bstr.equals("Infinity") || astr.equals("-Infinity")) return astr.equals(bstr) ? 0 : -1;
        else if (astr.equals("NaN") || bstr.equals("NaN")) return 1;
        else return new BigDecimal(astr).compareTo(new BigDecimal(bstr));
    }
}
