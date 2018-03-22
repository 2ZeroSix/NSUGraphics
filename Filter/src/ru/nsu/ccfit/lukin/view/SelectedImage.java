package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.observers.FullImageObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.image.RasterFormatException;

public class SelectedImage extends ImagePanel implements FullImageObserver, ImageObservable {
    private final FullImage fullImage;

    public SelectedImage(FullImage fullImage) {
        this.fullImage = fullImage;
        fullImage.addImageObserver(this);
        setMaximumSize(fullImage.getMaximumSize());
        setSize(fullImage.getSize());
        setPreferredSize(fullImage.getPreferredSize());
        setMinimumSize(fullImage.getMinimumSize());
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));
    }

    @Override
    public void updateSelection(FullImageObservable observable) {
        Rectangle selection = observable.getSelection();
        try {
            if (observable.getImage() != null) {
                setImage(observable.getImage().getSubimage(selection.x, selection.y, selection.width, selection.height));
                revalidate();
            }
        } catch (RasterFormatException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateSelectable(FullImageObservable observable) {
    }

    @Override
    public void updateImage(ImageObservable observable) {
        setImage(null);
    }
}
