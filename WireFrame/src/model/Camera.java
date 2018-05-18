package model;

import java.util.Objects;
import java.util.Observable;

public class Camera extends Observable {
    private Matrix4x4 proj = new Matrix4x4();
    private Matrix4x4 view = new Matrix4x4();
    private Matrix4x4 viewProj = new Matrix4x4();
    private Vector4 eye = new Vector4(-10, 0, 0);
    private Vector4 target = new Vector4(10, 0, 0);
    private Vector4 up = new Vector4(0, 1, 0);
    private double frontClip = 5;
    private double backClip = 8;
    private double frontWidth = 1;
    private double frontHeight = 1;

    public Camera() {
        recalcMatrices();
    }
    public Matrix4x4 getView() {
        return view;
    }

    public double getFrontClip() {
        return frontClip;
    }

    public Camera setFrontClip(double frontClip) {
        if (!Objects.equals(this.frontClip, frontClip)) {
            this.frontClip = frontClip;
            recalcMatrices();
        }
        return this;
    }

    public double getBackClip() {
        return backClip;
    }

    public Camera setBackClip(double backClip) {
        if (!Objects.equals(this.backClip, backClip)) {
            this.backClip = backClip;
            recalcMatrices();
        }
        return this;
    }

    public double getFrontWidth() {
        return frontWidth;
    }

    public Camera setFrontWidth(double frontWidth) {
        if (!Objects.equals(this.frontWidth, frontWidth)) {
            this.frontWidth = frontWidth;
            recalcMatrices();
        }
        return this;
    }

    public double getFrontHeight() {
        return frontHeight;
    }

    public Camera setFrontHeight(double frontHeight) {
        if (!Objects.equals(this.frontHeight, frontHeight)) {
            this.frontHeight = frontHeight;
            recalcMatrices();
        }
        return this;
    }

    public Matrix4x4 getProj() {
        return proj;
    }

    public Vector4 getEye() {
        return eye;
    }

    public Camera setEye(Vector4 eye) {
        if (!Objects.equals(this.eye, eye)) {
            this.eye = eye;
            recalcMatrices();
        }
        return this;
    }

    public Vector4 getTarget() {
        return target;
    }

    public Camera setTarget(Vector4 target) {
        if (!Objects.equals(this.target, target)) {
            this.target = target;
            recalcMatrices();
        }
        return this;
    }

    public Vector4 getUp() {
        return up;
    }

    public Camera setUp(Vector4 up) {
        if (!Objects.equals(this.up, up)) {
            this.up = up;
            recalcMatrices();
        }
        return this;
    }


    private void recalcMatrices() {
        Vector4 zaxis = eye.sub(target).normalizeAs3D();
        Vector4 xaxis = up.crossProduct(zaxis).normalizeAs3D();
        Vector4 yaxis = zaxis.crossProduct(xaxis);
        Matrix4x4 orientation = new Matrix4x4(new double[]{
                xaxis.p[0], yaxis.p[0], zaxis.p[0], 0,
                xaxis.p[1], yaxis.p[1], zaxis.p[1], 0,
                xaxis.p[2], yaxis.p[2], zaxis.p[2], 0,
                0, 0, 0, 1
        });

        Matrix4x4 translation = new Matrix4x4(new double[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                -eye.p[0], -eye.p[1], -eye.p[2], 1
        });
        view = orientation.mult(translation);

        proj = new Matrix4x4(new double[]{
                2. * frontClip / frontWidth, 0, 0, 0,
                0, 2. * frontClip/ frontHeight, 0, 0,
                0, 0, backClip / (backClip - frontClip), - frontClip * backClip / (backClip - frontClip),
                0, 0, 1, 0
        });

        viewProj = view.mult(proj);
        notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }

    public Matrix4x4 getViewProj() {
        return viewProj;
    }
}
