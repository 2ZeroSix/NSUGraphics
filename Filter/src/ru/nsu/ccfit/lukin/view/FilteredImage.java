package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.*;
import ru.nsu.ccfit.lukin.model.observables.FilteredImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.observers.FilteredImageObserver;
import ru.nsu.ccfit.lukin.view.observers.ImageObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class FilteredImage extends ImagePanel implements FilteredImageObservable, ImageObserver{
    private SelectedImage selectedImage;
    private Set<FilteredImageObserver>  filteredImageObservers  = new HashSet<>();
    private Filter                      appliedFilter;
//    private Thread updater;
//    private boolean updated = false;
//    private Object update = new Object();
    public FilteredImage(SelectedImage selectedImage) {
        this.selectedImage = selectedImage;
        selectedImage.addImageObserver(this);
        setMaximumSize(selectedImage.getMaximumSize());
        setSize(selectedImage.getSize());
        setPreferredSize(selectedImage.getPreferredSize());
        setMinimumSize(selectedImage.getMinimumSize());
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        setAppliedFilter(new SobelFilter(30));
//        updater = new Thread(() -> {
//            try {
//                while(true) {
//                    synchronized (update) {
//                        while (!updated) {
//                            update.wait();
//                        }
//                        updated = false;
//                    }
//                    applyFilter();
//                    notifyAppliedFilter();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        updater.start();
    }

    private void applyFilter() {
        BufferedImage image = selectedImage.getImage();
        if (image != null && appliedFilter != null) {
            if (getImage() == null) image = ImageUtils.copy(image);
            else                    image = ImageUtils.copy(image, getImage());
            appliedFilter.apply(image);
        }
        setImage(image);
    }

    public FilteredImage setAppliedFilter(Filter appliedFilter) {
        this.appliedFilter = appliedFilter;
//        synchronized (update) {
//            updated = true;
//            update.notify();
//        }
        applyFilter();
        return this;
    }

    @Override
    public Filter getAppliedFilter() {
        return appliedFilter;
    }

    @Override
    public void notifyAppliedFilter() {
        for (FilteredImageObserver filteredImageObserver : filteredImageObservers) {
            filteredImageObserver.updateAppliedFilter(this);
        }
    }

    @Override
    public void addImageObserver(ImageObserver imageObserver) {
        if (imageObserver instanceof FilteredImageObserver) {
            filteredImageObservers.add((FilteredImageObserver) imageObserver);
        }
        super.addImageObserver(imageObserver);
    }

    @Override
    public void removeImageObserver(ImageObserver imageObserver) {
        filteredImageObservers.remove(imageObserver);
        super.removeImageObserver(imageObserver);
    }

    @Override
    public void updateImage(ImageObservable observable) {
        setAppliedFilter(getAppliedFilter());
    }
}
