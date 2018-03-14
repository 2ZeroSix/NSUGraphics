package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class NewDocumentFrame extends JDialog implements FieldObserver {
    private Controller controller;
    private JFormattedTextField width;
    private JFormattedTextField height;
    public NewDocumentFrame(JFrame parent, Controller controller) {
        super(parent, "New Document", true);
        this.controller = controller;
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        DecimalFormat format = new DecimalFormat("0; ()");
        {
            JLabel wLabel = new JLabel("width");
            c.gridx = 0;
            c.gridy = 0;
            add(wLabel, c);
            width = new JFormattedTextField(format);
            width.setColumns(5);
//            width.setValue(10);
            c.gridx = 1;
            c.gridy = 0;
            add(width, c);
            JLabel hLabel = new JLabel("height");
            c.gridx = 0;
            c.gridy = 1;
            add(hLabel, c);
            height = new JFormattedTextField(format);
            height.setColumns(5);
//            height.setValue(10);
            c.gridx = 1;
            c.gridy = 1;
            add(height, c);
        }
        {
            JButton start = new JButton("start");
            start.addActionListener(ae -> {
                controller.newDocument(((Number)width.getValue()).intValue(),
                        ((Number)height.getValue()).intValue());
                setVisible(false);
            });
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            add(start, c);
        }
        controller.addFieldObserver(this);
        pack();
        setResizable(false);
    }

    @Override
    public void updateField(FieldObservable field) {
        updateImpact(field);
        updateSize(field);
        updateLifeBounds(field);
    }

    @Override
    public void updateState(FieldObservable field, int row, int column) {
    }

    @Override
    public void updateImpact(FieldObservable field, int row, int column) {
    }

    @Override
    public void updateSize(FieldObservable field) {
        width.setValue(field.getWidth(0));
        height.setValue(field.getHeight());
    }

    @Override
    public void updateLifeBounds(FieldObservable field) {
    }

    @Override
    public void updateImpact(FieldObservable field) {
    }
}
