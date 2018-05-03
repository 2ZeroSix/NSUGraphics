package view;

import actions.IconAction;
import view.buttons.IconButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class GUI extends ToolBarStatusBarFrame {
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
                    // TODO implement reading config
                } catch (IOException | IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(GUI.this, e.getLocalizedMessage(), "open file error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (retval == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(GUI.this, "can't choose this file", "open file error", JOptionPane.ERROR_MESSAGE);
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
        JPanel workSpace = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(workSpace,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        add(scrollPane, BorderLayout.CENTER);
//        add(workSpace, new IsolinesPanel(model, this::setStatus), 0, 0, 1, 1);
        // TODO add main panel
    }

    private void initToolBarItems() {
        JToolBar toolBar = getToolBar();
        toolBar.add(new IconButton(openFile));

        toolBar.addSeparator();

        // TODO add edit buttons

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

        // add edit menu items

        menuBar.add(edit);

        JMenu help = new JMenu("Help");

        help.add(showAbout);

        menuBar.add(help);


        setJMenuBar(menuBar);
    }

}
