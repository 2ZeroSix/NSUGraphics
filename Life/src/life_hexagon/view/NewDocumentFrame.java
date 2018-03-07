package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class NewDocumentFrame extends JFrame implements FieldObserver {
    private Controller controller;
    private JFormattedTextField width;
    private JFormattedTextField height;
    public NewDocumentFrame(Controller controller) {
        super("new document");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.controller = controller;
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
            c.fill = GridBagConstraints.HORIZONTAL;
            add(start, c);
        }
        pack();
        controller.addFieldObserver(this);
    }

    @Override
    public void updateField(FieldObservable fieldObservable) {
        updateImpact(fieldObservable);
        updateSize(fieldObservable);
        updateLifeBounds(fieldObservable);
    }

    @Override
    public void updateState(FieldObservable fieldObservable, int row, int column) {
    }

    @Override
    public void updateImpact(FieldObservable fieldObservable, int row, int column) {
    }

    @Override
    public void updateSize(FieldObservable fieldObservable) {
        width.setValue(fieldObservable.getWidth(0));
        height.setValue(fieldObservable.getHeight());
    }

    @Override
    public void updateLifeBounds(FieldObservable fieldObservable) {
    }

    @Override
    public void updateImpact(FieldObservable fieldObservable) {
    }
}
