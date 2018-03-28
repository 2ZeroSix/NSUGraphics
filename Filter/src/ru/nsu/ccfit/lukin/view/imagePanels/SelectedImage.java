package ru.nsu.ccfit.lukin.view.imagePanels;

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
        if (observable.isSelectable()) {
            Rectangle selection = observable.getSelection();
            try {
                if (selection != null && observable.getImage() != null && selection.getWidth() > 0 && selection.getHeight() > 0) {
                    setImage(observable.getImage().getSubimage(selection.x, selection.y, selection.width, selection.height));
                    notifyImage();
                }
            } catch (RasterFormatException ignore) {
            }
        }
    }

    @Override
    public void updateSelectable(FullImageObservable observable) {
        updateSelection(observable);
    }

    @Override
    public void updateImage(ImageObservable observable) {
    }
}
