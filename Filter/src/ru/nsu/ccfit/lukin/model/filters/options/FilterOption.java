package ru.nsu.ccfit.lukin.model.filters.options;

public class FilterOption<T> {
    private T value;

    public FilterOption(T value) {
        this.value = value;
    }

    public Class<?> getType() {
        return value.getClass();
    }

    public FilterOption<T> setValue(T value) {
        if (!isValid(value)) throw new IllegalArgumentException();
        this.value = value;
        return this;
    }
    public T getValue() {
        return this.value;
    }

    public boolean isValid(T value) {
        if (value == null) return false;
        return true;
    }
}
