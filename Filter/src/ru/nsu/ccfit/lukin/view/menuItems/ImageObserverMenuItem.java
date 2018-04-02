package ru.nsu.ccfit.lukin.view.menuItems;

import ru.nsu.ccfit.lukin.model.observables.FilteredImageObservable;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.observers.FilteredImageObserver;
import ru.nsu.ccfit.lukin.view.observers.FullImageObserver;

import javax.swing.*;

public class ImageObserverMenuItem extends JMenuItem implements FullImageObserver, FilteredImageObserver {

    public ImageObserverMenuItem(String name, ImageObservable... imageObservables) {
        super(name);
        for (ImageObservable imageObservable : imageObservables) {
            imageObservable.addImageObserver(this);
            updateImage(imageObservable);
        }
    }

    @Override
    public void updateAppliedFilter(FilteredImageObservable observable) {

    }

    @Override
    public void updateSelection(FullImageObservable observable) {

    }

    @Override
    public void updateSelectable(FullImageObservable observable) {

    }

    @Override
    public void updateImage(ImageObservable observable) {

    }
}
