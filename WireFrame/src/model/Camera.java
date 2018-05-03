package model;

import java.util.Objects;
import java.util.Observable;

public class Camera extends Observable {
    private Matrix4x4 proj = new Matrix4x4();
    private Matrix4x4 view = new Matrix4x4();
    private Vector4 pos = new Vector4();
    private Vector4 lookAt = new Vector4();
    private Vector4 up = new Vector4();
    private double frontClip;
    private double backClip;
    private double frontWidth;
    private double frontHeight;

    public Matrix4x4 getView() {
        return view;
    }

    public double getFrontClip() {
        return frontClip;
    }

    public Camera setFrontClip(double frontClip) {
        if (!Objects.equals(this.frontClip, frontClip)) {
            this.frontClip = frontClip;
            notifyObservers();
        }
        return this;
    }

    public double getBackClip() {
        return backClip;
    }

    public Camera setBackClip(double backClip) {
        if (!Objects.equals(this.backClip, backClip)) {
            this.backClip = backClip;
            notifyObservers();
        }
        return this;
    }

    public double getFrontWidth() {
        return frontWidth;
    }

    public Camera setFrontWidth(double frontWidth) {
        if (!Objects.equals(this.frontWidth, frontWidth)) {
            this.frontWidth = frontWidth;
            notifyObservers();
        }
        return this;
    }

    public double getFrontHeight() {
        return frontHeight;
    }

    public Camera setFrontHeight(double frontHeight) {
        if (!Objects.equals(this.frontHeight, frontHeight)) {
            this.frontHeight = frontHeight;
            notifyObservers();
        }
        return this;
    }

    public Matrix4x4 getProj() {
        return proj;
    }

    public Vector4 getPos() {
        return pos;
    }

    public Camera setPos(Vector4 pos) {
        if (!Objects.equals(this.pos, pos)) {
            this.pos = pos;
            recalcMatrices();
        }
        return this;
    }

    public Vector4 getLookAt() {
        return lookAt;
    }

    public Camera setLookAt(Vector4 lookAt) {
        if (!Objects.equals(this.lookAt, lookAt)) {
            this.lookAt = lookAt;
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

        notifyObservers();
    }

    public Matrix4x4 getMatrix() {
        return null; // TODO implement;
    }
}
