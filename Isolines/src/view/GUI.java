package view;

import actions.IconAction;
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
    private IconAction openFile;
    private IconAction showValue;
    private IconAction toggleInterpolation;
    private IconAction toggleGrid;
    private IconAction toggleMap;
    private IconAction toggleIsolines;
    private IconAction toggleHachures;
    private IconAction toggleIsolineControlPoints;
    private IconAction toggleParabolicIsolines;
    private IconAction showAbout;
    private IconAction exit;

    public GUI() {
        super("Isolines");
        initWindow();
        initWorkspace();
        initActions();
        initMenuBar();
        initToolBarItems();
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
        add(workSpace, new IsolinesPanel(), 0, 0, 1, 1);
    }
    private void initToolBarItems() {
        JToolBar toolBar = getToolBar();


        toolBar.add(new IconButton(showAbout));
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(openFile);
        file.add(exit);

        menuBar.add(file);

        JMenu edit = new JMenu("Edit");
        edit.add(showValue);
        edit.add(toggleInterpolation);
        edit.add(toggleGrid);
        edit.add(toggleMap);
        edit.add(toggleIsolines);
        edit.add(toggleHachures);
        edit.add(toggleIsolineControlPoints);
        edit.add(toggleParabolicIsolines);

        menuBar.add(edit);

        JMenu help = new JMenu("Help");

        help.add(showAbout);

        menuBar.add(help);


        setJMenuBar(menuBar);
    }

    private void initActions() {

        openFile = new IconAction("open file");
        showValue = new IconAction("show value");
        toggleInterpolation = new IconAction("toggle interpolation");
        toggleGrid = new IconAction("toggle grid");
        toggleMap = new IconAction("toggle map");
        toggleIsolines = new IconAction("toggle contour line");
        toggleHachures = new IconAction("toggle hachures");
        toggleIsolineControlPoints = new IconAction("toggle contour line control points");
        toggleParabolicIsolines = new IconAction("toggle parabolic contour lines");
        showAbout =  new IconAction("about") {
            Container about;
            {
                about = new Box(BoxLayout.Y_AXIS);
                try {
                    about.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/me.png")))));
                } catch (IllegalArgumentException | IOException e) {
                }
                about.add(new JLabel("Isolines ver. 1.0"));
                about.add(new JLabel("Bogdan Lukin"));
                about.add(new JLabel("FIT 15206"));
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.this, about, "About", JOptionPane.PLAIN_MESSAGE);
            }
        };

        exit = new IconAction("exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
    }


}
