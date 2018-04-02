package ru.nsu.ccfit.lukin.view.imagePanels;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.Filter;
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
    private Filter filter;
    private Thread updater;
    private boolean updated = false;
    private BufferedImage tmpImage = null;
    private final Object update = new Object();
    private boolean autoUpdate = true;
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
                //noinspection InfiniteLoopStatement
                while(true) {
                    try {
                        BufferedImage image;
                        synchronized (update) {
                            while (!updated) {
                                update.wait();
                            }
                            updated = false;

                            image = selectedImage.getImage();
                            if (image != null) {
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
                        notifyAppliedFilter();
                    } catch (InterruptedException e) {
                        throw e;
                    } catch (Exception e) {
                        e.printStackTrace();
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

    public FilteredImage setFilter(Filter filter) {
        this.filter = filter;
        if (autoUpdate) {
            applyFilter();
        }
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
        if (imageObserver instanceof FilteredImageObserver)
            filteredImageObservers.remove(imageObserver);
        super.removeImageObserver(imageObserver);
    }

    @Override
    public void updateImage(ImageObservable observable) {
        if (autoUpdate) applyFilter();
    }

    public SelectedImage getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(SelectedImage selectedImage) {
        this.selectedImage = selectedImage;
    }

    @Override
    public void clean() {
        setFilter(null);
        super.clean();
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public void applyFilter() {
        synchronized (update) {
            updated = true;
            update.notify();
        }
    }
}
