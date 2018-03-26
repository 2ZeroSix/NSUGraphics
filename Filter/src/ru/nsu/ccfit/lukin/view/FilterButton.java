package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.Filter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class FilterButton extends ImageObserverButton implements ActionListener {
    private Component parent;
    private FilterOptionsDialog dialog;

    public FilterButton(Component parent, FilterOptionsDialog dialog) {
        super(dialog.getFilter().getName(), dialog.getImage()); this.parent = parent;
        this.dialog = dialog;
        addActionListener(this);
    }

    public Filter getFilter() {
        return dialog.getFilter();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        dialog.showDialog(parent);
    }
}
