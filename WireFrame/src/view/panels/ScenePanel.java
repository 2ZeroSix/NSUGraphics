package view.panels;

import model.*;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

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
                scene.displaySize.setValue(new Dimension(getWidth() - 7, getHeight() - 7));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(scene.getSettings().legendColor.getValue());
        BufferedImage image = scene.canvas.getValue();
        int xStart = getWidth() / 2 - (image.getWidth() + 7) / 2;
        int yStart = getHeight() / 2 - (image.getHeight() + 7) / 2;
        g2.drawRect(xStart + 2, yStart + 2, image.getWidth() + 2, image.getHeight() + 2);
        g2.drawImage(image, null, xStart + 4, yStart + 4);
    }
}
