package model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Matrix4x4 {
    protected double[][] mat = new double[4][4];
    protected boolean transposed = false;

    public Matrix4x4 rotate(double angleX, double angleY, double angleZ) {
        return this.mult(new RotationX(angleX)).mult(new RotationY(angleY)).mult(new RotationZ(angleZ));
    }

    public static class Diagonal extends Matrix4x4{
        public Diagonal() {
            this(1);
        }
        public Diagonal(double val) {
            for (int i = 0; i < 4; ++i) {
                set(i, i, val);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder start = new StringBuilder("Matrix4x4{" +
                "mat=[\n");
        for (int i = 0; i < 4; ++i) {
            start.append(Arrays.toString(mat[i]));
            start.append("\n");
        }
        return  start +
                "], transposed=" + transposed +
                '}';
    }

    public static class ViewPort extends Matrix4x4 {
        public ViewPort(int displayWidth, int displayHeight, double cameraWidth, double cameraHeight) {
            double multiplier = Math.min(displayWidth, displayHeight);
            int width = (int) (multiplier * cameraWidth);
            int height = (int) (multiplier * cameraHeight);
            double scaleX = width / (2);
            double scaleY = height / (2);
            Matrix4x4 viewPort = (new Scale(new Vector4(scaleX, scaleY))).mult(new Shift(new Vector4(1, 1)));
            this.mat = viewPort.mat;
            this.transposed = viewPort.transposed;
        }
    }

    public static class Rotation extends Matrix4x4 {
        public Rotation(double anglex, double angley, double anglez) {
            super(new RotationZ(anglez).mult(new RotationY(angley)).mult(new RotationX(anglex)));
        }
    }
    public static class RotationX extends Matrix4x4 {
        public RotationX(double angle) {
            super(new double[][]{
                    { 1,            0,           0,          0 },
                    { 0,            cos(angle), -sin(angle), 0 },
                    { 0,            sin(angle),  cos(angle), 0 },
                    { 0,            0,           0,          1 } });
        }
    }

    public static class RotationY extends Matrix4x4 {
        public RotationY(double angle) {
            super(new double[][]{
                    {  cos(angle),  0,           sin(angle), 0 },
                    {  0,           1,           0,          0 },
                    { -sin(angle),  0,           cos(angle), 0 },
                    {  0,           0,           0,          1 } });
        }
    }

    public static class RotationZ extends Matrix4x4 {
        public RotationZ(double angle) {
            super(new double[][]{
                    {  cos(angle), -sin(angle),  0,          0 },
                    {  sin(angle),  cos(angle),  0,          0 },
                    {  0,            0,          1,          0 },
                    {  0,            0,          0,          1 } });
        }
    }

    public static class Shift extends Matrix4x4 {
        public Shift(Vector4 vector) {
            super(new double[][]{
                    {1, 0, 0, vector.get(0)},
                    {0, 1, 0, vector.get(1)},
                    {0, 0, 1, vector.get(2)},
                    {0, 0, 0, 1}
            });
        }
    }

    public static class Scale extends Matrix4x4 {
        public Scale(double factor) {
            super(new double[][]{
                    { factor, 0,      0,      0 },
                    { 0,      factor, 0,      0 },
                    { 0,      0,      factor, 0 },
                    { 0,      0,      0,      1 }
            });
        }
        public Scale(Vector4 factors) {
            super(new double[][]{
                    { factors.get(0), 0,      0,      0 },
                    { 0,      factors.get(1), 0,      0 },
                    { 0,      0,      factors.get(2), 0 },
                    { 0,      0,      0,      1 }
            });
        }
    }


    public Matrix4x4() {
    }

    public Matrix4x4(double[][] matrix4x4) {
        for (int i = 0; i < Integer.min(4, matrix4x4.length); ++i) {
            for (int j = 0; j < Integer.min(4, matrix4x4[i].length); ++j) {
                mat[i][j] = matrix4x4[i][j];
            }
        }
    }

    public Matrix4x4(double[] matrix4x4) {
        for (int i = 0; i < Integer.min(16, matrix4x4.length); ++i) {
            mat[i / 4][i % 4] = matrix4x4[i];
        }
    }

    public Matrix4x4(Matrix4x4 matrix4x4) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                mat[i][j] = matrix4x4.get(i, j);
            }
        }
    }

    Vector4 getRow(int row) {
        return new Vector4(mat[row]);
    }

    Vector4 getColumn(int column) {
        return new Vector4(mat[0][column], mat[1][column], mat[2][column], mat[3][column]);
    }

    public double get(Point p) {
        return get(p.x, p.y);
    }

    public double get(int row, int column) {
        return transposed ? mat[column][row] : mat[row][column];
    }

    public void set(Point p, double val) {
        set(p.x, p.y, val);
    }

    public void set(int row, int column, double val) {
        if (transposed) {
            mat[column][row] = val;
        } else {
            mat[row][column] = val;
        }
    }

    public void transpose() {
        transposed = !transposed;
    }

    public Matrix4x4 getTransposed() {
        transpose();
        Matrix4x4 result = clone();
        transpose();
        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(mat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Matrix4x4) {
            Matrix4x4 m = (Matrix4x4) o;
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    if (get(i, j) != m.get(i, j))
                        return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public Matrix4x4 mult(Matrix4x4 matrix4x4) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    result.mat[i][j] += get(i, k) * matrix4x4.get(k, j);
                }
            }
        }
        return result;
    }

    public Vector4 mult(Vector4 v) {
        Vector4 result = new Vector4(0,0,0,0);
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                result.p[i] += v.get(j) * get(i, j);
            }
        }
        return result;
    }

    public Matrix4x4 getInversed() {
        throw new NotImplementedException();
    }

    @Override
    public Matrix4x4 clone() {
        return new Matrix4x4(this);
    }
}
