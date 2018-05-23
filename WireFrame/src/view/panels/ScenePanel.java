package view.panels;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

public class ScenePanel extends JPanel {
    public Scene getScene() {
        return scene;
    }

    private Scene scene;
    public ScenePanel(Scene scene) {
        this.scene = scene;
        scene.canvas.addListener((observable, oldValue, newValue) -> repaint());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scene.viewPort.setValue(getSize());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(scene.canvas.getValue(), null, 0, 0);
    }
}
