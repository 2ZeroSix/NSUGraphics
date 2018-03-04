import life_hexagon.controller.Controller;
import life_hexagon.model.ModelFactoryImpl;
import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.FieldView;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.Format;

public class GUI extends JFrame implements DisplayModelObserver, FieldObserver{

    public static void main(String[] args) {
        new GUI();
    }
    private Controller controller;
    private FieldObservable field;
    GUI() {
        super("GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        {
            controller = new Controller(new ModelFactoryImpl());
            controller.addFieldObserver(this);
            FieldView fieldView = new FieldView(controller);
            fieldView.setHorizontalAlignment(SwingConstants.LEFT);
            fieldView.setVerticalAlignment(SwingConstants.TOP);
            JScrollPane scrollPane = new JScrollPane (fieldView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setContentPane(scrollPane);
        }
        {
            JMenuBar menuBar = new JMenuBar();
            {
                JMenu file = new JMenu("File");
                {
                    JMenuItem exit = new JMenuItem("Exit");
                    exit.addActionListener(actionEvent -> System.exit(0));
                    file.add(exit);

                    JMenuItem newDocument = new JMenu("newDocument");
                    newDocument.addActionListener(actionEvent -> {
                        JInternalFrame internalFrame = new JInternalFrame("new document", false, true, false, false);
                        JTextField width = new JFormattedTextField(field.getWidth(0));
                        internalFrame.add(width);
                        JTextField height = new JFormattedTextField(field.getHeight());
                        internalFrame.add(height);
                        add(internalFrame);
                        internalFrame.setSize(200, 200);
                        internalFrame.setVisible(true);
                    });
                    file.add(newDocument);
                }
                menuBar.add(file);
            }
            {
                JMenu about = new JMenu("About");
                menuBar.add(about);
            }
            setJMenuBar(menuBar);
        }


        pack();
        setVisible(true);
    }

    @Override
    public void updateDisplayMode(DisplayModelObservable displayModel) {
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
}
