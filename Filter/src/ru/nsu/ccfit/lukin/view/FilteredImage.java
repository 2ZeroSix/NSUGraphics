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
import java.util.Objects;
import java.util.Set;

public class FilteredImage extends ImagePanel implements FilteredImageObservable, ImageObserver{
    private SelectedImage selectedImage;
    private Set<FilteredImageObserver>  filteredImageObservers  = new HashSet<>();
    private Filter filter;
    private Thread updater;
    private boolean updated = false;
    private BufferedImage tmpImage = null;
    private final Object update = new Object();
    private final Object paint = new Object();
    public FilteredImage(SelectedImage selectedImage) {
        this.selectedImage = selectedImage;
        selectedImage.addImageObserver(this);
        setMaximumSize(selectedImage.getMaximumSize());
        setSize(selectedImage.getSize());
        setPreferredSize(selectedImage.getPreferredSize());
        setMinimumSize(selectedImage.getMinimumSize());
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));
        updater = new Thread(() -> {
            try {
                while(true) {
                    try {
                        BufferedImage image;
//                    setIgnoreRepaint(true);
                        synchronized (update) {
                            while (!updated) {
                                update.wait();
                            }
                            updated = false;

                            image = selectedImage.getImage();
                            if (image != null && filter != null) {
                                if (tmpImage == null) tmpImage = ImageUtils.copy(image);
                                else tmpImage = ImageUtils.copy(image, tmpImage);
                            } else {
                                tmpImage = null;
                            }
                        }
                        if (tmpImage != null && filter != null) {
                            filter.apply(tmpImage);
                        }
                        swapImage();
//                    setIgnoreRepaint(false);
//                    applyFilter();
                        notifyAppliedFilter();
                    } catch (InterruptedException e) {
                        throw e;
                    } catch (Exception ignore) {
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        updater.start();
    }

    private void swapImage() {
        BufferedImage tmp = getImage();
        setImage(tmpImage);
        tmpImage = tmp;
    }

//    private void applyFilter() {
//        BufferedImage image = selectedImage.getImage();
//        if (image != null && filter != null) {
//            if (getImage() == null) image = ImageUtils.copy(image);
//            else                    image = ImageUtils.copy(image, getImage());
//            filter.apply(image);
//        }
//        setImage(image);
//    }

    public FilteredImage setFilter(Filter filter) {
//        if (!Objects.equals(this.filter, filter)) {
            this.filter = filter;
            synchronized (update) {
                updated = true;
                update.notify();
            }
//            applyFilter();
//        }
        return this;
    }

    @Override
    public Filter getFilter() {
        return filter;
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
        setFilter(getFilter());
    }
}
