package ru.nsu.ccfit.lukin.view;

import jdk.nashorn.internal.scripts.JD;
import ru.nsu.ccfit.lukin.model.filters.BlackAndWhiteFilter;
import ru.nsu.ccfit.lukin.model.filters.FloydSteinbergDithering;
import ru.nsu.ccfit.lukin.model.filters.NegativeFilter;
import ru.nsu.ccfit.lukin.model.filters.OrderedDithering;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class GUI extends ToolBarStatusBarFrame{
    private FullImage fullImage;
    private SelectedImage selectedImage;
    private FilteredImage filteredImage;

    public GUI() {
        super("Filter");
        init();
        postInit();
    }

    private void init() {
        initWindow();
        initWorkspace();
        initMenuBar();
        initToolBarItems();
        initAdditionalFrames();
    }

    private void initWindow() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exit();
            }
        });
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        {
            JMenu file = new JMenu("File");
            {
                JMenuItem newDocument = new JMenuItem("New Document");
                newDocument.addActionListener(actionEvent -> {
                    // TODO implement new document
//                    newDocumentFrame.setLocationRelativeTo(this);
//                    newDocumentFrame.setVisible(true);
                });
                file.add(newDocument);


//                File dir = new File("Data");
//                if (dir.isDirectory() ? !dir.canWrite() : !dir.mkdir()) {
//                    JOptionPane.showMessageDialog(this,
//                            "can't open \"Data\" catalog with write rights, using default (" + new JFileChooser().getCurrentDirectory() + ")",
//                            "Default save catalog",
//                            JOptionPane.WARNING_MESSAGE);
//                }

                JMenuItem save = new JMenuItem("Save");
                save.addActionListener(ae -> {
                    // TODO implement saving
//                    JFileChooser fileChooser = new JFileChooser("Data");
//                    int retVal = fileChooser.showSaveDialog(this);
//                    if (retVal == JFileChooser.APPROVE_OPTION) {
//                        try {
//                            controller.saveDocument(fileChooser.getSelectedFile());
//                            changed = false;
//                        } catch (LifeIO.LifeIOException e) {
//                            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
//                        }
//                    } else if (retVal == JFileChooser.ERROR_OPTION) {
//                        System.err.println("error");
//                    }
                });
                file.add(save);


                JMenuItem fileOpen = new JMenuItem("File Open");
                fileOpen.addActionListener(ae -> {
                    // TODO implement opening
//                    JFileChooser fileChooser = new JFileChooser("Data");
//                    int retVal = fileChooser.showOpenDialog(this);
//                    if (retVal == JFileChooser.APPROVE_OPTION) {
//                        try {
//                            controller.openDocument(fileChooser.getSelectedFile());
//                            changed = false;
//                        } catch (LifeIO.LifeIOException e) {
//                            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Open error", JOptionPane.ERROR_MESSAGE);
//                        }
//                    } else if (retVal == JFileChooser.ERROR_OPTION) {
//                        System.err.println("error");
//                    }
                });
                file.add(fileOpen);


                JMenuItem exit = new JMenuItem("Exit");
                exit.addActionListener(ae -> exit());
                file.add(exit);
            }
            menuBar.add(file);


            JMenu edit = new JMenu("Edit");
            // TODO add edit menu items
            menuBar.add(edit);

            JMenu help = new JMenu("Help");
            {
                JMenuItem about = new JMenuItem("About");
                about.addActionListener(ae -> {
                    // TODO add about
//                    aboutFrame.setLocationRelativeTo(null);
//                    aboutFrame.setVisible(true);
                });
                help.add(about);
            }
            menuBar.add(help);
        }
        setJMenuBar(menuBar);
    }

    protected void initToolBarItems() {
        JToolBar toolBar = getToolBar();
        JButton about = new JButton("about");
        about.setToolTipText("about");
        toolBar.add(about);
        // TODO add toolbar buttons
//        * new
        ButtonGroup group = new ButtonGroup();
        ImageObserverButton newFileButton = new ImageObserverButton("new-file");
        newFileButton.addActionListener(ae -> fullImage.setImage(null));
        toolBar.add(newFileButton);
//        * open
        ImageObserverButton openFileButton = new ImageObserverButton("open-file");
        openFileButton.addActionListener(ae -> openFile());
        toolBar.add(openFileButton);
//        * save
        ImageObserverButton saveFileButton = new ImageObserverButton("save-file");
        saveFileButton.addActionListener(ae -> saveFile());
        toolBar.add(saveFileButton);

        toolBar.addSeparator();
//        * start/stop select
        ImageObserverButton selectButton = new ImageObserverButton("select", fullImage) {
            @Override
            public void updateSelectable(FullImageObservable observable) {
                this.setSelected(fullImage.isSelectable());
            }
        };
        selectButton.addActionListener(ae -> fullImage.setSelectable(!fullImage.isSelectable()));
        toolBar.add(selectButton);


        toolBar.addSeparator();
//        * ч/б
        FilterButton blackWhiteButton = new FilterButton("black-white", filteredImage) {
            @Override
            public boolean initFilter() {
                setFilter(new BlackAndWhiteFilter());
                setNeedToInitFilter(false);
                return true;
            }
        };
        toolBar.add(blackWhiteButton);
//        * Негатив
        FilterButton negativeButton = new FilterButton("negative", filteredImage) {
            @Override
            public boolean initFilter() {
                setFilter(new NegativeFilter());
                setNeedToInitFilter(false);
                return true;
            }
        };
        toolBar.add(negativeButton);
//        * Дизеринг
//        *   Флойда-Стейнберга - FloydSteinbergDithering
        FilterButton floydSteinbergButton = new FilterButton("floyd-steinberg", filteredImage) {
            public JFormattedTextField countBlueTextField;
            public JFormattedTextField countGreenTextField;
            public JFormattedTextField countRedTextField;
            public JPanel dialog = null;
            @Override
            public boolean initFilter() {
                if (dialog == null) {
                    dialog = new JPanel();
//                dialog.setResizable(false);
                    dialog.setLayout(new GridLayout());
                    Consumer<JFormattedTextField> changeListenerSetter = (field) -> {
                        field.addPropertyChangeListener("value", evt -> {
                            if (evt.getNewValue() != null) {
                                int value = ((Number) evt.getNewValue()).intValue();
                                if (value < 1 || value > 256) {
                                    field.setValue(evt.getOldValue());
                                }
                            }
                        });
                    };
                    JLabel countRedLabel = new JLabel("count of red colors");
                    JLabel countGreenLabel = new JLabel("count of green colors");
                    JLabel countBlueLabel = new JLabel("count of blue colors");
                    countRedTextField = new JFormattedTextField(new DecimalFormat("0; ()"));
                    countRedTextField.setValue(8);
                    countGreenTextField = new JFormattedTextField(new DecimalFormat("0; ()"));
                    countGreenTextField.setValue(8);
                    countBlueTextField = new JFormattedTextField(new DecimalFormat("0; ()"));
                    countBlueTextField.setValue(4);
                    changeListenerSetter.accept(countRedTextField);
                    changeListenerSetter.accept(countGreenTextField);
                    changeListenerSetter.accept(countBlueTextField);
                    dialog.add(countRedLabel);
                    dialog.add(countRedTextField);
                    dialog.add(countGreenLabel);
                    dialog.add(countGreenTextField);
                    dialog.add(countBlueLabel);
                    dialog.add(countBlueTextField);
//                add(dialog);
                }
                int retval = JOptionPane.showConfirmDialog(GUI.this, dialog,
                        "Floyd-Steiberg options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                setNeedToInitFilter(true);
                if (retval == JOptionPane.OK_OPTION) {
                    setFilter(new FloydSteinbergDithering(((Number)countRedTextField.getValue()).intValue(),
                            ((Number)countGreenTextField.getValue()).intValue(), ((Number)countBlueTextField.getValue()).intValue()));
                    return true;
                } else {
                    return false;
                }
            }
        };
        toolBar.add(floydSteinbergButton);
//        *   ordered dither - OrderedDithering
        FilterButton orderedDitheringButton = new FilterButton("ordered dithering", filteredImage) {
            public JFormattedTextField countBlueTextField;
            public JFormattedTextField countGreenTextField;
            public JFormattedTextField countRedTextField;
            public JPanel dialog = null;
            @Override
            public boolean initFilter() {
                if (dialog == null) {
                    dialog = new JPanel();
//                dialog.setResizable(false);
                    dialog.setLayout(new GridLayout(3, 3, 5, 5));
                    Consumer<JFormattedTextField> changeListenerSetter = (field) -> {
                        field.addPropertyChangeListener("value", evt -> {
                            if (evt.getNewValue() != null) {
                                int value = ((Number) evt.getNewValue()).intValue();
                                if (value < 1 || value > 256) {
                                    field.setValue(evt.getOldValue());
                                }
                            }
                        });
                    };
                    JLabel countRedLabel = new JLabel("count of red colors");
                    JLabel countGreenLabel = new JLabel("count of green colors");
                    JLabel countBlueLabel = new JLabel("count of blue colors");
                    countRedTextField = new JFormattedTextField(new DecimalFormat("0; ()"));
                    countRedTextField.setValue(8);
                    countGreenTextField = new JFormattedTextField(new DecimalFormat("0; ()"));
                    countGreenTextField.setValue(8);
                    countBlueTextField = new JFormattedTextField(new DecimalFormat("0; ()"));
                    countBlueTextField.setValue(4);
                    changeListenerSetter.accept(countRedTextField);
                    changeListenerSetter.accept(countGreenTextField);
                    changeListenerSetter.accept(countBlueTextField);
                    dialog.add(countRedLabel);
                    dialog.add(countRedTextField);
                    dialog.add(countGreenLabel);
                    dialog.add(countGreenTextField);
                    dialog.add(countBlueLabel);
                    dialog.add(countBlueTextField);
//                add(dialog);
                }
                int retval = JOptionPane.showConfirmDialog(GUI.this, dialog,
                        "Ordered dithering options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                setNeedToInitFilter(true);
                if (retval == JOptionPane.OK_OPTION) {
                    setFilter(new OrderedDithering(((Number)countRedTextField.getValue()).intValue(),
                            ((Number)countGreenTextField.getValue()).intValue(), ((Number)countBlueTextField.getValue()).intValue()));
                    return true;
                } else {
                    return false;
                }
            }
        };
        toolBar.add(orderedDitheringButton);
//        * Удвоение - NearestNeighborDoubleFilter
//        * Дифференцирующий фильтр
//        * Собеля - SobelFilter
//        * Робертса -
//        * Сглаживающий фльтр -
//        * Фильтр повышения резкости -
//        * Тиснение -
//        * Акварелизация -
//        * selected -> filtered
//        * selected <- filtered

    }

    private void initWorkspace() {
        getWorkWindow().setLayout(new BorderLayout());
        JPanel workSpace = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(workSpace,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(scrollPane, BorderLayout.CENTER);
        fullImage = new FullImage(new Dimension(350, 350));
        selectedImage = new SelectedImage(fullImage);
        filteredImage = new FilteredImage(selectedImage);
        add(workSpace, fullImage, 0, 0, 1, 1);
        add(workSpace, selectedImage, 1, 0, 1, 1);
        add(workSpace, filteredImage, 2, 0, 1, 1);
    }

    private void initAdditionalFrames() {
        // TODO implement additional frames
//        optionsFrame = new OptionsFrame(this, controller);
//        newDocumentFrame = new NewDocumentFrame(this, controller);
//        aboutFrame = new AboutFrame(this);
    }

    private void postInit() {
        pack();
        setLocationRelativeTo(null);

    }

    private void exit() {
        // TODO implement exit
        System.exit(0);
    }


    private void openFile() {
        try {
            fullImage.setImage(ImageIO.read(getClass().getResource("/Lena.bmp")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO implement opening
    }
    private void saveFile() {
        // TODO implement opening
    }
}
