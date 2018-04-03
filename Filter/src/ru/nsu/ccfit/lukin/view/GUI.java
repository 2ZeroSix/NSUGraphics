package ru.nsu.ccfit.lukin.view;

import javafx.stage.FileChooser;
import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.*;
import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.observables.FilteredImageObservable;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.buttons.FilterButton;
import ru.nsu.ccfit.lukin.view.buttons.ImageObserverButton;
import ru.nsu.ccfit.lukin.view.imagePanels.FilteredImage;
import ru.nsu.ccfit.lukin.view.imagePanels.FullImage;
import ru.nsu.ccfit.lukin.view.imagePanels.SelectedImage;
import ru.nsu.ccfit.lukin.view.imagePanels.VolumeConfigurationChart;
import ru.nsu.ccfit.lukin.view.menuItems.FilterMenuItem;
import ru.nsu.ccfit.lukin.view.menuItems.ImageObserverMenuItem;
import ru.nsu.ccfit.lukin.view.observers.FilterObserver;
import ru.nsu.ccfit.lukin.view.observers.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI extends ToolBarStatusBarFrame {
    private FullImage fullImage;
    private SelectedImage selectedImage;
    private FilteredImage filteredImage;
    private Map<String, Container> dialogs;
    private ArrayList<Filter> filters;
    private JFileChooser fileChooser = new JFileChooser();
    private VolumeRenderingFilter volumeRenderingFilter;

    public GUI() {
        super("Filter");
        init();
        postInit();
    }

    private void init() {
        initWindow();
        initAdditionalFrames();
        initWorkspace();
        initMenuBar();
        initToolBarItems();
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
                JMenuItem newDocument = new JMenuItem("new document");
                newDocument.addActionListener(actionEvent -> newFile());
                file.add(newDocument);

                JMenuItem save = new ImageObserverMenuItem("save file", filteredImage) {
                    @Override
                    public void updateImage(ImageObservable observable) {
                        setEnabled(filteredImage.getImage() != null);
                        super.updateImage(observable);
                    }
                };
                save.addActionListener(ae -> saveFile());
                file.add(save);


                JMenuItem fileOpen = new JMenuItem("open file");
                fileOpen.addActionListener(ae -> openFile());
                file.add(fileOpen);


                JMenuItem exit = new JMenuItem("Exit");
                exit.addActionListener(ae -> exit());
                file.add(exit);
            }
            menuBar.add(file);


            JMenu edit = new JMenu("Edit");
//            JMenuItem selectionItem = new JMenuItem("start/stop selection");
//            selectionItem.addActionListener(ae -> {
//                if (fullImage.getImage() != null) {
//                    fullImage.setSelectable(fullImage.isSelectable());
//                } else {
//                    JOptionPane.showMessageDialog(GUI.this, "Nothing to select",
//                            "Select error", JOptionPane.PLAIN_MESSAGE);
//                }
//            });
//            edit.add();
            for (Filter filter : filters) {
                edit.add(new FilterMenuItem(this, filter, selectedImage, filteredImage));
            }
            edit.add(new FilterMenuItem(this, volumeRenderingFilter, selectedImage, filteredImage) {
                private boolean optionsReady = false;

                @Override
                public void updateImage(ImageObservable observable) {
                    try {
                        setEnabled(optionsReady && this.dialog.getFilteredImage().getSelectedImage().getImage() != null);
                    } catch (NullPointerException e) {
                        setEnabled(false);
                    }
                }

                @Override
                public void updateOption(String name, Object value) {
                    optionsReady = true;
                    for (FilterOption<?> option : this.dialog.getFilter().getOptions().values()) {
                        if (option.getValue() == null) optionsReady = false;
                    }
                    updateImage(this.dialog.getFilteredImage());
                }
            });
            ImageObserverMenuItem loadConfiguration = new ImageObserverMenuItem("load VR configuration");
            loadConfiguration.addActionListener(ae -> loadConfig());
            edit.add(loadConfiguration);
            ImageObserverMenuItem emission = new ImageObserverMenuItem("emission") {
                @Override
                public void updateOption(String name, Object value) {
                    if (name.equals("emission")) {
                        this.setSelected((Boolean) value);
                    }
                }
            };
            volumeRenderingFilter.addFilterObserver(emission);
            emission.addActionListener(ae -> volumeRenderingFilter.setOption("emission", !(Boolean) volumeRenderingFilter.getOption("emission").getValue()));
            edit.add(emission);
            ImageObserverMenuItem absorption = new ImageObserverMenuItem("absorption") {
                @Override
                public void updateOption(String name, Object value) {
                    if (name.equals("absorption")) {
                        this.setSelected((Boolean) value);
                    }
                }
            };
            volumeRenderingFilter.addFilterObserver(absorption);
            absorption.addActionListener(ae -> volumeRenderingFilter.setOption("absorption", !(Boolean) volumeRenderingFilter.getOption("absorption").getValue()));
            edit.add(absorption);
            menuBar.add(edit);

            JMenu help = new JMenu("Help");
            {
                JMenuItem about = new JMenuItem("about");
                about.addActionListener(ae -> JOptionPane.showMessageDialog(this, dialogs.get(about.getText()), about.getText(), JOptionPane.PLAIN_MESSAGE));
                help.add(about);
            }
            menuBar.add(help);
        }
        setJMenuBar(menuBar);
    }

    protected void initToolBarItems() {
        JToolBar toolBar = getToolBar();
//        * new
        ButtonGroup group = new ButtonGroup();
        ImageObserverButton newFileButton = new ImageObserverButton("new file");
        newFileButton.addActionListener(ae -> newFile());
        toolBar.add(newFileButton);
//        * open
        ImageObserverButton openFileButton = new ImageObserverButton("open file");
        openFileButton.addActionListener(ae -> openFile());
        toolBar.add(openFileButton);
//        * save
        ImageObserverButton saveFileButton = new ImageObserverButton("save file", filteredImage) {
            @Override
            public void updateImage(ImageObservable observable) {
                setEnabled(filteredImage.getImage() != null);
                super.updateImage(observable);
            }
        };
        saveFileButton.addActionListener(ae -> saveFile());
        toolBar.add(saveFileButton);

        toolBar.addSeparator();
//        * start/stop select
        ImageObserverButton selectButton = new ImageObserverButton("select", fullImage) {
            @Override
            public void updateImage(ImageObservable observable) {
                try {
                    setEnabled(fullImage.getImage() != null);
                } catch (NullPointerException e) {
                    setEnabled(false);
                }
            }

            @Override
            public void updateSelectable(FullImageObservable observable) {
                this.setSelected(fullImage.isSelectable());
            }
        };
        selectButton.addActionListener(ae -> fullImage.setSelectable(!fullImage.isSelectable()));
        toolBar.add(selectButton);


        toolBar.addSeparator();
        for (Filter filter : filters) {
            JButton button = new FilterButton(this, filter, filteredImage);
            toolBar.add(button);
            group.add(button);
        }
        toolBar.addSeparator();
        {
            JButton loadConfiguration = new ImageObserverButton("load VR configuration");
            loadConfiguration.addActionListener(ae -> loadConfig());
            toolBar.add(loadConfiguration);
            ImageObserverButton emission = new ImageObserverButton("emission") {
                @Override
                public void updateOption(String name, Object value) {
                    if (name.equals("emission")) {
                        this.setSelected((Boolean) value);
                    }
                }
            };
            volumeRenderingFilter.addFilterObserver(emission);
            emission.addActionListener(ae -> volumeRenderingFilter.setOption("emission", !(Boolean) volumeRenderingFilter.getOption("emission").getValue()));
            toolBar.add(emission);
            ImageObserverButton absorption = new ImageObserverButton("absorption") {
                @Override
                public void updateOption(String name, Object value) {
                    if (name.equals("absorption")) {
                        this.setSelected((Boolean) value);
                    }
                }
            };
            volumeRenderingFilter.addFilterObserver(absorption);
            absorption.addActionListener(ae -> volumeRenderingFilter.setOption("absorption", !(Boolean) volumeRenderingFilter.getOption("absorption").getValue()));
            toolBar.add(absorption);
            FilterButton volumeRenderingFilterButton = new FilterButton(this, GUI.this.volumeRenderingFilter, filteredImage) {
                private boolean optionsReady = false;

                @Override
                public void updateImage(ImageObservable observable) {
                    try {
                        setEnabled(optionsReady && this.dialog.getFilteredImage().getSelectedImage().getImage() != null);
                    } catch (NullPointerException e) {
                        setEnabled(false);
                    }
                }

                @Override
                public void updateOption(String name, Object value) {
                    optionsReady = true;
                    for (FilterOption<?> option : this.dialog.getFilter().getOptions().values()) {
                        if (option.getValue() == null) optionsReady = false;
                    }
                    updateImage(this.dialog.getFilteredImage());
                }
            };
            this.volumeRenderingFilter.addFilterObserver(volumeRenderingFilterButton);

            toolBar.add(volumeRenderingFilterButton);
            group.add(volumeRenderingFilterButton);
        }
        toolBar.addSeparator();
        ImageObserverButton copyLeftButton = new ImageObserverButton("copy left", selectedImage, filteredImage) {
            @Override
            public void updateImage(ImageObservable observable) {
                setEnabled(filteredImage.getImage() != null);
                super.updateImage(observable);
            }
        };
        copyLeftButton.addActionListener(ae -> {
            selectedImage.setImage(ImageUtils.copy(filteredImage.getImage()));
            filteredImage.setFilter(null);
        });
        toolBar.add(copyLeftButton);
        toolBar.addSeparator();
        JButton about = new ImageObserverButton("about");
        about.addActionListener(ae -> JOptionPane.showMessageDialog(this, dialogs.get(about.getName()), about.getName(), JOptionPane.PLAIN_MESSAGE));
        toolBar.add(about);

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
        VolumeConfigurationChart chart = new VolumeConfigurationChart(volumeRenderingFilter);
        add(workSpace, chart, 0, 1, 3, 1);
    }

    private void initAdditionalFrames() {
        dialogs = new HashMap<>();
        {
            Container about = new Box(BoxLayout.Y_AXIS);
            try {
                about.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/me.png")))));
            } catch (IOException e) {
            }
            about.add(new JLabel("Life ver. 1.0"));
            about.add(new JLabel("Bogdan Lukin"));
            about.add(new JLabel("FIT 15206"));
            dialogs.put("about", about);
        }
        {
            File dir = new File("Data");
            if (dir.isDirectory() ? !dir.canWrite() : !dir.mkdir()) {
                JOptionPane.showMessageDialog(this,
                        "can't open \"Data\" catalog with write rights, using default (" + fileChooser.getCurrentDirectory() + ")",
                        "Default save catalog",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                fileChooser.setCurrentDirectory(dir);
            }
            fileChooser.setFileFilter(new FileNameExtensionFilter("images",
                    "jpg", "jpeg", "bmp", "png", "svg"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileView(new FileView() {
                FileFilter fileFilter = new FileNameExtensionFilter("images",
                        "jpg", "jpeg", "bmp", "png", "svg");
                Map<File, ImageIcon> imageMap = new WeakHashMap<>();
                private int ICON_SIZE = 20;
                Image defaultImage;
                private final ExecutorService executor = Executors.newCachedThreadPool();
                FileView parent = fileChooser.getFileView();

                {
                    try {
                        defaultImage = ImageIO.read(getClass().getResource("/icons/loading.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    } catch (IllegalArgumentException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Icon getIcon(File f) {
                    if (f.isDirectory() || !fileFilter.accept(f))
                        return parent != null ? parent.getIcon(f) : super.getIcon(f);
                    ImageIcon imageIcon = new ImageIcon(defaultImage);
                    Icon icon = imageMap.get(f);
                    synchronized (imageMap) {
                        if (icon != null) return icon;
                        imageMap.put(f, imageIcon);
                    }
                    executor.execute(() -> {
                        try {
                            BufferedImage image = ImageIO.read(f);
                            int w = image.getWidth();
                            int h = image.getHeight();
                            int dw = 0;
                            int dh = 0;
                            if (w > h) {
                                h = h * ICON_SIZE / w;
                                w = ICON_SIZE;
                                dh = (ICON_SIZE - h) / 2;
                            } else {
                                w = w * ICON_SIZE / h;
                                h = ICON_SIZE;
                                dw = (ICON_SIZE - w) / 2;
                            }
                            Image scaledImage = ImageIO.read(f).getScaledInstance(w, h, Image.SCALE_SMOOTH);
                            BufferedImage centeredImage = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
                            Graphics g = centeredImage.getGraphics();
                            g.drawImage(scaledImage, dw, dh, null);
                            g.dispose();
                            imageIcon.setImage(centeredImage);
                            fileChooser.repaint();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return imageIcon;
                }
            });
        }
        {
            filters = new ArrayList<>();
            filters.add(new BlackAndWhiteFilter());
            filters.add(new NegativeFilter());
            filters.add(new FloydSteinbergDithering(8, 8, 4));
            filters.add(new OrderedDithering(8, 8, 4));
            filters.add(new NearestNeighborDoubleFilter());
            filters.add(new SobelFilter(80));
            filters.add(new RobertsFilter(25));
            filters.add(new SmoothFilter());
            filters.add(new SharpFilter());
            filters.add(new EmbossFilter());
            filters.add(new AquaFilter());
            filters.add(new RotateFilter(0.));
            filters.add(new GammaFilter(1.));
            volumeRenderingFilter = new VolumeRenderingFilter();
        }
    }

    private void postInit() {
        pack();
        setLocationRelativeTo(null);

    }

    private void exit() {
        System.exit(0);
    }

    private void newFile() {
        fullImage.clean();
        selectedImage.clean();
        filteredImage.clean();
    }

    private void openFile() {
        int retval = fileChooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                fullImage.clean();
                fullImage.setImage(image);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "open file error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (retval == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadConfig() {
        FileFilter old = fileChooser.getFileFilter();
        fileChooser.setFileFilter(new FileNameExtensionFilter("configuration file", "txt"));
        int retval = fileChooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            try {
                new VRFilterLoader(volumeRenderingFilter).load(fileChooser.getSelectedFile());
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "configuration reading error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (retval == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
        }
        fileChooser.setFileFilter(old);
    }

    private void saveFile() {
        if (filteredImage.getImage() != null) {
            int retval = fileChooser.showSaveDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    String[] split = file.getName().split("\\.");

                    if (!ImageIO.write(filteredImage.getImage(), split[split.length - 1], fileChooser.getSelectedFile())) {
                        JOptionPane.showMessageDialog(this, "wrong extension", "save file error", JOptionPane.ERROR_MESSAGE);
                        saveFile();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "save file error", JOptionPane.ERROR_MESSAGE);
                    saveFile();
                }
            } else if (retval == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(this, "can't choose this file", "save file error", JOptionPane.ERROR_MESSAGE);
                saveFile();
            }
        } else {
            JOptionPane.showMessageDialog(this, "nothing to save", "save file error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
