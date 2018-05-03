package model;

public class Scene {
    private Camera camera;

    Matrix4x4 getViewMatrix() {
        return camera.getMatrix();
    }

    Matrix4x4 get() {
        return null; // TODO implement
    }
}
