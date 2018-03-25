package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.Filter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public abstract class FilterButton extends ImageObserverButton implements ActionListener {
    private FilteredImage filteredImage;
    private boolean needToInitFilter = true;
    private Filter filter;

    public FilterButton(String filterName, FilteredImage filteredImage) {
        super(filterName, filteredImage);
        this.filteredImage = filteredImage;
        addActionListener(this);
    }

    public boolean isNeedToInitFilter() {
        return needToInitFilter;
    }

    public FilterButton setNeedToInitFilter(boolean needToInitFilter) {
        this.needToInitFilter = needToInitFilter;
        return this;
    }

    public Filter getFilter() {
        return filter;
    }

    public FilterButton setFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    public abstract boolean initFilter();
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (needToInitFilter) {
            if (initFilter()) {
                filteredImage.setFilter(filter);
            }
        } else {
            filteredImage.setFilter(filter);
        }
    }


    private int row = 0;
    public void addIntegerInput(String name, int min, int max, int defaultValue, Consumer<Integer> onChange) {
        JLabel label = new JLabel(name);
        JFormattedTextField field = new JFormattedTextField(new DecimalFormat("0; (0)"));
        field.setColumns(Integer.max(Integer.toString(min).length(), Integer.toString(max).length()));
        field.setValue(defaultValue);
        field.addPropertyChangeListener("value", evt -> {
            if (evt.getNewValue() != null) {
                int value = ((Number) evt.getNewValue()).intValue();
                if (value < min || value > max) {
                    field.setValue(evt.getOldValue());
                } else {
                    onChange.accept(value);
                }
            }
        });
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);

        ContainerUtils.add(this, label, 0, row, 1, 1);
        ContainerUtils.add(this, field, 1, row, 1, 1);
        ContainerUtils.add(this, slider, 2, row, 1, 1);
    }
}
