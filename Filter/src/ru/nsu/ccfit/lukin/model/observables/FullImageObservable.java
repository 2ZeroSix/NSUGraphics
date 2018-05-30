package ru.nsu.ccfit.lukin.model.observables;

import java.awt.*;

public interface FullImageObservable extends ImageObservable {
    Rectangle getSelection();
    boolean isSelectable();

    void notifySelectable();
    void notifySelection();
}
