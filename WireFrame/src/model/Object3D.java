package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Object3D {

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

    private Matrix4x4 scale = new Matrix4x4.Diagonal();
    private Matrix4x4 rotation = new Matrix4x4.Diagonal();
    private Vector4 center = new Vector4();

    private Edge[] axises = new Edge[3];
    private ArrayList<Edge> edges = new ArrayList<>();

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
                new Vector4(1, 0,0), Color.RED);

        axises[1] = new Object3D.Edge(
                new Vector4(),
                new Vector4(0, 1, 0), Color.GREEN);

        axises[2] = new Object3D.Edge(
                new Vector4(),
                new Vector4(0, 0, 1), Color.BLUE);
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

//    ------   moving   ------

    public void rotate(double angleX, double angleY, double angleZ) {
        Matrix4x4 rotation = new Matrix4x4.Diagonal().rotate(angleX, angleY, angleZ);
        this.rotation.mult(rotation);
    }

//    public void rotate(Matrix4x4 Matrix4x4) {
//        rotation.mult(Matrix4x4);
//    }

    public void shift(Vector4 Vector4) {
        center.shift(Vector4);
    }

//    ------   get data   ------

    public Vector4[] getMinMaxPoints() {
        double[] minMaxX = { center.getX(), center.getX() };
        double[] minMaxY = { center.getY(), center.getY() };
        double[] minMaxZ = { center.getZ(), center.getZ() };

        edges.stream()
                .flatMap(edge -> Arrays.stream(edge.getPoints()))
                .forEach(Vector4 -> {
                    Vector4 __Vector4 = rotation.mult(new Matrix4x4.Shift(center)).mult(Vector4);

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
        return rotation;
    }

    public Vector4 getCenter() {
        return center;
    }

    Matrix4x4 getWorldMatrix() {
        return scale.mult(new Matrix4x4.Shift(center)).mult(rotation);
    }
}
