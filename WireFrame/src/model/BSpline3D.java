package model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.stream.Stream;

public class BSpline3D extends Object3D {
    private BSpline spline = new BSpline();
    private SceneSettings settings;

    BSpline3D(SceneSettings settings) {
        this.settings = settings;
        recount();
        spline.addObserver((o, arg) -> recount());
        settings.a.addListener((observable, oldValue, newValue) -> recount());
        settings.b.addListener((observable, oldValue, newValue) -> recount());
        settings.c.addListener((observable, oldValue, newValue) -> recount());
        settings.d.addListener((observable, oldValue, newValue) -> recount());
        settings.m.addListener((observable, oldValue, newValue) -> recount());
        settings.n.addListener((observable, oldValue, newValue) -> recount());
        settings.k.addListener((observable, oldValue, newValue) -> recount());
    }

    private void recount() {
        clear();
        if (spline.points.size() - 3 != spline.segments.size()) return;
        double step = (settings.b.get() - settings.a.get()) / settings.n.get();
        double subStep = step / settings.k.get();
        for (int t = 0; t < settings.n.get(); ++t) {
            Point2D p1, p2 = spline.getPointAtLength(t * step + settings.a.get());
            rotateVector(new Vector4(p2.getX(), p2.getY()));
            for (int subT = 0; subT < settings.k.get(); ++subT) {
                p1 = p2;
                p2 = spline.getPointAtLength((subT + 1) * subStep + t * step + settings.a.get());
                rotateEdge(new Edge(new Vector4(p1.getX(), p1.getY()), new Vector4(p2.getX(), p2.getY()), spline.getColor()));
            }
        }
        Point2D last = spline.getPointAtLength(settings.b.get());
        rotateVector(new Vector4(last.getX(), last.getY()));
        setChanged(); notifyObservers();
    }

    private void rotateVector(Vector4 vector) {
        double step = Math.toRadians(((double)settings.d.get() - settings.c.get()) / settings.m.get());
        double subStep = step / settings.k.get();
        Matrix4x4 rotationStep = new Matrix4x4.RotationY(subStep);
        Vector4 v1, v2 = new Matrix4x4.RotationY(Math.toRadians(settings.c.get())).mult(vector);
        for (int i = 0; i < settings.m.get() * settings.k.get(); ++i) {
            v1 = v2;
            v2 = rotationStep.mult(v2);
            addEdge(new Edge(v1, v2, spline.getColor()));
        }
    }

    private void rotateEdge(Edge edge) {
        double step = Math.toRadians(((double)settings.d.get() - settings.c.get()) / settings.m.get());
        Matrix4x4 rotationStep = new Matrix4x4.RotationY(step);
        Vector4 v1  = new Matrix4x4.RotationY(Math.toRadians(settings.c.get())).mult(edge.getPoints()[0]), v2 = new Matrix4x4.RotationY(Math.toRadians(settings.c.get())).mult(edge.getPoints()[1]);
        for (int i = 0; i <= settings.m.get(); ++i) {
            addEdge(new Edge(v1, v2, spline.getColor()));
            v1 = rotationStep.mult(v1);
            v2 = rotationStep.mult(v2);
        }
    }
    public BSpline getSpline() {
        return spline;
    }
}
