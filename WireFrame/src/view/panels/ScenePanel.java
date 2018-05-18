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
    Scene scene = new Scene();
    public ScenePanel() {
//        ------   resize   ------

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });

//        ------   rotate   ------

//        int[] mouseX = { 0 };
//        int[] mouseY = { 0 };
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                grabFocus();
//
//                mouseX[0] = e.getX();
//                mouseY[0] = e.getY();
//            }
//        });
//        addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                double angleX = (mouseX[0] - e.getX()) * (Math.PI / 500);
//                double angleY = (e.getY() - mouseY[0]) * (Math.PI / 500);
//
//                FigureMover figureMover = FigureMover.getInstance();
//                if (figureMover.getFigure() != null && figureMover.isEnable()) {
//                    Vector4.Translation cameraTranslation = new Vector4.Translation(Vector4.zero(),
//                            camera.axisX, camera.axisY, camera.axisZ);
//                    Matrix4x4 rotation = Matrix4x4.identity()
//                            .apply(cameraTranslation)
//                            .apply(camera.getRotation())
//                            .rotate(angleX, angleY, 0)
//                            .applyInversed(camera.getRotation())
//                            .applyInversed(cameraTranslation);
//
//                    figureMover.getFigure().rotate(rotation);
//                } else {
//                    camera.rotate(angleX, angleY);
//                }
//
//                mouseX[0] = e.getX();
//                mouseY[0] = e.getY();
//
//                update();
//            }
//        });

//        ------   scroll   ------

//        addMouseWheelListener(e -> {
//            camera.move(e.getWheelRotation());
//        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        scene.draw((Graphics2D) g);
    }
}
