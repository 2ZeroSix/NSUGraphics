package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.filters.options.IntegerFilterOption;
import ru.nsu.ccfit.lukin.view.imagePanels.FilteredImage;

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
    private FilteredImage image;
    private boolean instantFiltering;
    private boolean haveOptions;
    private Map<String, JFormattedTextField> componentMap = new HashMap<>();

    public FilterOptionsDialog(Filter filter, FilteredImage image, boolean instantFiltering) {
        this.filter = filter;
        this.image = image;
        this.instantFiltering = instantFiltering;
        Map<String, FilterOption<?>> options = filter.getOptions();
        if (!options.isEmpty()) {
            haveOptions = true;
            options.forEach((name, option) -> {
                if (option instanceof IntegerFilterOption) {
                    IntegerFilterOption intOpt = (IntegerFilterOption) option;
                    addIntegerInput(name, intOpt.getMin(), intOpt.getMax(), intOpt.getValue(), (Integer val) -> {
                        filter.setOption(name, val);
                        if (instantFiltering) {
                            image.setFilter(filter);
                        }
                    });
                }
            });
        } else {
            haveOptions = false;
        }
    }

    private void updateOptions() {
        final int[] i = {0};
        filter.getOptions().forEach((name, option) -> {
            if (option instanceof IntegerFilterOption) {
                IntegerFilterOption intOpt = (IntegerFilterOption) option;
                componentMap.get(name).setValue(intOpt.getValue());
            }
        });
    }

    public void showDialog(Component parentComponent) {
        if (haveOptions) {
            updateOptions();
            if (instantFiltering) {
                image.setFilter(filter);

            }
            int retval = JOptionPane.showConfirmDialog(parentComponent, panel, filter.getName() + " Options",
                    instantFiltering ? JOptionPane.DEFAULT_OPTION : JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (!instantFiltering) {
                switch (retval) {
                    case JOptionPane.OK_OPTION:
                        image.setFilter(filter);
                    default:
                        //pass
                }
            }
        } else {
            image.setFilter(filter);
        }
    }

    private int intOptionsCount = 0;
    private void addIntegerInput(String name, int min, int max, int defaultValue, Consumer<Integer> onChange) {
        JLabel label = new JLabel(name);
        JFormattedTextField field = new JFormattedTextField(new DecimalFormat("0; (0)"));
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
        field.setColumns(Integer.max(Integer.toString(min).length(), Integer.toString(max).length()));
        field.setValue(defaultValue);
        field.addPropertyChangeListener("value", evt -> {
            if (evt.getNewValue() != null) {
                int value = ((Number) evt.getNewValue()).intValue();
                if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                    if (value < min || value > max) {
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
    }


    public Filter getFilter() {
        return filter;
    }

    public FilteredImage getImage() {
        return image;
    }
}
