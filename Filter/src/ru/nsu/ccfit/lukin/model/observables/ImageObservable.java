package ru.nsu.ccfit.lukin.model.observables;

import ru.nsu.ccfit.lukin.view.observers.ImageObserver;

import java.awt.image.BufferedImage;

public interface ImageObservable {
    BufferedImage getImage();
    void notifyImage();

    void addImageObserver(ImageObserver image);
    void removeImageObserver(ImageObserver image);
}
