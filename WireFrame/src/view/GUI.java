package view;

import actions.IconAction;
import model.Matrix4x4;
import model.Scene;
import model.SceneSettings;
import view.panels.SceneEditor;
import view.panels.ScenePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;


public class GUI extends ToolBarStatusBarFrame {
    private ScenePanel scenePanel = new ScenePanel(new Scene(new SceneSettings()));
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
    private IconAction openFile = new IconAction("open file") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            int retval = fileChooser.showOpenDialog(GUI.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())){
                    scenePanel.getScene().load(scanner);
                } catch (IOException | IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(GUI.this, e.getLocalizedMessage(), "open file error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (retval == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(GUI.this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    private IconAction saveFile = new IconAction("save file") {

        @Override
        public void actionPerformed(ActionEvent ae) {
            int retval = fileChooser.showSaveDialog(GUI.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                try (PrintStream stream = new PrintStream(fileChooser.getSelectedFile())){
                    scenePanel.getScene().save(stream);
                } catch (IOException | IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(GUI.this, e.getLocalizedMessage(), "open file error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (retval == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(GUI.this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    private IconAction openSettings = new IconAction("settings") {
        SceneEditor sceneEditor = new SceneEditor(scenePanel.getScene());
        JDialog dialog = new JDialog(GUI.this, "Settings", false);
        {
            dialog.add(sceneEditor);
            dialog.setMinimumSize(new Dimension(500, 500));
            dialog.pack();

            dialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    scenePanel.getScene().selected.setValue(null);
                }

                @Override
                public void componentShown(ComponentEvent e) {
                    scenePanel.getScene().selected.setValue(sceneEditor.getSelected());
                }
            });
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            dialog.setLocationRelativeTo(GUI.this);
            dialog.setVisible(true);
        }
    };

    private IconAction initScene = new IconAction("init") {
        @Override
        public void actionPerformed(ActionEvent e) {
            scenePanel.getScene().globalRotation.setValue(new Matrix4x4.Diagonal());
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
            about.add(new JLabel("WireFrame ver. 1.0"), gbc);
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
        super("WireFrame");
        initWindow();
        initWorkspace();
        initMenuBar();
        initToolBarItems();
        pack();
    }

    private void initWindow() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        try (Scanner scanner = new Scanner(getClass()
                .getResourceAsStream("/config.txt"))) {
            scenePanel.getScene().load(scanner);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exit.actionPerformed(null);
            }
        });
    }

    private void initWorkspace() {
        getWorkWindow().setLayout(new BorderLayout());
        add(scenePanel, BorderLayout.CENTER);

    }

    private void initToolBarItems() {
        JToolBar toolBar = getToolBar();
        toolBar.add(openFile);
        toolBar.add(saveFile);

        toolBar.addSeparator();

        // TODO add edit buttons
        toolBar.add(initScene);
        toolBar.add(openSettings);

        toolBar.addSeparator();

        toolBar.add(showAbout);

        toolBar.addSeparator();

        toolBar.add(exit);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(openFile);
        file.add(exit);

        menuBar.add(file);

        JMenu edit = new JMenu("Edit");

        // add edit menu items
        edit.add(initScene);
        edit.add(openSettings);

        menuBar.add(edit);

        JMenu help = new JMenu("Help");

        help.add(showAbout);

        menuBar.add(help);


        setJMenuBar(menuBar);
    }

}
