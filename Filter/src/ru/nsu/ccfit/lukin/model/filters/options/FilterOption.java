package ru.nsu.ccfit.lukin.model.filters.options;

public abstract class FilterOption<T> {
    private T value;

    public Class<?> getType() {
        return value.getClass();
    }

    public FilterOption<T> setValue(Object value) {
        if (!isValid(value)) throw new IllegalArgumentException();
        this.value = (T) value;
        return this;
    }
    public T getValue() {
        return this.value;
    }

    public boolean isValid(Object value) {
        if (value == null) return false;
        return getType().isAssignableFrom(value.getClass());
    }
}
