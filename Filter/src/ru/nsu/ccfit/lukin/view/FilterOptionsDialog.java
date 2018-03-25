package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.filters.options.IntegerFilterOption;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class FilterOptionsDialog extends JPanel {
    public FilterOptionsDialog(Filter filter, FilteredImage image, boolean instantFiltering) {
        super(new GridBagLayout());
        Map<String, FilterOption<?>> options = filter.getOptions();
        int intOptionsCount = 0;
        options.forEach((name, option) -> {
            if (option instanceof IntegerFilterOption) {
                ContainerUtils.add()
            }
        });
    }
}
