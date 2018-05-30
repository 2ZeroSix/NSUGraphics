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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class GUI extends ToolBarStatusBarFrame {
    private MutableIsolineModel model = new IsolineModelImpl();
    private IconAction openFile = new IconAction("open file") {
        private JFileChooser fileChooser;
        {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            File dir = new File("Data");
            if (dir.isDirectory() ? !dir.canWrite() : !dir.mkdir()) {
                JOptionPane.showMessageDialog(GUI.this,
                        "can't open \"Data\" catalog with write rights, using default (" + fileChooser.getCurrentDirectory() + ")",
                        "Default save catalog",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                fileChooser.setCurrentDirectory(dir);
            }
        }
        @Override
        public void actionPerformed(ActionEvent ae) {
            int retval = fileChooser.showOpenDialog(GUI.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())){
                    scanner.useDelimiter("(([\\v\\h])|([\\v\\h]*(//.*[\\v])[\\v\\h]*))+");
                    int k, m;
                    if (!scanner.hasNextInt()) throw new IOException("wrong format");
                    k = scanner.nextInt();
                    if (!scanner.hasNextInt()) throw new IOException("wrong format");
                    m = scanner.nextInt();
                    if (!scanner.hasNextInt()) throw new IOException("wrong format");
                    int n = scanner.nextInt();
                    if (n <= 0) throw new IOException("wrong format");
                    List<Color> colorList = new ArrayList<>(n + 1);
                    for (int i = 0; i <= n; ++i) {
                        if (!scanner.hasNextInt()) throw new IOException("wrong format");
                        int r = scanner.nextInt();
                        if (!scanner.hasNextInt()) throw new IOException("wrong format");
                        int g = scanner.nextInt();
                        if (!scanner.hasNextInt()) throw new IOException("wrong format");
                        int b = scanner.nextInt();
                        colorList.add(new Color(r, g, b));
                    }
                    if (!scanner.hasNextInt()) throw new IOException("wrong format");
                    int r = scanner.nextInt();
                    if (!scanner.hasNextInt()) throw new IOException("wrong format");
                    int g = scanner.nextInt();
                    if (!scanner.hasNextInt()) throw new IOException("wrong format");
                    int b = scanner.nextInt();
                    model.setIsolineColor(new Color(r, g, b));
                    model.setGridSize(new Dimension(k, m));
                    model.setColors(colorList);
                } catch (IOException | IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(GUI.this, e.getLocalizedMessage(), "open file error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (retval == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(GUI.this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    private IsolineAction toggleShowValue = new IsolineAction("show value", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isShowValue());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setShowValue(!model.isShowValue());
        }
    };
    private IsolineAction toggleInterpolation = new IsolineAction("interpolation", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isInterpolating());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setInterpolating(!model.isInterpolating());
        }
    };
    private IsolineAction toggleGrid = new IsolineAction("grid", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isGrid());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setGrid(!model.isGrid());
        }
    };
    private IsolineAction toggleMap = new IsolineAction("map", "real face or interpolated by grid ", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isPlot());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setPlot(!model.isPlot());
        }
    };
    private IsolineAction toggleIsolines = new IsolineAction("contour lines", "isolines", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isIsolines());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setIsolines(!model.isIsolines());
        }
    };
    private IsolineAction toggleIsolinesOnClick = new IsolineAction("clicking", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isClicking());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setClicking(!model.isClicking());
        }
    };

    private IsolineAction clearUserIsolines = new IsolineAction("clean user isolines", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            setEnabled(model.getUserIsolines().size() > 0);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.cleanUserIsolines();
        }
    };

    private IsolineAction toggleDynamicIsoline = new IsolineAction("dynamic isoline", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isDynamic());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setDynamic(!model.isDynamic());
        }
    };

    private IsolineAction toggleIsolineControlPoints = new IsolineAction("control points", model) {
        @Override
        public void update(Observable isolineModel, Object object) {
            putValue(Action.SELECTED_KEY, model.isGridDots());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            model.setGridDots(!model.isGridDots());
        }
    };

    private IsolineAction showOptions = new IsolineAction("options", model) {
        private JPanel panel = new JPanel(new GridBagLayout());

        @Override
        public void actionPerformed(ActionEvent e) {
            panel.removeAll();
            addNumberInput("grid width", 1, 100, model.getGridSize().width, (num) -> true, (num) -> model.setGridSize(new Dimension(num.intValue(), model.getGridSize().height)));
            addNumberInput("grid height", 1, 100, model.getGridSize().height, (num) -> true, (num) -> model.setGridSize(new Dimension(model.getGridSize().width, num.intValue())));
            Rectangle.Double bounds = model.getFunction().getBounds();
            addNumberInput("a", -100., 100., bounds.getMinX(),
                    (num) -> num.doubleValue() < bounds.getMaxX(),
                    (num) -> {
                        synchronized (model) {
                            bounds.setRect(new Rectangle2D.Double(num.doubleValue(), bounds.y, bounds.getMaxX() - num.doubleValue(), bounds.height));
                            model.getFunction().setBounds(bounds);
                        }
                    });
            addNumberInput("b", -100., 100., bounds.getMaxX(),
                    (num) -> num.doubleValue() > bounds.getMinX(),
                    (num) -> {
                        synchronized (model) {
                            bounds.setRect(new Rectangle2D.Double(bounds.x, bounds.y, num.doubleValue() - bounds.x, bounds.height));
                            model.getFunction().setBounds(bounds);
                        }
                    });
            addNumberInput("c", -100., 100., bounds.getMinY(),
                    (num) -> num.doubleValue() < bounds.getMaxY(),
                    (num) -> {
                        synchronized (model) {
                            bounds.setRect(new Rectangle2D.Double(bounds.x, num.doubleValue(), bounds.width, bounds.getMaxY() - num.doubleValue()));
                            model.getFunction().setBounds(bounds);
                        }
                    });

            addNumberInput("d", -100., 100., bounds.getMaxY(),
                    (num) -> num.doubleValue() > bounds.getMinY(),
                    (num) ->

                    {
                        synchronized (model) {
                            bounds.setRect(new Rectangle2D.Double(bounds.x, bounds.y, bounds.width, num.doubleValue() - bounds.y));
                            model.getFunction().setBounds(bounds);
                        }
                    });
            JOptionPane.showMessageDialog(GUI.this, panel, "Options", JOptionPane.PLAIN_MESSAGE);
        }

        private int intOptionsCount = 0;

        private <T extends Number> void addNumberInput(String name, T min, T max, T defaultValue, Predicate<Number> isValid, Consumer<Number> onChange) {
            JLabel label = new JLabel(name);
            if (defaultValue instanceof Integer) {
                JFormattedTextField field = new JFormattedTextField(new DecimalFormat("0"));
                JSlider slider = new JSlider(JSlider.HORIZONTAL, min.intValue(), max.intValue(), defaultValue.intValue());
                field.setColumns(Integer.max(min.toString().length(), max.toString().length()));
                field.setValue(defaultValue);
                field.addPropertyChangeListener("value", evt -> {
                    if (evt.getNewValue() != null) {
                        int value = ((Number) evt.getNewValue()).intValue();
                        if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                            if (value < min.intValue() || value > max.intValue() || !isValid.test(value)) {
                                field.setValue(evt.getOldValue());
                            } else {
                                onChange.accept(value);
                                slider.setValue(value);

                            }
                        }
                    }
                });
                slider.addChangeListener((ce) -> field.setValue(slider.getValue()));
                ContainerUtils.add(panel, label, 0, intOptionsCount, 1, 1);
                ContainerUtils.add(panel, field, 1, intOptionsCount, 1, 1);
                ContainerUtils.add(panel, slider, 2, intOptionsCount, 1, 1);
                ++intOptionsCount;
            } else if (defaultValue instanceof Double) {
                JFormattedTextField field = new JFormattedTextField(new DecimalFormat("0.00"));
                JSlider slider = new JSlider(JSlider.HORIZONTAL, (int) (min.doubleValue() * 100), (int) (max.doubleValue() * 100), (int) (defaultValue.doubleValue() * 100));
                field.setColumns(Integer.max(min.toString().length(), max.toString().length()));
                field.setValue(defaultValue);
                field.addPropertyChangeListener("value", evt -> {
                    if (evt.getNewValue() != null) {
                        double value = ((Number) evt.getNewValue()).doubleValue();
                        if (!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
                            if (value < min.doubleValue() || value > max.doubleValue() || !isValid.test(value)) {
                                field.setValue(evt.getOldValue());
                            } else {
                                onChange.accept(value);
                                slider.setValue((int) (value * 100));
                            }
                        }
                    }
                });
                slider.addChangeListener((ce) -> field.setValue(slider.getValue() / 100.));
                ContainerUtils.add(panel, label, 0, intOptionsCount, 1, 1);
                ContainerUtils.add(panel, field, 1, intOptionsCount, 1, 1);
                ContainerUtils.add(panel, slider, 2, intOptionsCount, 1, 1);
                ++intOptionsCount;

            }
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
        add(new IsolinesPanel(model, this::setStatus), BorderLayout.CENTER);
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
        toolBar.add(new IconButton(clearUserIsolines));
        toolBar.add(new IconButton(toggleDynamicIsoline));
        toolBar.add(new IconButton(toggleIsolineControlPoints));
        toolBar.addSeparator();

        toolBar.add(new IconButton(showOptions));
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
        edit.add(clearUserIsolines);
        edit.add(toggleDynamicIsoline);
        edit.add(toggleIsolineControlPoints);
        edit.add(showOptions);
        menuBar.add(edit);

        JMenu help = new JMenu("Help");

        help.add(showAbout);

        menuBar.add(help);


        setJMenuBar(menuBar);
    }

}
