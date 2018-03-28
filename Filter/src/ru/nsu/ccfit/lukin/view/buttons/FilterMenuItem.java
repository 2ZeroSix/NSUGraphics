package ru.nsu.ccfit.lukin.view.buttons;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.view.FilterOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FilterMenuItem extends JMenuItem implements ActionListener {
    private Component parent;
    private FilterOptionsDialog dialog;

    public FilterMenuItem(Component parent, FilterOptionsDialog dialog) {
        super(dialog.getFilter().getName());
        this.parent = parent;
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
