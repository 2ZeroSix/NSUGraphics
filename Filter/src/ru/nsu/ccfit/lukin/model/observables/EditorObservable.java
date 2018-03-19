package ru.nsu.ccfit.lukin.model.observables;

import ru.nsu.ccfit.lukin.model.filters.Filter;
import ru.nsu.ccfit.lukin.view.observers.EditorObserver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

public interface EditorObservable {
    BufferedImage getSourceImage();
    BufferedImage getSelectedImage();
    BufferedImage getFilteredImage();
    Rectangle getSelection();
    Collection<Filter> getAppliedFilters();

    default boolean isFiltered() {
        return !getAppliedFilters().isEmpty();
    }




    Collection<EditorObserver> getObservers();
    default void notifyEditor() {
        for (EditorObserver observer:
             getObservers()) {

        }
    }
    default void notifySourceImage() {
        for (EditorObserver observer:
                getObservers()) {

        }
    }
    default void notifySelectedImage(){
        for (EditorObserver observer:
                getObservers()) {

        }
    }
    default void notifyFilteredImage(){
        for (EditorObserver observer:
                getObservers()) {

        }
    }
    default void notifySelection() {
        for (EditorObserver observer:
                getObservers()) {

        }
    }
    default void notifyFilters() {
        for (EditorObserver observer:
                getObservers()) {

        }
    }
    default void addObserver(EditorObserver observer) {
        getObservers().add(observer);
    }
    default void removeObserver(EditorObserver observer) {
        getObservers().remove(observer);
    }
}
