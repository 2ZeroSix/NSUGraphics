package life_hexagon.view;

import life_hexagon.controller.Controller;
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

public class GUI extends JFrame implements DisplayModelObserver, FieldObserver, EditModelObserver {
    private Controller controller;
    private FieldObservable field;
    private DisplayModelObservable displayModel;
    private EditModelObservable editModel;

    private OptionsFrame optionsFrame;
    private NewDocumentFrame newDocumentFrame;
    private JLabel statuslabel;

    private JButton stepButton;
    private JButton xorButton;
    private JToggleButton loopButton;
    public GUI() {
        super("LifeHexagon");

//        JDesktopPane desktop = new JDesktopPane();
//        {
//            setContentPane(desktop);
//            desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
//        }
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        {
            JPanel statusbar = new JPanel();
            statusbar.setBorder(new BevelBorder(BevelBorder.LOWERED));
            add(statusbar, BorderLayout.PAGE_END);
            statusbar.setPreferredSize(new Dimension(getWidth(), 16));
            statusbar.setLayout(new BoxLayout(statusbar, BoxLayout.X_AXIS));
            JLabel statusLabel = new JLabel("status");
            statusLabel.setFont(Font.getFont("Serif"));
            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
            statusbar.add(statusLabel);
        }
        {
            JMenuBar menuBar = new JMenuBar();
            {
                JMenu file = new JMenu("File");
                {
                    JMenuItem newDocument = new JMenuItem("New Document");
                    newDocument.addActionListener(actionEvent -> newDocumentFrame.setVisible(true));
                    file.add(newDocument);


                    JMenuItem save = new JMenuItem("Save");
                    save.addActionListener(ae -> {
                        // TODO FileChooser
                    });
                    file.add(save);


                    JMenuItem fileOpen = new JMenuItem("File Open");
                    fileOpen.addActionListener(ae -> {
                        // TODO FileChooser
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
            }
            {
                JToolBar toolBar = new JToolBar();
                toolBar.setFloatable(false);
                {
                    {
                        stepButton = new JButton("step");
                        stepButton.addActionListener(ae -> controller.makeStep());
                        toolBar.add(stepButton);
                    }

                    {
                        loopButton = new JToggleButton();
                        loopButton.addActionListener(ae -> controller.toggleLoopMode());
                        toolBar.add(loopButton);
                    }

                    {
                        xorButton = new JButton();
                        xorButton.addActionListener(ae -> controller.toggleXORMode());
                        toolBar.add(xorButton);
                    }

                    {
                    }
                }
                add(toolBar, BorderLayout.PAGE_START);
            }
            {
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(ae -> {
                    // TODO add info
                });
                menuBar.add(about);
            }
            setJMenuBar(menuBar);
        }


        {
            controller = new Controller(new ModelFactoryImpl());
            controller.addFieldObserver(this);
            controller.addEditModelObserver(this);
            FieldView fieldView = new FieldView(controller);
            fieldView.setHorizontalAlignment(SwingConstants.LEFT);
            fieldView.setVerticalAlignment(SwingConstants.TOP);
            JScrollPane scrollPane = new JScrollPane(fieldView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(scrollPane, BorderLayout.CENTER);
        }
        {
            optionsFrame = new OptionsFrame(controller);
            newDocumentFrame = new NewDocumentFrame(controller);
        }

        pack();
        setVisible(true);
    }

    @Override
    public void updateDisplay(DisplayModelObservable displayModel) {
        this.displayModel = displayModel;
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
    public void updateField(FieldObservable fieldObservable) {
        this.field = fieldObservable;
    }

    @Override
    public void updateState(FieldObservable fieldObservable, int row, int column) {

    }

    @Override
    public void updateImpact(FieldObservable fieldObservable, int row, int column) {

    }

    @Override
    public void updateSize(FieldObservable fieldObservable) {

    }

    @Override
    public void updateLifeBounds(FieldObservable fieldObservable) {

    }

    @Override
    public void updateImpact(FieldObservable fieldObservable) {

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
        } else {
            xorButton.setText("xor");
        }
    }

    @Override
    public void updateLoop(EditModelObservable editModel) {
        if (editModel.isLoop()) {
            loopButton.setText("stop");
            loopButton.setSelected(true);
            xorButton.setEnabled(false);
            stepButton.setEnabled(false);
        } else {
            loopButton.setText("start");
            loopButton.setSelected(false);
            xorButton.setEnabled(true);
            stepButton.setEnabled(true);
        }
    }

}