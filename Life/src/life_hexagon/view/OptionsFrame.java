package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.EditModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.EditModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Objects;

public class OptionsFrame extends JDialog implements FieldObserver, EditModelObserver, DisplayModelObserver {

    private FieldObservable field;
    private DisplayModelObservable display;
    private EditModelObservable edit;

    private JPanel sizePanel;
    private JPanel rulesPanel;
    private JPanel displayPanel;
    private JPanel editPanel;

    private JLabel widthLabel;
    private JLabel heightLabel;
    private JLabel cellSizeLabel;
    private JLabel borderSizeLabel;
    private Controller controller;
    private JFormattedTextField widthField;
    private JFormattedTextField heightField;
    private ButtonGroup group;
    private JRadioButton xorButton;
    private JRadioButton replaceButton;
    private JSlider cellSizeSlider;
    private JFormattedTextField cellSizeField;
    private JSlider borderSizeSlider;
    private JFormattedTextField borderSizeField;
    private JToggleButton displayImpactButton;
    private JToggleButton fullColorButton;
    private JLabel liveBeginLabel;
    private JLabel birthBeginLabel;
    private JLabel birthEndLabel;
    private JLabel liveEndLabel;
    private JLabel firstImpactLabel;
    private JLabel secondImpactLabel;
    private JFormattedTextField liveBeginField;
    private JFormattedTextField birthBeginField;
    private JFormattedTextField birthEndField;
    private JFormattedTextField liveEndField;
    private JFormattedTextField firstImpactField;
    private JFormattedTextField secondImpactField;

