package view.panels;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

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
        addMouseWheelListener(e -> {
            SceneSettings settings = scene.getSettings();
            double step = - e.getWheelRotation() * 0.1;
            step = Math.min(settings.zf.get() + step, 20.) - settings.zf.get();
            step = Math.max(settings.zn.get() + step, 0) - settings.zn.get();
            settings.zn.set(settings.zn.get() + step);
            settings.zf.set(settings.zf.get() + step);
        });

        int[] mouseX = { 0 };
        int[] mouseY = { 0 };
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();

                mouseX[0] = e.getX();
                mouseY[0] = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double angleX = (mouseX[0] - e.getX()) * (Math.PI / 500);
                double angleY = (e.getY() - mouseY[0]) * (Math.PI / 500);
                BSpline3D selected = scene.selected.getValue();
                if (selected == null) {
                    scene.globalRotation.setValue(scene.globalRotation.getValue().rotate(angleY, -angleX, 0));
                } else {
                    selected.viewSpaceRotation.setValue(selected.viewSpaceRotation.getValue().rotate(angleY, -angleX, 0));
                }


                mouseX[0] = e.getX();
                mouseY[0] = e.getY();
            }
        });

        addKeyListener(new KeyAdapter() {
            private final Set<Character> pressed = new HashSet<>();


            @Override
            public synchronized void keyReleased(KeyEvent e) {
                pressed.remove(e.getKeyChar());
            }
            @Override
            public synchronized void keyPressed(KeyEvent e) {
                pressed.add(e.getKeyChar());
                if (pressed.size() <= 0) return;
                Object3D selected = scene.selected.getValue();
                if (selected == null)
                    return;
                for (Character key : pressed) {

                    double step = 0.1;
                    Vector4 shift;
                    switch (key) {
                        case '6':
                            shift = new Vector4(step, 0, 0);
                            break;
                        case '4':
                            shift = new Vector4(-step, 0, 0);
                            break;
                        case '8':
                            shift = new Vector4(0, step, 0);
                            break;
                        case '2':
                            shift = new Vector4(0, -step, 0);
                            break;
                        case '7':
                            shift = new Vector4(0, 0, -step);
                            break;
                        case '9':
                            shift = new Vector4(0, 0, step);
                            break;
                        default:
                            shift = new Vector4(0, 0, 0);
                    }
                    selected.center.setValue(new Matrix4x4.Shift(shift).mult(selected.center.getValue()));
                }
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
