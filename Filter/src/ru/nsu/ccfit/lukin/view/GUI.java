package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.*;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.view.buttons.FilterButton;
import ru.nsu.ccfit.lukin.view.buttons.FilterMenuItem;
import ru.nsu.ccfit.lukin.view.buttons.ImageObserverButton;
import ru.nsu.ccfit.lukin.view.imagePanels.FilteredImage;
import ru.nsu.ccfit.lukin.view.imagePanels.FullImage;
import ru.nsu.ccfit.lukin.view.imagePanels.SelectedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI extends ToolBarStatusBarFrame{
    private FullImage fullImage;
    private SelectedImage selectedImage;
    private FilteredImage filteredImage;
    private Map<String, Container> dialogs;
    private ArrayList<FilterOptionsDialog> filterOptionsDialogs;
    private JFileChooser fileChooser = new JFileChooser();

    public GUI() {
        super("Filter");
        init();
        postInit();
    }

    private void init() {
        initWindow();
        initWorkspace();
        initAdditionalFrames();
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
                newDocument.addActionListener(actionEvent -> fullImage.setImage(null));
                file.add(newDocument);

                JMenuItem save = new JMenuItem("save file");
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
            for (FilterOptionsDialog dialog : filterOptionsDialogs) {
                edit.add(new FilterMenuItem(this, dialog));
            }
            // TODO add edit menu items
            menuBar.add(edit);

            JMenu help = new JMenu("Help");
            {
                JMenuItem about = new JMenuItem("about");
                about.addActionListener(ae -> {
                    JOptionPane.showMessageDialog(this, dialogs.get(about.getText()), about.getText(), JOptionPane.PLAIN_MESSAGE);
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
        ImageObserverButton newFileButton = new ImageObserverButton("new file");
        newFileButton.addActionListener(ae -> {
            fullImage.clean();
            selectedImage.setImage(null);
            filteredImage.setImage(null);
        });
        toolBar.add(newFileButton);
//        * open
        ImageObserverButton openFileButton = new ImageObserverButton("open file");
        openFileButton.addActionListener(ae -> openFile());
        toolBar.add(openFileButton);
//        * save
        ImageObserverButton saveFileButton = new ImageObserverButton("save file");
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
        for (FilterOptionsDialog dialog : filterOptionsDialogs) {
            JButton button = new FilterButton(this, dialog);
            toolBar.add(button);
            group.add(button);
        }

        ImageObserverButton copyLeftButton = new ImageObserverButton("copy left", selectedImage, filteredImage);
        copyLeftButton.addActionListener(ae -> selectedImage.setImage(filteredImage.getImage()));
        toolBar.add(copyLeftButton);
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
        }
        {
            filterOptionsDialogs = new ArrayList<>();
            filterOptionsDialogs.add(new FilterOptionsDialog(new BlackAndWhiteFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new NegativeFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new FloydSteinbergDithering(8, 8, 4), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new OrderedDithering(8, 8, 4), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new NearestNeighborDoubleFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new SobelFilter(80), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new RobertsFilter(25), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new SmoothFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new SharpFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new EmbossFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new AquaFilter(), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new RotateFilter(0.), filteredImage, true));
            filterOptionsDialogs.add(new FilterOptionsDialog(new GammaFilter(1.), filteredImage, true));
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