    private void add(Container container, JComponent component, int x, int y, int width, int height) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5 , 5);
        container.add(component, c);
    }

    private void initComponents() {
        sizePanel = new JPanel(new GridBagLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder("Field size"));

        rulesPanel = new JPanel(new GridBagLayout());
        rulesPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
//        rulesPanel.setBorder();
        rulesPanel.createToolTip().setTipText("hello");
        displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Display options"));

        editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit options"));

        DecimalFormat positiveNatural = new DecimalFormat("0; ()");
        DecimalFormat positiveFloat = new DecimalFormat("0.00; ()");

        widthLabel = new JLabel("width ");
        heightLabel = new JLabel("height ");
        cellSizeLabel = new JLabel("cell size");
        borderSizeLabel = new JLabel("border size");

        widthField = new JFormattedTextField(positiveNatural);
        widthField.setColumns(5);
        widthField.addPropertyChangeListener("value", evt -> {
            if (!Objects.equals(evt.getOldValue(), evt.getNewValue()))
                controller.resize(((Number) widthField.getValue()).intValue(), field.getHeight());
        });

        heightField = new JFormattedTextField(positiveNatural);
        heightField.setColumns(5);
        heightField.addPropertyChangeListener("value", evt -> {
            if (!Objects.equals(evt.getOldValue(), evt.getNewValue()))
                controller.resize(field.getWidth(0), ((Number) evt.getNewValue()).intValue());
        });

        cellSizeField = new JFormattedTextField(positiveNatural);
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

        borderSizeField = new JFormattedTextField(positiveNatural);
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

        cellSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 100, 2);
        cellSizeSlider.addChangeListener(l -> controller.setHexagonSize(cellSizeSlider.getValue()));

        borderSizeSlider = new JSlider(JSlider.HORIZONTAL,1, 100, 1);
        borderSizeSlider.addChangeListener(l -> controller.setBorderWidth(borderSizeSlider.getValue()));

        group = new ButtonGroup();

        xorButton = new JRadioButton("xor");
        xorButton.addActionListener(ae -> controller.toggleXORMode(true));
        group.add(xorButton);

        replaceButton = new JRadioButton("replace");
        replaceButton.addActionListener(ae -> controller.toggleXORMode(false));
        group.add(replaceButton);

        displayImpactButton = new JToggleButton("impact");
        displayImpactButton.addActionListener(ae -> controller.toggleDisplayImpact());

        fullColorButton = new JToggleButton("extended colors");
        fullColorButton.addActionListener(ae -> controller.toggleFullColor());

        birthBeginLabel = new JLabel("Birth begin");
        birthEndLabel = new JLabel("Birth end");
        liveBeginLabel = new JLabel("Live begin");
        liveEndLabel = new JLabel("Live end");


        birthBeginField = new JFormattedTextField(positiveFloat);
        birthBeginField.setColumns(5);
        birthEndField = new JFormattedTextField(positiveFloat);
        birthEndField.setColumns(5);
        liveBeginField = new JFormattedTextField(positiveFloat);
        liveBeginField.setColumns(5);
        liveEndField = new JFormattedTextField(positiveFloat);
        liveEndField.setColumns(5);
        PropertyChangeListener liveBoundsListener = evt -> {
            if (    !Objects.equals(evt.getOldValue(), evt.getNewValue()) &&
                    birthBeginField.getValue() != null &&
                    birthEndField.getValue() != null &&
                    liveEndField.getValue() != null &&
                    liveBeginField.getValue() != null) {
                try {
                    controller.setLifeBounds(((Number) liveBeginField.getValue()).floatValue(),
                            ((Number) birthBeginField.getValue()).floatValue(),
                            ((Number) birthEndField.getValue()).floatValue(),
                            ((Number) liveEndField.getValue()).floatValue());
                    rulesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GREEN), "Rules"));
                } catch (IllegalArgumentException e) {
                    rulesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Rules (" + e.getLocalizedMessage() + ")"));
                }
            }
        };
        birthBeginField.addPropertyChangeListener("value", liveBoundsListener);
        birthEndField.addPropertyChangeListener("value", liveBoundsListener);
        liveBeginField.addPropertyChangeListener("value", liveBoundsListener);
        liveEndField.addPropertyChangeListener("value", liveBoundsListener);

        firstImpactLabel = new JLabel("First impact");
        secondImpactLabel = new JLabel("Second impact");

        firstImpactField = new JFormattedTextField(positiveFloat);
        firstImpactField.setColumns(5);
        firstImpactField.addPropertyChangeListener("value", evt -> {
            if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                controller.setFirstImpact(((Number)evt.getNewValue()).floatValue());
            }
        });
        secondImpactField = new JFormattedTextField(positiveFloat);
        secondImpactField.setColumns(5);
        secondImpactField.addPropertyChangeListener("value", evt -> {
            if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                controller.setSecondImpact(((Number)evt.getNewValue()).floatValue());
            }
        });
    }

    private void locateComponents() {
        add(this, sizePanel, 0, 0, 1, 1);
        add(sizePanel, widthLabel,          0, 0, 1, 1);
        add(sizePanel, widthField,          0, 1, 1, 1);
        add(sizePanel, heightLabel,         1, 0, 1, 1);
        add(sizePanel, heightField,         1, 1, 1, 1);
        add(this, displayPanel, 0, 1, 2, 1);
        add(displayPanel, cellSizeLabel,    0, 0, 1, 1);
        add(displayPanel, cellSizeField,    1, 0, 1, 1);
        add(displayPanel, cellSizeSlider,   0, 1, 2, 1);
        add(displayPanel, borderSizeLabel,  2, 0, 1, 1);
        add(displayPanel, borderSizeField,  3, 0, 1, 1);
        add(displayPanel, borderSizeSlider, 2, 1, 2, 1);
        add(this, editPanel, 1, 0, 1, 1);
        add(editPanel, xorButton,           0, 0, 1, 1);
        add(editPanel, replaceButton,       0, 1, 1, 1);
        add(editPanel, displayImpactButton, 1, 0, 1, 1);
        add(editPanel, fullColorButton,     1, 1, 1, 1);

        add(this, rulesPanel,       0, 2, 2, 1);

        add(rulesPanel, firstImpactLabel,   0, 0, 1, 1);
        add(rulesPanel, firstImpactField,   1, 0, 1, 1);
        add(rulesPanel, secondImpactLabel,  2, 0, 1, 1);
        add(rulesPanel, secondImpactField,  3, 0, 1, 1);

        add(rulesPanel, liveBeginLabel,     0, 1, 1, 1);
        add(rulesPanel, birthBeginLabel,    1, 1, 1, 1);
        add(rulesPanel, birthEndLabel,      2, 1, 1, 1);
        add(rulesPanel, liveEndLabel,       3, 1, 1, 1);

        add(rulesPanel, liveBeginField,     0, 2, 1, 1);
        add(rulesPanel, birthBeginField,    1, 2, 1, 1);
        add(rulesPanel, birthEndField,      2, 2, 1, 1);
        add(rulesPanel, liveEndField,       3, 2, 1, 1);
    }

    private void subscribe() {
        controller.addDisplayModelObserver(this);
        controller.addFieldObserver(this);
        controller.addEditModelObserver(this);
    }

    public OptionsFrame(JFrame parent, Controller controller) {
        super(parent, "Options", true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new GridBagLayout());
        this.controller = controller;

        initComponents();
        locateComponents();
        subscribe();

        pack();
        setResizable(false);
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
    public void updateFullColor(DisplayModelObservable displayModel) {
        fullColorButton.setSelected(displayModel.isFullColor());
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
        widthField.setValue(field.getWidth(0));
        heightField.setValue(field.getHeight());
    }

    @Override
    public void updateLifeBounds(FieldObservable field) {
        if (birthBeginField.getValue() == null || !(((Number)birthBeginField.getValue()).floatValue() != field.getBirthBegin()))
            birthBeginField.setValue(field.getBirthBegin());
        if (birthEndField.getValue() == null || !(((Number)birthEndField.getValue()).floatValue() != field.getBirthEnd()))
            birthEndField.setValue(field.getBirthEnd());
        if (liveBeginField.getValue() == null || !(((Number) liveBeginField.getValue()).floatValue() != field.getLiveBegin()))
            liveBeginField.setValue(field.getLiveBegin());
        if (liveEndField.getValue() == null || !(((Number) liveEndField.getValue()).floatValue() != field.getLiveEnd()))
            liveEndField.setValue(field.getLiveEnd());
    }

    @Override
    public void updateImpact(FieldObservable field) {
        if (firstImpactField.getValue() == null || !(((Number) firstImpactField.getValue()).floatValue() != field.getFirstImpact()))
            firstImpactField.setValue(field.getFirstImpact());
        if (secondImpactField.getValue() == null || !(((Number) secondImpactField.getValue()).floatValue() != field.getSecondImpact()))
            secondImpactField.setValue(field.getSecondImpact());
    }
}
