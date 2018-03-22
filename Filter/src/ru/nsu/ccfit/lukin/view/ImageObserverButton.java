package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.observables.FilteredImageObservable;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.observers.FilteredImageObserver;
import ru.nsu.ccfit.lukin.view.observers.FullImageObserver;
import ru.nsu.ccfit.lukin.view.observers.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

public class ImageObserverButton extends JButton implements FullImageObserver, FilteredImageObserver, ImageObserver {
    private static final String[] suffixes = {
            "",
            "-selected",
            "-disabled",
            "-pressed",
            "-rollover",
            "-disabled-selected",
            "-rollover-selected",
    };
    private static final String[] extensions = {
            "png",
            "bmp",
            "svg",
            "jpg",
            "jpeg"
    };

    private final Consumer<Icon>[] iconSetter = (Consumer<Icon>[]) new Consumer[]{
            (Consumer<Icon>) this::setIcon,
            (Consumer<Icon>) this::setSelectedIcon,
            (Consumer<Icon>) this::setDisabledIcon,
            (Consumer<Icon>) this::setPressedIcon,
            (Consumer<Icon>) this::setRolloverIcon,
            (Consumer<Icon>) this::setDisabledSelectedIcon,
            (Consumer<Icon>) this::setRolloverSelectedIcon,

    };

    public ImageObserverButton(String iconName, ImageObservable... imageObservables) {
        for (ImageObservable imageObservable : imageObservables) {
            imageObservable.addImageObserver(this);
        }
        setToolTipText(iconName);
        for (int i = 0; i < suffixes.length; ++i) {
            for (String extension : extensions) {
                try {
                    iconSetter[i].accept(new ImageIcon(
                            ImageIO.read(getClass()
                                    .getResource("/icons/" + iconName + suffixes[i] + "." + extension))));
                } catch (IllegalArgumentException | IOException ignore) {
                }
            }
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
