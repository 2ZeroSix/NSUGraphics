package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;
import ru.nsu.ccfit.lukin.view.imagePanels.FilteredImage;
import ru.nsu.ccfit.lukin.view.imagePanels.SelectedImage;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class FilterOptionsDialog {
    private JPanel panel = new JPanel(new GridBagLayout());
    private Filter filter;
    private FilteredImage filteredImage;
    private boolean haveOptions;
    private Map<String, JFormattedTextField> componentMap = new HashMap<>();

    public FilterOptionsDialog(Filter filter, FilteredImage filteredImage) {
        this.filter = filter;
        this.filteredImage = filteredImage;
        Map<String, FilterOption<?>> options = filter.getOptions();
        if (!options.isEmpty()) {
            haveOptions = true;
            options.forEach((name, option) -> {
                if (option instanceof NumberFilterOption<?>) {
                    NumberFilterOption<?> numOpt = (NumberFilterOption) option;
                    addNumberInput(name, numOpt.getMin(), numOpt.getMax(), numOpt.getValue(), val -> {
                        filter.setOption(name, val);
                        filteredImage.setFilter(filter);
                    });
                }
            });
        } else {
            haveOptions = false;
        }
    }

    private void updateOptions() {
        filter.getOptions().forEach((name, option) -> {
            if (option instanceof NumberFilterOption) {
                NumberFilterOption intOpt = (NumberFilterOption) option;
                componentMap.get(name).setValue(intOpt.getValue());
            }
        });
    }

    public void showDialog(Component parentComponent) {
        if (getFilteredImage().getSelectedImage().getImage() == null) {
            JOptionPane.showMessageDialog(parentComponent, "Image is not selected",
                    "Can't apply filter", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        if (haveOptions) {
            updateOptions();
            filteredImage.setFilter(filter);
            int retval = JOptionPane.showConfirmDialog(parentComponent, panel, filter.getName() + " Options",
                    filteredImage.isAutoUpdate() ? JOptionPane.DEFAULT_OPTION : JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (!filteredImage.isAutoUpdate()) {
                switch (retval) {
                    case JOptionPane.OK_OPTION:
                        filteredImage.applyFilter();
                    default:
                        //pass
                }
            }
        } else {
            filteredImage.setFilter(filter);
        }
    }

    private int intOptionsCount = 0;
    private <T extends Number> void addNumberInput(String name, T min, T max, T defaultValue, Consumer<Number> onChange) {
        JLabel label = new JLabel(name);
        if (defaultValue instanceof Integer) {
            JFormattedTextField field = new JFormattedTextField(new DecimalFormat("0"));
            JSlider slider = new JSlider(JSlider.HORIZONTAL, min.intValue(), max.intValue(), defaultValue.intValue());
            field.setColumns(Integer.max(min.toString().length(), max.toString().length()));
            field.setValue(defaultValue);
            field.addPropertyChangeListener("value", evt -> {
                if (evt.getNewValue() != null) {
                    int value = ((Number) evt.getNewValue()).intValue();
                    if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                        if (value < min.intValue() || value > max.intValue()) {
                            field.setValue(evt.getOldValue());
                        } else {
                            onChange.accept(value);
                            slider.setValue(value);
                        }
                    }
                }
            });
            slider.addChangeListener((ce) -> field.setValue(slider.getValue()));
            ContainerUtils.add(panel, label, 0, intOptionsCount, 1, 1);
            ContainerUtils.add(panel, field, 1, intOptionsCount, 1, 1);
            ContainerUtils.add(panel, slider, 2, intOptionsCount, 1, 1);
            ++intOptionsCount;
            componentMap.put(name, field);
        } else if (defaultValue instanceof Double) {
            JFormattedTextField field = new JFormattedTextField(new DecimalFormat("0.00"));
            JSlider slider = new JSlider(JSlider.HORIZONTAL, (int)(min.doubleValue() * 100), (int)(max.doubleValue() * 100), (int)(defaultValue.doubleValue() * 100));
            field.setColumns(Integer.max(min.toString().length(), max.toString().length()));
            field.setValue(defaultValue);
            field.addPropertyChangeListener("value", evt -> {
                if (evt.getNewValue() != null) {
                    double value = ((Number) evt.getNewValue()).doubleValue();
                    if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                        if (value < min.doubleValue() || value > max.doubleValue()) {
                            field.setValue(evt.getOldValue());
                        } else {
                            onChange.accept(value);
                            slider.setValue((int)(value * 100));
                        }
                    }
                }
            });
            slider.addChangeListener((ce) -> field.setValue(slider.getValue() / 100.));
            ContainerUtils.add(panel, label, 0, intOptionsCount, 1, 1);
            ContainerUtils.add(panel, field, 1, intOptionsCount, 1, 1);
            ContainerUtils.add(panel, slider, 2, intOptionsCount, 1, 1);
            ++intOptionsCount;
            componentMap.put(name, field);

        }
    }


    public Filter getFilter() {
        return filter;
    }

    public FilteredImage getFilteredImage() {
        return filteredImage;
    }
}
