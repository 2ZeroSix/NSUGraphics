package view.panels;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Spinner;
import model.BSpline3D;
import model.Object3D;
import model.Scene;
import model.SceneSettings;
import sun.plugin.com.DispatchClient;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SceneEditor extends JPanel {
    private SceneSettings settings;
    private Scene scene;
    JTabbedPane tabbedPane = new JTabbedPane();

    public SceneEditor(Scene scene) {
        this.scene = scene;
        this.settings = scene.getSettings();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = .9;
        {
//            scene.addBSpline();
//            scene.addBSpline();
        }
        initTabs();
        scene.object3DS.addListener((ListChangeListener<Object3D>) c -> {
            boolean[] needUpdate = new boolean[]{false};
            while (c.next()) {
                if (c.wasAdded() && c.getAddedSubList().stream()
                        .filter(o -> o instanceof BSpline3D)
                        .map(o -> ((BSpline3D) o).getSpline())
                        .findFirst().isPresent() ||
                        c.wasRemoved() &&
                                c.getRemoved().stream()
                                        .filter(o -> o instanceof BSpline3D)
                                        .map(o -> ((BSpline3D) o).getSpline())
                                        .findFirst().isPresent()) {
                    needUpdate[0] = true;
                }
            }
            if (needUpdate[0])
                initTabs();
        });
        add(tabbedPane, gbc);
        gbc.weighty = 1;
        gbc.weightx = 1;
        JPanel panel = new JPanel(new GridBagLayout());
        {
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(createSpinnerBox("n", settings.n, new SimpleIntegerProperty(1), new SimpleIntegerProperty(30), 1), gbc);
        }
        {
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(createSpinnerBox("m", settings.m, new SimpleIntegerProperty(1), new SimpleIntegerProperty(30), 1), gbc);
        }
        {
            gbc.gridx = 2;
            gbc.gridy = 1;
            panel.add(createSpinnerBox("k", settings.k, new SimpleIntegerProperty(0), new SimpleIntegerProperty(10), 1), gbc);
        }
        {
            JButton button = new JButton("spline color");
            button.addActionListener(e -> {
                if (tabbedPane.getSelectedComponent() instanceof SplineEditor) {
                    Property<Color> color = ((SplineEditor) (tabbedPane.getSelectedComponent())).getSpline().color;
                    Color tmp = (JColorChooser.showDialog(this, "spline color", color.getValue()));
                    if (tmp != null) {
                        color.setValue(tmp);
                    }
                }
            });
            gbc.gridx = 3;
            panel.add(button, gbc);
        }
        {
            JButton button = new JButton("legend color");
            button.addActionListener(e -> {
                Property<Color> color = settings.legendColor;
                Color tmp = (JColorChooser.showDialog(this, "legend color", color.getValue()));
                if (tmp != null) {
                    color.setValue(tmp);
                }
            });
            gbc.gridx = 4;
            panel.add(button, gbc);
        }
        {
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(createSpinnerBox("a", settings.a, new SimpleDoubleProperty(0), settings.b, .01), gbc);
        }
        {
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(createSpinnerBox("b", settings.b, settings.a, new SimpleDoubleProperty(1), .01), gbc);
        }
        {
            gbc.gridx = 2;
            gbc.gridy = 2;
            panel.add(createSpinnerBox("c", settings.c, new SimpleIntegerProperty(0), settings.d, 1), gbc);
        }
        {
            gbc.gridx = 3;
            gbc.gridy = 2;
            panel.add(createSpinnerBox("d", settings.d, settings.c, new SimpleIntegerProperty(360), 1), gbc);
        }
        {
            JButton button = new JButton("background color");
            button.addActionListener(e -> {
                Property<Color> color = settings.backgroundColor;
                Color tmp = (JColorChooser.showDialog(this, "background color", color.getValue()));
                if (tmp != null) {
                    color.setValue(tmp);
                }
            });
            gbc.gridx = 4;
            panel.add(button, gbc);
        }
        {
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(createSpinnerBox("zn", settings.zn, new SimpleDoubleProperty(0), settings.zf, .01), gbc);
        }
        {
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(createSpinnerBox("zf", settings.zf, settings.zn, new SimpleDoubleProperty(100), .01), gbc);
        }
        {
            gbc.gridx = 2;
            gbc.gridy = 3;
            panel.add(createSpinnerBox("sw", settings.sw, new SimpleDoubleProperty(0), new SimpleDoubleProperty(100), .01), gbc);
        }
        {
            gbc.gridx = 3;
            gbc.gridy = 3;
            panel.add(createSpinnerBox("sh", settings.sh, new SimpleDoubleProperty(0), new SimpleDoubleProperty(100), .01), gbc);
        }
        {
            JButton button = new JButton("points color");
            button.addActionListener(e -> {
                Property<Color> color = settings.pointsColor;
                Color tmp = (JColorChooser.showDialog(this, "points color", color.getValue()));
                if (tmp != null) {
                    color.setValue(tmp);
                }
            });
            gbc.gridx = 4;
            panel.add(button, gbc);
        }

        gbc.weighty = 0.1;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(panel, gbc);
        setSize(new Dimension(400, 400));
    }

    private Box createSpinnerBox(String name, Property<Number> val, Property<Number> min, Property<Number> max, Number step) {
        Box box = new Box(BoxLayout.LINE_AXIS);
        box.add(new JLabel(name));
        SpinnerNumberModel model;
        if (min.getValue() instanceof Integer) {
            model = new SpinnerNumberModel(val.getValue(), min.getValue().intValue(), max.getValue().intValue(), step);
        } else {
            model = new SpinnerNumberModel(val.getValue(), min.getValue().doubleValue(), max.getValue().doubleValue(), step);
        }
        JSpinner spinner = new JSpinner(model);
        box.add(spinner);
        val.addListener((observable, oldValue, newValue) -> model.setValue(newValue));
        model.addChangeListener(e -> val.setValue((Number) model.getValue()));
        min.addListener((o, old, newV) -> model.setMinimum(newV.doubleValue()));
        max.addListener((o, old, newV) -> model.setMaximum(newV.doubleValue()));
        return box;
    }

    private void initTabs() {
        tabbedPane.removeAll();

        List<BSpline3D> splines = scene.getBSplines3D();
        for (int i = 0; i < splines.size(); ++i) {
            tabbedPane.addTab(null,
                    new SplineEditor(settings, splines.get(i).getSpline()));
            JLabel label = new JLabel("spline " + Integer.toString(i + 1));
            int finalI = i;
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        tabbedPane.setSelectedIndex(finalI);
                    } else if (SwingUtilities.isMiddleMouseButton(e)) {
                        int previousSelected = tabbedPane.getSelectedIndex();
                        scene.removeBSpline(finalI);
                        tabbedPane.setSelectedIndex(Math.max(0, previousSelected - (finalI <= previousSelected ? 1 : 0)));
                    }
                }
            });
            tabbedPane.setTabComponentAt(i, label);
        }
        tabbedPane.addTab(null, new JLabel("click + to add splines"));
        JLabel label = new JLabel(" + ");
        label.setFocusable(false);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scene.addBSpline();
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 2);
            }
        });
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, label);
        tabbedPane.setEnabledAt(tabbedPane.getTabCount() - 1, false);
    }
}
