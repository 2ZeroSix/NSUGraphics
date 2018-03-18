package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FieldView extends JLabel implements FieldObserver, DisplayModelObserver{
    private Controller controller;
    private ImageIcon icon = new ImageIcon();
    private MyImage image;
    private FieldCell fieldCell = new FieldCell();
    private FieldObservable field;
    public FieldView(Controller controller) {
        this.controller = controller;
        setIcon(icon);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
                    Point p = fieldCell.calculatePositionOnField(e.getPoint());
                    if (p != null) {
                        controller.touchCell(p.y, p.x, e.getButton() == MouseEvent.BUTTON1);
                    } else {
                        controller.clearTouch(e.getButton() != MouseEvent.BUTTON1);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
                    controller.clearTouch(e.getButton() == MouseEvent.BUTTON1);
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = fieldCell.calculatePositionOnField(e.getPoint());
                if (p != null) {
                    controller.touchCell(p.y, p.x);
                } else {
                    controller.clearTouch();
                }
            }
        });
        controller.addFieldObserver(this);
        controller.addDisplayModelObserver(this);
    }

    @Override
    public void updateField(FieldObservable field) {
        this.field = field;
        updateAll(field);
    }

    private void updateAll(FieldObservable field) {
        Point size = fieldCell.calculateSize(field.getHeight(), field.getWidth(0));
        image = new MyImage(size.x > 0 ? size.x : 1, size.y > 0 ? size.y: 1);
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.updateParams(field, row, column).draw(image);
            }
        }
        icon.setImage(image);
        revalidate();
        repaint();
    }

    @Override
    public void updateState(FieldObservable field, int row, int column) {
        fieldCell.updateParams(field, row, column).drawWithoutBorder(image);
        repaint(fieldCell.getX() - fieldCell.getRadius(),
                fieldCell.getY() - fieldCell.getRadius(),
                fieldCell.getRadius() * 2,
                fieldCell.getRadius() * 2);
    }

    @Override
    public void updateImpact(FieldObservable field, int row, int column) {
        fieldCell.updateParams(field, row, column).drawWithoutBorder(image);
        repaint(fieldCell.getX() - fieldCell.getRadius(),
                fieldCell.getY() - fieldCell.getRadius(),
                fieldCell.getRadius() * 2,
                fieldCell.getRadius() * 2);
    }

    @Override
    public void updateSize(FieldObservable field) {
        updateAll(field);
    }

    @Override
    public void updateImpact(FieldObservable field) {
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.updateParams(field, row, column).drawWithoutBorder(image);
            }
        }
        repaint();
    }

    @Override
    public void updateLifeBounds(FieldObservable field) {
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.updateParams(field, row, column).drawWithoutBorder(image);
            }
        }
        repaint();
    }

    @Override
    public void updateDisplay(DisplayModelObservable displayModel) {
        updateBorderWidth(displayModel);
        updateDisplayImpact(displayModel);
        updateHexagonSize(displayModel);
        updateFullColor(displayModel);
    }

    @Override
    public void updateBorderWidth(DisplayModelObservable displayModel) {
        fieldCell.setBorderWidth(displayModel.getBorderWidth());
        if (field != null)
            this.updateAll(field);
    }

    @Override
    public void updateHexagonSize(DisplayModelObservable displayModel) {
        fieldCell.setRadius(displayModel.getHexagonSize());
        if (field != null)
            this.updateAll(field);
    }

    @Override
    public void updateDisplayImpact(DisplayModelObservable displayModel) {
        fieldCell.setPrintImpact(displayModel.isDisplayImpact());
        if (field != null) {
            for (int row = 0; row < field.getHeight(); ++row) {
                for (int column = 0; column < field.getWidth(row); ++column) {
                    fieldCell.updateParams(field, row, column);
                    if(!displayModel.isDisplayImpact()){
                        fieldCell.cleanImpact(image);
                    }
                    fieldCell.drawWithoutBorder(image);
                }
            }
            repaint();
        }
    }

    @Override
    public void updateFullColor(DisplayModelObservable displayModel) {
        fieldCell.setFullColor(displayModel.isFullColor());
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                fieldCell.updateParams(field, row, column).drawWithoutBorder(image);
            }
        }
        repaint();
    }

}
