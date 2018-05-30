package model;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Object3D extends java.util.Observable {

    protected static class Edge {
        private Vector4[] points = new Vector4[2];
        private Color color = Color.BLACK;

        public Edge(Vector4 p1, Vector4 p2, Color color) {
            points[0] = p1;
            points[1] = p2;

            this.color = color;
        }

        public Edge(Vector4 p1, Vector4 p2) {
            points[0] = p1;
            points[1] = p2;
        }

        public Vector4[] getPoints() {
            return points;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

    public final Property<Matrix4x4> scale = new SimpleObjectProperty<>(new Matrix4x4.Diagonal());
    public final Property<Matrix4x4> rotation = new SimpleObjectProperty<>(new Matrix4x4.Diagonal());
    public final Property<Matrix4x4> viewSpaceRotation = new SimpleObjectProperty<>(new Matrix4x4.Diagonal());
    public final Property<Vector4> center = new SimpleObjectProperty<>(new Vector4());
    public final Property<Vector4> viewSpaceCenter = new SimpleObjectProperty<>(new Vector4());
    {
        scale.addListener((observable, oldValue, newValue) -> {setChanged();notifyObservers();});
        rotation.addListener((observable, oldValue, newValue) -> {setChanged();notifyObservers();});
        viewSpaceRotation.addListener((observable, oldValue, newValue) -> {setChanged();notifyObservers();});
        center.addListener((observable, oldValue, newValue) -> {setChanged();notifyObservers();});
        viewSpaceCenter.addListener((observable, oldValue, newValue) -> {setChanged();notifyObservers();});
    }
    private Edge[] axises = new Edge[3];
    public final ListProperty<Edge> edges = new SimpleListProperty<>(new ObservableListWrapper<>(new ArrayList<>()));

    public static Object3D getBrick(double sizeX, double sizeY, double sizeZ) {
        Vector4 p000 = new Vector4( sizeX,  sizeY,  sizeZ);
        Vector4 p001 = new Vector4( sizeX,  sizeY, -sizeZ);
        Vector4 p010 = new Vector4( sizeX, -sizeY,  sizeZ);
        Vector4 p011 = new Vector4( sizeX, -sizeY, -sizeZ);
        Vector4 p100 = new Vector4(-sizeX,  sizeY,  sizeZ);
        Vector4 p101 = new Vector4(-sizeX,  sizeY, -sizeZ);
        Vector4 p110 = new Vector4(-sizeX, -sizeY,  sizeZ);
        Vector4 p111 = new Vector4(-sizeX, -sizeY, -sizeZ);

        return new Object3D()
                .addEdge(new Edge(p000, p001))
                .addEdge(new Edge(p000, p010))
                .addEdge(new Edge(p000, p100))
                .addEdge(new Edge(p001, p011))
                .addEdge(new Edge(p001, p101))
                .addEdge(new Edge(p010, p011))
                .addEdge(new Edge(p010, p110))
                .addEdge(new Edge(p011, p111))
                .addEdge(new Edge(p100, p101))
                .addEdge(new Edge(p100, p110))
                .addEdge(new Edge(p101, p111))
                .addEdge(new Edge(p110, p111));
    }

    protected Object3D() {
        axises[0] = new Object3D.Edge(
                new Vector4(),
                new Vector4(.1, 0,0), Color.RED);

        axises[1] = new Object3D.Edge(
                new Vector4(),
                new Vector4(0, .1, 0), Color.GREEN);

        axises[2] = new Object3D.Edge(
                new Vector4(),
                new Vector4(0, 0, .1), Color.BLUE);
    }

    protected Object3D addEdge(Edge edge) {
        Edge tmp = new Edge(
                edge.points[0].clone(),
                edge.points[1].clone(), edge.color);
        edges.add(tmp);

        return this;
    }

    protected void clear() {
        edges.clear();
    }


    public void rotate(double angleX, double angleY, double angleZ) {
        this.rotation.setValue(this.rotation.getValue().rotate(angleX, angleY, angleZ));
    }

    public void shift(Vector4 vector4) {
        this.center.setValue(this.center.getValue().shift(vector4));
    }


    public Vector4[] getMinMaxPoints() {
        double[] minMaxX = { center.getValue().getX(), center.getValue().getX() };
        double[] minMaxY = { center.getValue().getY(), center.getValue().getY() };
        double[] minMaxZ = { center.getValue().getZ(), center.getValue().getZ() };

        edges.stream()
                .flatMap(edge -> Arrays.stream(edge.getPoints()))
                .forEach(Vector4 -> {
                    Vector4 __Vector4 = getWorldMatrix().mult(Vector4);

                    minMaxX[0] = Double.min(__Vector4.getX(), minMaxX[0]);
                    minMaxX[1] = Double.max(__Vector4.getX(), minMaxX[1]);

                    minMaxY[0] = Double.min(__Vector4.getY(), minMaxY[0]);
                    minMaxY[1] = Double.max(__Vector4.getY(), minMaxY[1]);

                    minMaxZ[0] = Double.min(__Vector4.getZ(), minMaxZ[0]);
                    minMaxZ[1] = Double.max(__Vector4.getZ(), minMaxZ[1]);
                });

        return new Vector4[]{
                new Vector4(minMaxX[0], minMaxY[0], minMaxZ[0]),
                new Vector4(minMaxX[1], minMaxY[1], minMaxZ[1])
        };
    }

    List<Edge> getEdges() {
        return Stream.concat(Arrays.stream(axises), edges.stream()).collect(Collectors.toList());
    }

    public Matrix4x4 getRotation() {
        return rotation.getValue();
    }

    public Vector4 getCenter() {
        return center.getValue();
    }

    Matrix4x4 getWorldMatrix() {
        return scale.getValue().mult(new Matrix4x4.Shift(viewSpaceCenter.getValue())).mult(new Matrix4x4.Shift(center.getValue())).mult(rotation.getValue());
    }
}
