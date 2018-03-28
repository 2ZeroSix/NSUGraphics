package ru.nsu.ccfit.lukin.view.menuItems;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.FilterOptionsDialog;
import ru.nsu.ccfit.lukin.view.imagePanels.FilteredImage;
import ru.nsu.ccfit.lukin.view.imagePanels.SelectedImage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FilterMenuItem extends ImageObserverMenuItem implements ActionListener {
    private Component parent;
    private FilterOptionsDialog dialog;

    public FilterMenuItem(Component parent, Filter filter, SelectedImage selectedImage, FilteredImage filteredImage) {
        super(filter.getName(), selectedImage);
        this.parent = parent;
        this.dialog = new FilterOptionsDialog(filter, filteredImage);
        addActionListener(this);
        setEnabled(selectedImage.getImage() != null);
    }

    public Filter getFilter() {
        return dialog.getFilter();
    }

    @Override
    public void updateImage(ImageObservable observable) {
        setEnabled(dialog.getFilteredImage().getSelectedImage().getImage() != null);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        dialog.showDialog(parent);
    }
}
