package view;

import actions.IconAction;
import actions.IsolineAction;
import model.IsolineModel;
import model.IsolineModelImpl;
import model.MutableIsolineModel;
import view.buttons.IconButton;
import view.panels.IsolinesPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GUI extends ToolBarStatusBarFrame {
    private MutableIsolineModel model = new IsolineModelImpl();
    private IconAction openFile = new IconAction("open file");
    private IsolineAction toggleShowValue = new IsolineAction("show value", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isShowValue());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setShowValue(!model.isShowValue());
        }
    };
    private IsolineAction toggleInterpolation = new IsolineAction("interpolation", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isInterpolating());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setInterpolating(!model.isInterpolating());
        }
    };
    private IsolineAction toggleGrid = new IsolineAction("grid", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isGrid());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setGrid(!model.isGrid());
        }
    };
    private IsolineAction toggleMap = new IsolineAction("map", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isPlot());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setPlot(!model.isPlot());
        }
    };
    private IsolineAction toggleIsolines = new IsolineAction("contour lines", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isIsolines());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setIsolines(!model.isIsolines());
        }
    };
    private IsolineAction toggleIsolinesOnClick = new IsolineAction("clicking", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isClicking());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setClicking(!model.isClicking());
        }
    };
    private IsolineAction toggleIsolineControlPoints = new IsolineAction("control points", model) {
        @Override
        public void update(IsolineModel isolineModel) {
            putValue(Action.SELECTED_KEY, model.isGridDots());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setGridDots(!model.isGridDots());
        }
    };
    private IconAction showAbout = new IconAction("about", "show info about author") {
        Container about;
        {
            about = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            try {
                about.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/me.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH))), gbc);
                gbc.gridy++;
            } catch (IllegalArgumentException | IOException ignore) {
            }
            about.add(new JLabel("Isolines ver. 1.0"), gbc);
            gbc.gridy++;
            about.add(new JLabel("Bogdan Lukin"), gbc);
            gbc.gridy++;
            about.add(new JLabel("FIT 15206"), gbc);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(GUI.this, about, "About", JOptionPane.PLAIN_MESSAGE);
        }
    };
    private IconAction exit = new IconAction("exit") {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };

    public GUI() {
        super("Isolines");
        initWindow();
        initWorkspace();
        initMenuBar();
        initToolBarItems();
        pack();
    }

    private void initWindow() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exit.actionPerformed(null);
            }
        });
    }
    private void initWorkspace() {
        getWorkWindow().setLayout(new BorderLayout());
        JPanel workSpace = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(workSpace,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(scrollPane, BorderLayout.CENTER);
        add(workSpace, new IsolinesPanel(model), 0, 0, 1, 1);
    }
    private void initToolBarItems() {
        JToolBar toolBar = getToolBar();
        toolBar.add(new IconButton(openFile));

        toolBar.addSeparator();

        toolBar.add(new IconButton(toggleShowValue));
        toolBar.add(new IconButton(toggleInterpolation));
        toolBar.add(new IconButton(toggleGrid));
        toolBar.add(new IconButton(toggleMap));
        toolBar.add(new IconButton(toggleIsolines));
        toolBar.add(new IconButton(toggleIsolinesOnClick));
        toolBar.add(new IconButton(toggleIsolineControlPoints));

        toolBar.addSeparator();

        toolBar.add(new IconButton(showAbout));

        toolBar.addSeparator();

        toolBar.add(new IconButton(exit));
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(openFile);
        file.add(exit);

        menuBar.add(file);

        JMenu edit = new JMenu("Edit");
        edit.add(toggleShowValue);
        edit.add(toggleInterpolation);
        edit.add(toggleGrid);
        edit.add(toggleMap);
        edit.add(toggleIsolines);
        edit.add(toggleIsolinesOnClick);
        edit.add(toggleIsolineControlPoints);

        menuBar.add(edit);

        JMenu help = new JMenu("Help");

        help.add(showAbout);

        menuBar.add(help);


        setJMenuBar(menuBar);
    }

}
