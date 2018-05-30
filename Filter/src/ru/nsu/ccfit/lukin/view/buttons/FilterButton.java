package ru.nsu.ccfit.lukin.view.buttons;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.model.observables.FilteredImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.FilterOptionsDialog;
import ru.nsu.ccfit.lukin.view.imagePanels.FilteredImage;
import ru.nsu.ccfit.lukin.view.imagePanels.SelectedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class FilterButton extends ImageObserverButton implements ActionListener {
    protected final Component parent;
    protected final FilterOptionsDialog dialog;

    public FilterButton(Component parent, Filter filter, FilteredImage filteredImage) {
        super(filter.getName(), filteredImage.getSelectedImage(), filteredImage);
        this.parent = parent;
        dialog = new FilterOptionsDialog(filter, filteredImage);
        addActionListener(this);
    }

    public Filter getFilter() {
        return dialog.getFilter();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        dialog.showDialog(parent);
    }

    @Override
    public void updateImage(ImageObservable observable) {
        try {
            setEnabled(dialog.getFilteredImage().getSelectedImage().getImage() != null);
        } catch (NullPointerException e) {
            setEnabled(false);
        }
    }

    @Override
    public void updateAppliedFilter(FilteredImageObservable observable) {
        setSelected(Objects.equals(getFilter(), observable.getFilter()));
    }
}
