package model;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Objects;
import java.util.Observable;

public class Camera extends Observable {

    public final Property<Vector4> eye = new SimpleObjectProperty<>(new Vector4(-10, 0, 0));
    public final Property<Vector4> target = new SimpleObjectProperty<>(new Vector4(0, 0, 0));
    public final Property<Vector4> up = new SimpleObjectProperty<>(new Vector4(0, 1, 0));
    public final DoubleProperty frontClip = new SimpleDoubleProperty(4.5);
    public final DoubleProperty backClip = new SimpleDoubleProperty(13.5);
    public final DoubleProperty frontWidth = new SimpleDoubleProperty(1);
    public final DoubleProperty frontHeight = new SimpleDoubleProperty(1);
    public final Binding<Matrix4x4> proj = Bindings.createObjectBinding(() -> new Matrix4x4(new double[]{
            2. * frontClip.get() / frontWidth.get(), 0, 0, 0,
            0, 2. * frontClip.get() / frontHeight.get(), 0, 0,
            0, 0, -(frontClip.get() + backClip.get()) / (backClip.get() - frontClip.get()), -2 * frontClip.get() * backClip.get() / (backClip.get() - frontClip.get()),
            0, 0, -1, 0
    }), frontClip, backClip, frontHeight, frontWidth);
    public final Binding<Matrix4x4> orientation = Bindings.createObjectBinding(() -> {
        Vector4 zaxis = eye.getValue().sub(target.getValue()).normalizeAs3D();
        Vector4 xaxis = up.getValue().crossProduct(zaxis).normalizeAs3D();
        Vector4 yaxis = zaxis.crossProduct(xaxis);
        return  new Matrix4x4(new double[]{
                xaxis.p[0], xaxis.p[1], xaxis.p[2], 0,
                yaxis.p[0], yaxis.p[1], yaxis.p[2], 0,
                zaxis.p[0], zaxis.p[1], zaxis.p[2], 0,
                0, 0, 0, 1
        });
    }, eye, target, up);
    public final Binding<Matrix4x4> translation = Bindings.createObjectBinding(() -> new Matrix4x4(new double[]{
            1, 0, 0, -eye.getValue().p[0],
            0, 1, 0, -eye.getValue().p[1],
            0, 0, 1, -eye.getValue().p[2],
            0, 0, 0, 1
    }), eye);
    public final Binding<Matrix4x4> view = Bindings.createObjectBinding(() -> orientation.getValue().mult(translation.getValue()), translation, orientation);
    public final Binding<Matrix4x4> viewProj = Bindings.createObjectBinding(() -> proj.getValue().mult(view.getValue()), view, proj);


}
