package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.filters.*;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.view.buttons.FilterButton;
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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GUI extends ToolBarStatusBarFrame{
    private FullImage fullImage;
    private SelectedImage selectedImage;
    private FilteredImage filteredImage;
    private Map<String, Container> dialogs;
    private Map<String, FilterOptionsDialog> filterOptionsDialogs;
    private JFileChooser fileChooser = new JFileChooser();

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
        newFileButton.addActionListener(ae -> fullImage.setImage(null));
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
//        * ч/б
        FilterButton blackWhiteButton = new FilterButton(this,
                new FilterOptionsDialog(new BlackAndWhiteFilter(), filteredImage, true));
        toolBar.add(blackWhiteButton);
//        * Негатив
        FilterButton negativeButton = new FilterButton(this,
                new FilterOptionsDialog(new NegativeFilter(), filteredImage, true));
        toolBar.add(negativeButton);
//        * Дизеринг
//        *   Флойда-Стейнберга - FloydSteinbergDithering
        FilterButton floydSteinbergButton = new FilterButton(this,
                new FilterOptionsDialog(new FloydSteinbergDithering(8, 8, 4), filteredImage, true));
        toolBar.add(floydSteinbergButton);
//        *   ordered dither - OrderedDithering
        FilterButton orderedDitheringButton = new FilterButton(this,
                new FilterOptionsDialog(new OrderedDithering(8, 8, 4), filteredImage, true));
        toolBar.add(orderedDitheringButton);
//        * Удвоение - NearestNeighborDoubleFilter
        FilterButton doubleFilterButton = new FilterButton(this,
                new FilterOptionsDialog(new NearestNeighborDoubleFilter(), filteredImage, true));
        toolBar.add(doubleFilterButton);
//        * Дифференцирующий фильтр
//        * Собеля - SobelFilter
        FilterButton sobelFilter = new FilterButton(this,
                new FilterOptionsDialog(new SobelFilter(15), filteredImage, true));
        toolBar.add(sobelFilter);
//        * Робертса -
        FilterButton robertsFilter = new FilterButton(this,
                new FilterOptionsDialog(new RobertsFilter(), filteredImage, true));
        toolBar.add(robertsFilter);
//        * Сглаживающий фльтр -
        FilterButton smoothFilterButton = new FilterButton(this,
                new FilterOptionsDialog(new SmoothFilter(), filteredImage, true));
        toolBar.add(smoothFilterButton);
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
        }
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
        int retval = fileChooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            try {
                fullImage.setImage(ImageIO.read(fileChooser.getSelectedFile()));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "open file error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (retval == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void saveFile() {
        int retval = fileChooser.showSaveDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            try {
                String[] split = fileChooser.getSelectedFile().getName().split(".");
                ImageIO.write(filteredImage.getImage(), split[split.length - 1], fileChooser.getSelectedFile());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "save file error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (retval == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(this, "can't choose this file", "save file error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
