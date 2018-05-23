package model;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scene {
    private Camera camera = new Camera();
    public final ListProperty<Object3D> object3DS = new SimpleListProperty<>(new ObservableListWrapper<>(new ArrayList<>()));
    private final SceneSettings settings;
    public final Property<Dimension> displaySize = new SimpleObjectProperty<>(new Dimension(800, 600));
    public final Binding<Matrix4x4.ViewPort> viewPort = Bindings.createObjectBinding(() -> new Matrix4x4.ViewPort(displaySize.getValue().width, displaySize.getValue().height, camera.frontWidth.get(), camera.frontHeight.get()),
            camera.frontWidth, camera.frontHeight, displaySize);
    public final Binding<BufferedImage> canvas;

    public Scene(SceneSettings settings) {
        this.settings = settings;
        Matrix4x4.Rotation globalRotation = new Matrix4x4.Rotation(Math.PI / 4, Math.PI / 4, Math.PI / 4);
        canvas = Bindings.createObjectBinding(() -> {
                    Dimension size = displaySize.getValue();
                    System.gc();
                    double multiplier = Math.min(size.width, size.height);
                    if (multiplier <= 0)
                        return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);

                    int width = (int) (multiplier * camera.frontWidth.get());
                    int height = (int) (multiplier * camera.frontHeight.get());
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = image.createGraphics();
                    g.setColor(settings.backgroundColor.getValue());
                    g.fillRect(0, 0, width - 1, height - 1);
                    double scaleX = width / (2);
                    double scaleY = height / (2);
                    object3DS.forEach(object3D -> {
                        Matrix4x4 world = object3D.getWorldMatrix().mult(globalRotation);
                        Matrix4x4 wvp = camera.viewProj.getValue().mult(world);
                        System.out.println("world: " + world);
                        System.out.println("view:  " + camera.view.getValue());
                        System.out.println("proj:  " + camera.proj.getValue());
                        System.out.println("vp:    " + camera.viewProj.getValue());
                        System.out.println("wvp:   " + wvp);
                        object3D.getEdges().forEach(edge -> {
                            Vector4 start = wvp.mult(edge.getPoints()[0]).normalizeAs3D();
                            Vector4 end = wvp.mult(edge.getPoints()[1]).normalizeAs3D();
                            int x1 = (int) Math.round((start.getX() + 1) * scaleX);
                            int y1 = (int) Math.round((start.getY() + 1) * scaleY);
                            int x2 = (int) Math.round((end.getX() + 1) * scaleX);
                            int y2 = (int) Math.round((end.getY() + 1) * scaleY);
                            g.setColor(edge.getColor());
                            g.drawLine(x1, y1, x2, y2);
                        });
                    });
                    return image;
                }, settings.a, settings.b,
                settings.c, settings.d,
                settings.n, settings.m, settings.k,
                settings.backgroundColor, settings.legendColor,
                camera.viewProj, displaySize, object3DS);
        settings.zn.bindBidirectional(camera.frontClip);
        settings.zf.bindBidirectional(camera.backClip);
        settings.sw.bindBidirectional(camera.frontWidth);
        settings.sh.bindBidirectional(camera.frontHeight);
        object3DS.add(Object3D.getBrick(1, 1, 1));
    }

    public BSpline3D addBSpline() {
        BSpline3D bspline3D = new BSpline3D();
        object3DS.add(bspline3D);
        return bspline3D;
    }

    public void removeBSpline(int i) {
        object3DS.remove(object3DS.stream().filter(o -> o instanceof BSpline3D).skip(i).findFirst().get());
    }

    public List<BSpline3D> getBSplines3D() {
        return object3DS.stream()
                .filter(object3D -> object3D instanceof BSpline3D)
                .map(o -> (BSpline3D) o)
                .collect(Collectors.toList());
    }

    public void draw(Graphics2D g) {

//        return canvas;
    }

    public SceneSettings getSettings() {
        return settings;
    }

}
