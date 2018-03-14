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
import java.text.DecimalFormat;
import java.util.Objects;

public class OptionsFrame extends JDialog implements FieldObserver, EditModelObserver, DisplayModelObserver {
    private FieldObservable field;
    private DisplayModelObservable display;
    private EditModelObservable edit;
    private Controller controller;
    private JFormattedTextField width;
    private JFormattedTextField height;
    private ButtonGroup group;
    private JRadioButton xorButton;
    private JRadioButton replaceButton;
    private JSlider cellSizeSlider;
    private JFormattedTextField cellSizeField;
    private JSlider borderSizeSlider;
    private JFormattedTextField borderSizeField;
    private JToggleButton displayImpactButton;

    public OptionsFrame(JFrame parent, Controller controller) {
        super(parent, "Options", true);
        this.controller = controller;
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        DecimalFormat format = new DecimalFormat("0; ()");
        {
            {
                JLabel wLabel = new JLabel("width ");
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 1;
                add(wLabel, c);
            }
            {
                width = new JFormattedTextField(format);
                width.setColumns(5);
                width.addPropertyChangeListener("value", evt -> {
                    if (!Objects.equals(evt.getOldValue(), evt.getNewValue()))
                        controller.resize(((Number) width.getValue()).intValue(), field.getHeight());
                });
                c.gridx = 1;
                c.gridy = 0;
                c.gridwidth = 1;
                add(width, c);
            }
            {
                JLabel hLabel = new JLabel("height ");
                c.gridx = 3;
                c.gridy = 0;
                c.gridwidth = 1;
                add(hLabel, c);
            }
            {
                height = new JFormattedTextField(format);
                height.setColumns(5);
                height.addPropertyChangeListener("value", evt -> {
                    if (!Objects.equals(evt.getOldValue(), evt.getNewValue()))
                        controller.resize(field.getWidth(0), ((Number) evt.getNewValue()).intValue());
                });
                c.gridx = 4;
                c.gridy = 0;
                c.gridwidth = 1;
                add(height, c);
            }
            {
                JLabel csLabel = new JLabel("cell size");
                c.gridx = 0;
                c.gridy = 1;
                c.gridwidth = 2;
                add(csLabel, c);
            }
            {
                cellSizeSlider = new JSlider(JSlider.VERTICAL, 2, 100, 2);

                cellSizeSlider.addChangeListener(l -> controller.setHexagonSize(cellSizeSlider.getValue()));
                c.gridx = 0;
                c.gridy = 2;
                c.gridwidth = 1;
                add(cellSizeSlider, c);
            }
            {
                cellSizeField = new JFormattedTextField(format);
                cellSizeField.setColumns(5);
                cellSizeField.addPropertyChangeListener("value", evt -> {
                    if (evt.getNewValue() != null) {
                        int value = ((Number) evt.getNewValue()).intValue();
                        if (2 <= value && value <= 100 &&
                                !Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                            controller.setHexagonSize(value);
                        } else {
                            cellSizeField.setValue(evt.getOldValue());
                        }
                    }
                });
                c.gridx = 1;
                c.gridy = 2;
                c.gridwidth = 1;
                add(cellSizeField, c);
            }
            {
                JLabel bsLabel = new JLabel("border size");
                c.gridx = 3;
                c.gridy = 1;
                c.gridwidth = 2;
                add(bsLabel, c);
            }
            {
                borderSizeSlider = new JSlider(JSlider.VERTICAL,1, 100, 1);
                borderSizeSlider.addChangeListener(l -> controller.setBorderWidth(borderSizeSlider.getValue()));
                c.gridx = 3;
                c.gridy = 2;
                c.gridwidth = 1;
                add(borderSizeSlider, c);

                borderSizeField = new JFormattedTextField(format);
                borderSizeField.setColumns(5);
                borderSizeField.addPropertyChangeListener("value", evt -> {
                    if (evt.getNewValue() != null) {
                        int value = ((Number) evt.getNewValue()).intValue();
                        if (1 <= value && value <= 100 &&
                                !Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                            controller.setBorderWidth(((Number) evt.getNewValue()).intValue());
                        } else {
                            borderSizeField.setValue(evt.getOldValue());
                        }
                    }
                });
                c.gridx = 4;
                c.gridy = 2;
                c.gridwidth = 1;
                add(borderSizeField, c);
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
                c.gridy = 3;
                c.gridwidth = 2;
                add(xorButton, c);
                c.gridx = 3;
                c.gridy = 3;
                c.gridwidth = 2;
                add(replaceButton, c);
            }
            {
                displayImpactButton = new JToggleButton("impact");
                displayImpactButton.addActionListener(ae -> {
                    controller.toggleDisplayImpact();
                });
                c.gridx = 2;
                c.gridy = 4;
                c.gridwidth = 1;
                c.fill = GridBagConstraints.BOTH;
                add(displayImpactButton, c);
            }
        }
        controller.addDisplayModelObserver(this);
        controller.addFieldObserver(this);
        controller.addEditModelObserver(this);
        pack();
        setResizable(false);
    }

    @Override
    public void updateDisplay(DisplayModelObservable displayModel) {
        updateBorderWidth(displayModel);
        updateDisplayImpact(displayModel);
        updateHexagonSize(displayModel);
    }

    @Override
    public void updateBorderWidth(DisplayModelObservable displayModel) {
        int border = displayModel.getBorderWidth();
        if (borderSizeField.getValue() == null ||
                border != ((Number)borderSizeField.getValue()).intValue())
            borderSizeField.setValue(border);
        if (border != borderSizeSlider.getValue())
            borderSizeSlider.setValue(border);
    }

    @Override
    public void updateHexagonSize(DisplayModelObservable displayModel) {
        int hexagon = displayModel.getHexagonSize();
        if (cellSizeField.getValue() == null ||
                hexagon != ((Number)cellSizeField.getValue()).intValue())
            cellSizeField.setValue(hexagon);
        if (hexagon != cellSizeSlider.getValue())
            cellSizeSlider.setValue(hexagon);

    }

    @Override
    public void updateDisplayImpact(DisplayModelObservable displayModel) {
        displayImpactButton.setSelected(displayModel.isDisplayImpact());
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
    public void updateField(FieldObservable field) {
        this.field = field;
        updateImpact(field);
        updateLifeBounds(field);
        updateSize(field);
    }

    @Override
    public void updateState(FieldObservable field, int row, int column) {}

    @Override
    public void updateImpact(FieldObservable field, int row, int column) {}

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
