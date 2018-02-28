package life_hexagon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

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
    public void updateFull(Field field) {

    }

    @Override
    public void updateField(Field field) {

    }

    @Override
    public void updateState(Field field, int row, int column) {

    }

    @Override
    public void updateImpact(Field field, int row, int column) {

    }

    @Override
    public void updateImpact(Field field) {

    }

    @Override
    public void updateLifeBounds(Field field) {

    }
}
