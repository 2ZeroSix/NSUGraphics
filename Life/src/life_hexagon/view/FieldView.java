package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FieldView extends JLabel implements FieldObserver, DisplayModelObserver {
    Controller controller;
    ImageIcon icon = new ImageIcon();
    MyImage image;
    FieldCell fieldCell = new FieldCell();

    public FieldView(Controller controller) {
        this.controller = controller;
        controller.addDisplayModelObserver(this);
        controller.addDisplayModelObserver(fieldCell);
        controller.addFieldObserver(this);
        setIcon(icon);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = fieldCell.calculatePositionOnField(e.getPoint());
                if (p != null) {
                    controller.touchCell(p.y, p.x);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                controller.clearTouch();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = fieldCell.calculatePositionOnField(e.getPoint());
                if (p != null) {
                    controller.touchCell(p.y, p.x);
                }
            }
        });
    }

    @Override
    public void updateField(FieldObservable field) {
        updateAll(field);
    }

    private void updateAll(FieldObservable field) {
        Point size = fieldCell.calculateSize(field.getHeight(), field.getWidth(0));
        image = new MyImage(size.x > 0 ? size.x : 1, size.y > 0 ? size.y: 1);
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.draw(image, field, row, column);
            }
        }
        icon.setImage(image);
        repaint();
    }

    @Override
    public void updateState(FieldObservable field, int row, int column) {
        fieldCell.draw(image, field, row, column);
        repaint();
    }

    @Override
    public void updateImpact(FieldObservable field, int row, int column) {
        fieldCell.draw(image, field, row, column);
        repaint();
    }

    @Override
    public void updateSize(FieldObservable field) {
        updateAll(field);
    }

    @Override
    public void updateImpact(FieldObservable field) {
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.draw(image, field, row, column);
            }
        }
        repaint();
    }

    @Override
    public void updateLifeBounds(FieldObservable field) {
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.draw(image, field, row, column);
            }
        }
        repaint();
    }

    @Override
    public void updateDisplayMode(DisplayModelObservable displayModel) {
    }

    @Override
    public void updateBorderWidth(DisplayModelObservable displayModel) {
    }

    @Override
    public void updateHexagonSize(DisplayModelObservable displayModel) {
    }

    @Override
    public void updateDisplayImpact(DisplayModelObservable displayModel) {
    }
}
