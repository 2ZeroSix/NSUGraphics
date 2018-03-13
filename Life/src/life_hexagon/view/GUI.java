package life_hexagon.view;

import life_hexagon.controller.Controller;
import life_hexagon.model.LifeIO;
import life_hexagon.model.ModelFactoryImpl;
import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.EditModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.EditModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class GUI extends JFrame implements DisplayModelObserver, FieldObserver, EditModelObserver, MouseListener {
    private Controller controller;
    private FieldObservable field;
    private DisplayModelObservable displayModel;
    private EditModelObservable editModel;

    private OptionsFrame optionsFrame;
    private NewDocumentFrame newDocumentFrame;
    private JLabel statusLabel;

    private JButton stepButton;
    private JButton xorButton;
    private JToggleButton loopButton;
    private JToggleButton impactButton;

    private void initWindow() {
        setTitle("LifeHexagon");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));

    }


    private void initStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusBar, BorderLayout.PAGE_END);
        statusBar.setPreferredSize(new Dimension(getWidth(), 16));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusLabel = new JLabel("status");
        statusLabel.setFont(Font.getFont("Serif"));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        {
            JMenu file = new JMenu("File");
            {
                JMenuItem newDocument = new JMenuItem("New Document");
                newDocument.addActionListener(actionEvent -> newDocumentFrame.setVisible(true));
                file.add(newDocument);


                JMenuItem save = new JMenuItem("Save");
                save.addActionListener(ae -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int retVal = fileChooser.showSaveDialog(this);
                    if(retVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            controller.saveDocument(fileChooser.getSelectedFile());
                        } catch (LifeIO.LifeIOException e) {
                            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (retVal == JFileChooser.ERROR_OPTION) {
                        System.err.println("error");
                    }
                });
                file.add(save);


                JMenuItem fileOpen = new JMenuItem("File Open");
                fileOpen.addActionListener(ae -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int retVal = fileChooser.showOpenDialog(this);
                    if(retVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            controller.openDocument(fileChooser.getSelectedFile());
                        } catch (LifeIO.LifeIOException e) {
                            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Open error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (retVal == JFileChooser.ERROR_OPTION) {
                        System.err.println("error");
                    }
                });
                file.add(fileOpen);


                JMenuItem options = new JMenuItem("Options");
                options.addActionListener(actionEvent -> optionsFrame.setVisible(true));
                file.add(options);


                JMenuItem exit = new JMenuItem("Exit");
                exit.addActionListener(actionEvent -> System.exit(0));
                file.add(exit);
            }
            menuBar.add(file);


            JMenuItem about = new JMenuItem("About");
            about.addActionListener(ae -> {
                // TODO add info
            });
            menuBar.add(about);
        }
        setJMenuBar(menuBar);
    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        {
            {
                stepButton = new JButton("step");
                stepButton.addActionListener(ae -> controller.makeStep());
                stepButton.setToolTipText("make step of game");
                stepButton.addMouseListener(this);
                toolBar.add(stepButton);
            }

            {
                xorButton = new JButton();
                xorButton.addActionListener(ae -> controller.toggleXORMode());
                xorButton.addMouseListener(this);
                toolBar.add(xorButton);
            }

            {
                loopButton = new JToggleButton();
                loopButton.addActionListener(ae -> controller.toggleLoopMode());
                loopButton.addMouseListener(this);
                toolBar.add(loopButton);
            }

            {
                impactButton = new JToggleButton("impact");
                impactButton.addActionListener(ae -> controller.toggleDisplayImpact());
                impactButton.addMouseListener(this);
                toolBar.add(impactButton);
            }
        }
        add(toolBar, BorderLayout.PAGE_START);
    }

    private void initController() {
        controller = new Controller(new ModelFactoryImpl());
        controller.addFieldObserver(this);
        controller.addEditModelObserver(this);
        controller.addDisplayModelObserver(this);
    }

    private void initFieldView() {
        FieldView fieldView = new FieldView(controller);
        fieldView.setHorizontalAlignment(SwingConstants.LEFT);
        fieldView.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPane = new JScrollPane(fieldView,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initAdditionalFrames() {
        optionsFrame = new OptionsFrame(this, controller);
        newDocumentFrame = new NewDocumentFrame(this, controller);
    }

    public GUI() {
        initWindow();
        initStatusBar();
        initMenuBar();
        initToolBar();
        initController();
        initFieldView();
        initAdditionalFrames();

        pack();
        setVisible(true);
    }

    @Override
    public void updateDisplay(DisplayModelObservable displayModel) {
        this.displayModel = displayModel;
        updateBorderWidth(displayModel);
        updateHexagonSize(displayModel);
        updateDisplayImpact(displayModel);
    }

    @Override
    public void updateBorderWidth(DisplayModelObservable displayModel) {
    }

    @Override
    public void updateHexagonSize(DisplayModelObservable displayModel) {
    }

    @Override
    public void updateDisplayImpact(DisplayModelObservable displayModel) {
        if (displayModel.isDisplayImpact()) {
            impactButton.setSelected(true);
            impactButton.setToolTipText("hide impact");
        } else {
            impactButton.setSelected(false);
            impactButton.setToolTipText("show impact");
        }
    }

    @Override
    public void updateField(FieldObservable field) {
        this.field = field;
    }

    @Override
    public void updateState(FieldObservable field, int row, int column) {

    }

    @Override
    public void updateImpact(FieldObservable field, int row, int column) {

    }

    @Override
    public void updateSize(FieldObservable field) {

    }

    @Override
    public void updateLifeBounds(FieldObservable field) {

    }

    @Override
    public void updateImpact(FieldObservable field) {
    }

    @Override
    public void updateEdit(EditModelObservable editModel) {
        this.editModel = editModel;
        updateXOR(editModel);
        updateLoop(editModel);
    }

    @Override
    public void updateXOR(EditModelObservable editModel) {
        if (editModel.isXOR()) {
            xorButton.setText("replace");
            xorButton.setToolTipText("set edit mode to replace (current xor)");
        } else {
            xorButton.setText("xor");
            xorButton.setToolTipText("set edit mode to xor (current replace)");
        }
    }

    @Override
    public void updateLoop(EditModelObservable editModel) {
        if (editModel.isLoop()) {
            loopButton.setText("stop");
            loopButton.setToolTipText("stop game");
            loopButton.setSelected(true);
            stepButton.setEnabled(false);
        } else {
            loopButton.setText("start");
            loopButton.setToolTipText("start game (step per second)");
            loopButton.setSelected(false);
            stepButton.setEnabled(true);
        }
    }

    private GUI setStatus(String status) {
        if (!Objects.equals(status, statusLabel.getText())) {
            this.statusLabel.setText(status);
            this.statusLabel.repaint();
        }
        return this;
    }

    private GUI setStatus(JComponent component) {
        String tooltip = component.getToolTipText();
        setStatus(tooltip);
        return this;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        setStatus("");
    }
}