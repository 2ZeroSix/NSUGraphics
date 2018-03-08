package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.EditModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.EditModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

public class OptionsFrame extends JFrame implements FieldObserver, EditModelObserver, DisplayModelObserver {
    private FieldObservable field;
    private DisplayModelObservable display;
    private EditModelObservable edit;
    private Controller controller;
    private JFormattedTextField width;
    private JFormattedTextField height;
    private JFormattedTextField borderWidth;
    private JFormattedTextField hexSize;
    private ButtonGroup group;
    private JRadioButton xorButton;
    private JRadioButton replaceButton;
    public OptionsFrame(Controller controller) {
        super("Options");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.controller = controller;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        DecimalFormat format = new DecimalFormat("0; ()");
        {
            {
                JLabel wLabel = new JLabel("width");
                c.gridx = 0;
                c.gridy = 0;
                add(wLabel, c);
            }
            {
                width = new JFormattedTextField(format);
                width.setColumns(5);
                width.addPropertyChangeListener("value", evt -> {
                    controller.resize(((Number) width.getValue()).intValue(), field.getHeight());
                });
                c.gridx = 1;
                c.gridy = 0;
                add(width, c);
            }
            {
                JLabel hLabel = new JLabel("height");
                c.gridx = 0;
                c.gridy = 1;
                add(hLabel, c);
            }
            {
                height = new JFormattedTextField(format);
                height.setColumns(5);
                height.addPropertyChangeListener("value", evt -> {
                    controller.resize(field.getWidth(0), ((Number) height.getValue()).intValue());
                });
                c.gridx = 1;
                c.gridy = 1;
                add(height, c);
            }
            {
                group = new ButtonGroup();
                xorButton = new JRadioButton("xor");
                xorButton.addActionListener(ae -> {
                    controller.toggleXORMode(true);
                    if (!xorButton.isSelected()) {
                        xorButton.setSelected(true);
                    }
                });
                replaceButton = new JRadioButton("replace");
                replaceButton.addActionListener(ae -> {
                    controller.toggleXORMode(false);
                    if (!replaceButton.isSelected()) {
                        replaceButton.setSelected(true);
                    }
                });
                c.gridx = 0;
                c.gridy = 2;
                add(xorButton, c);
                c.gridx = 1;
                c.gridy = 2;
                add(replaceButton, c);
            }
        }
        controller.addDisplayModelObserver(this);
        controller.addFieldObserver(this);
        controller.addEditModelObserver(this);
        pack();
    }

    @Override
    public void updateDisplay(DisplayModelObservable displayModel) {

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

    @Override
    public void updateEdit(EditModelObservable editModel) {
        this.edit = editModel;
        updateXOR(editModel);
        updateLoop(editModel);
    }

    @Override
    public void updateXOR(EditModelObservable editModel) {
        if (editModel.isXOR()) {
            group.setSelected(xorButton.getModel(), true);
        } else {
            group.setSelected(replaceButton.getModel(), true);

        }
    }

    @Override
    public void updateLoop(EditModelObservable editModel) {

    }

    @Override
    public void updateField(FieldObservable fieldObservable) {
        this.field = fieldObservable;
        updateImpact(fieldObservable);
        updateLifeBounds(fieldObservable);
        updateSize(fieldObservable);
    }

    @Override
    public void updateState(FieldObservable fieldObservable, int row, int column) {}

    @Override
    public void updateImpact(FieldObservable fieldObservable, int row, int column) {}

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
