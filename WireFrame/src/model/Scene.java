package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera = new Camera();
    private List<Object3D> object3DS = new ArrayList<>();

    public Scene() {
        object3DS.add(Object3D.getBrick(0.1, .1, .1));
    }

    public void draw(Graphics2D g) {
        Rectangle size = g.getClipBounds();
//        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        double multiplier = Math.min(size.width, size.height);
        if (multiplier <= 0)
            return;

        int width = (int) (multiplier * camera.getFrontWidth());
        int height = (int) (multiplier * camera.getFrontHeight());
        double scaleX = width / (2);
        double scaleY = height / (2);
//        Graphics2D g = canvas.createGraphics();
        object3DS.forEach(object3D -> {
            Matrix4x4 world = object3D.getWorldMatrix();
            Matrix4x4 wvp = world.mult(camera.getViewProj());
            object3D.getEdges().forEach(edge -> {
                Vector4 start = wvp.mult(edge.getPoints()[0]);
                Vector4 end = wvp.mult(edge.getPoints()[1]);
                int x1 = (int) Math.round((start.getX() + 1) * scaleX);
                int y1 = (int) Math.round((start.getY() + 1) * scaleY);
                int x2 = (int) Math.round((end.getX() + 1) * scaleX);
                int y2 = (int) Math.round((end.getY() + 1) * scaleY);
                g.setColor(edge.getColor());
                g.drawLine(x1, y1, x2, y2);
            });
        });
//        return canvas;
    }
}
