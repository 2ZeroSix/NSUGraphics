package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.observables.FieldObservable;
import life_hexagon.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;

public class FieldView extends JLabel implements FieldObserver {
    Controller controller;
    MyImage image;
    public FieldView() {
        setIcon(new ImageIcon(image));
    }

    @Override
    public void paintComponent(Graphics graphics) {
    }

    @Override
    public void updateFull(FieldObservable fieldFieldObservable) {

    }

    @Override
    public void updateField(FieldObservable fieldFieldObservable) {

    }

    @Override
    public void updateState(FieldObservable fieldFieldObservable, int row, int column) {

    }

    @Override
    public void updateImpact(FieldObservable fieldFieldObservable, int row, int column) {

    }

    @Override
    public void updateImpact(FieldObservable fieldFieldObservable) {

    }

    @Override
    public void updateLifeBounds(FieldObservable fieldFieldObservable) {

    }
}
