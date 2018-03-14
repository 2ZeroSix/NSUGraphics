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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GUI extends JFrame implements DisplayModelObserver, FieldObserver, EditModelObserver, MouseListener {
    private Controller controller;
    private FieldObservable field;
    private DisplayModelObservable displayModel;
    private EditModelObservable editModel;

    private OptionsFrame optionsFrame;
    private NewDocumentFrame newDocumentFrame;
    private AboutFrame aboutFrame;
    private JLabel statusLabel;

    private JButton stepButton;
    private JButton xorButton;
    private JToggleButton loopButton;
    private JToggleButton impactButton;
    private JButton clearButton;
    private JButton aboutButton;

    private boolean changed = false;
    public GUI() {
        init();
        postInit();
    }

    private void init() {
        initWindow();
        initStatusBar();
        initMenuBar();
        initToolBar();
        initController();
        initFieldView();
        initAdditionalFrames();
    }

    private void initWindow() {
        setTitle("LifeHexagon");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (changed) {
                    int answer = JOptionPane.showConfirmDialog(GUI.this, "Do you want to save model?", "Exit confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        JFileChooser fileChooser = new JFileChooser("Data");
                        int retVal = fileChooser.showSaveDialog(GUI.this);
                        if (retVal == JFileChooser.APPROVE_OPTION) {
                            try {
                                controller.saveDocument(fileChooser.getSelectedFile());
                            } catch (LifeIO.LifeIOException e) {
                                JOptionPane.showMessageDialog(GUI.this, e.getLocalizedMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (retVal == JFileChooser.ERROR_OPTION) {
                            System.err.println("error");
                        } else {
                            windowClosing(event);
                        }
                    }
                }
            }
        });
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
                newDocument.addActionListener(actionEvent -> {
                    newDocumentFrame.setLocationRelativeTo(this);
                    newDocumentFrame.setVisible(true);
                });
                file.add(newDocument);


                File dir = new File("Data");
                if(dir.isDirectory() ? !dir.canWrite() : !dir.mkdir()) {
                    JOptionPane.showMessageDialog(this,
                            "can't open \"Data\" catalog with write rights, using default (" + new JFileChooser().getCurrentDirectory() + ")",
                            "Default save catalog",
                            JOptionPane.WARNING_MESSAGE);
                }

                JMenuItem save = new JMenuItem("Save");
                save.addActionListener(ae -> {
                    JFileChooser fileChooser = new JFileChooser("Data");
                    int retVal = fileChooser.showSaveDialog(this);
                    if(retVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            controller.saveDocument(fileChooser.getSelectedFile());
                            changed = false;
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
                    JFileChooser fileChooser = new JFileChooser("Data");
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


                JMenuItem exit = new JMenuItem("Exit");
                exit.addActionListener(actionEvent -> System.exit(0));
                file.add(exit);
            }
            menuBar.add(file);


            JMenu edit = new JMenu("Edit");
            {
                JMenuItem options = new JMenuItem("Options");
                options.addActionListener(actionEvent -> {
                    optionsFrame.setLocationRelativeTo(this);
                    optionsFrame.setVisible(true);
                });
                edit.add(options);
            }
            menuBar.add(edit);

            JMenu help = new JMenu("Help");
            {
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(ae -> {
                    aboutFrame.setLocationRelativeTo(null);
                    aboutFrame.setVisible(true);
                });
                help.add(about);
            }
            menuBar.add(help);
        }
        setJMenuBar(menuBar);
    }

    private void initToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        {
            {
                loopButton = new JToggleButton();
                loopButton.addActionListener(ae -> controller.toggleLoopMode());
                loopButton.addMouseListener(this);
                try {
                    loopButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/start.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                    loopButton.setSelectedIcon(new ImageIcon(ImageIO.read(getClass().getResource("/stop.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toolBar.add(loopButton);
            }

            {
                stepButton = new JButton();
                stepButton.addActionListener(ae -> controller.makeStep());
                stepButton.setToolTipText("make step of game");
                stepButton.addMouseListener(this);
                toolBar.add(stepButton);
                try {
                    stepButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/step.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                    stepButton.setDisabledIcon(new ImageIcon(ImageIO.read(getClass().getResource("/step-disabled.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            {
                clearButton = new JButton();
                clearButton.addActionListener(ae -> controller.clear());
                clearButton.setToolTipText("clear field");
                clearButton.addMouseListener(this);
                try {
                    clearButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/clear.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                    clearButton.setDisabledIcon(new ImageIcon(ImageIO.read(getClass().getResource("/clear-disabled.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toolBar.add(clearButton);
            }

            toolBar.addSeparator();

            {
                xorButton = new JButton();
                xorButton.addActionListener(ae -> controller.toggleXORMode());
                xorButton.addMouseListener(this);
                try {
                    xorButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/xor.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                    xorButton.setSelectedIcon(new ImageIcon(ImageIO.read(getClass().getResource("/replace.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toolBar.add(xorButton);
            }

            {
                impactButton = new JToggleButton();
                impactButton.addActionListener(ae -> controller.toggleDisplayImpact());
                impactButton.addMouseListener(this);
                try {
                    impactButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/impact.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toolBar.add(impactButton);
            }

            toolBar.addSeparator();

            {
                aboutButton = new JButton();
                aboutButton.addActionListener(ae -> {
                    aboutFrame.setLocationRelativeTo(null);
                    aboutFrame.setVisible(true);
                });
                aboutButton.addMouseListener(this);
                try {
                    aboutButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/about.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toolBar.add(aboutButton);
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
        aboutFrame = new AboutFrame(this);
    }

    private void postInit() {
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
        changed = true;
    }

    @Override
    public void updateHexagonSize(DisplayModelObservable displayModel) {
        changed = true;
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
        changed = true;
    }

    @Override
    public void updateImpact(FieldObservable field, int row, int column) {
        changed = true;
    }

    @Override
    public void updateSize(FieldObservable field) {
        changed = true;
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
            xorButton.setSelected(false);
            xorButton.setToolTipText("set edit mode to replace (current xor)");
        } else {
            xorButton.setSelected(true);

            xorButton.setToolTipText("set edit mode to xor (current replace)");
        }
    }

    @Override
    public void updateLoop(EditModelObservable editModel) {
        if (editModel.isLoop()) {
            loopButton.setToolTipText("stop game");
            loopButton.setSelected(true);
            stepButton.setEnabled(false);
            clearButton.setEnabled(false);
        } else {
            loopButton.setToolTipText("start game (step per second)");
            loopButton.setSelected(false);
            stepButton.setEnabled(true);
            clearButton.setEnabled(true);
        }
    }

    private void setStatus(String status) {
        if (!Objects.equals(status, statusLabel.getText())) {
            this.statusLabel.setText(status);
            this.statusLabel.repaint();
        }
    }

    private void setStatus(JComponent component) {
        String tooltip = component.getToolTipText();
        setStatus(tooltip);
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