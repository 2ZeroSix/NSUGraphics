package model;

import java.util.Arrays;

public class Vector4 {
    protected double[] p = new double[4];

    public Vector4() {
        this(0);
    }

    public Vector4(double x) {
        this(x, 0);
    }

    public Vector4(double x, double y) {
        this(x, y, 0);
    }

    public Vector4(double x, double y, double z) {
        this(x, y, z, 1);
    }

    public Vector4(double x, double y, double z, double w) {
        p[0] = x;
        p[1] = y;
        p[2] = z;
        p[3] = w;
    }

    public Vector4(double[] v) {
        this(v.length > 0 ? v[0] : 0, v.length > 1 ? v[1] : 0, v.length > 2 ? v[2] : 0, v.length > 3 ? v[3] : 1);
    }

    public Vector4(Vector4 v) {
        this(v.p);
    }


    public double dotProduct(Vector4 v) {
        double result = 0;
        for (int i = 0; i < 3; ++i)
            result += p[i] * v.p[i];
        return result;
    }

    public Vector4 crossProduct(Vector4 v) {
        Vector4 result = new Vector4();
        result.set(0, get(1) * v.get(2) - get(2) * v.get(1));
        result.set(1, get(2) * v.get(0) - get(0) * v.get(2));
        result.set(2, get(0) * v.get(1) - get(1) * v.get(0));
        return result;
    }

    public Vector4 add(Vector4 v) {
        return new Matrix4x4.Shift(v).mult(this);
    }

    public Vector4 sub(Vector4 v) {
        return new Matrix4x4.Shift(new Matrix4x4.Scale(-1).mult(v)).mult(this);
    }

    public double len() {
        return Math.sqrt(get(0)*get(0) + get(1)*get(1) + get(2)*get(2));
    }

    public Vector4 normalizeAs3D() {
        Vector4 result = clone();
        double len = len();
        for (int i = 0; i < 3; ++i)
            result.p[i] /= len;
        result.p[3] = 1;
        return result;
    }

    public Vector4 normalize() {
        Vector4 result = this.clone();
        for (int i = 0; i < 4; ++i)
            result.p[i] /= p[3];
        return result;
    }

    public double get(int i) {
        return p[i];
    }

    public void set(int i, double val) {
        p[i] = val;
    }

    @Override
    public Vector4 clone() {
        return new Vector4(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector4) {
            Vector4 v = (Vector4) o;
            return Arrays.equals(p, v.p);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(p);
    }

    public void shift(Vector4 vector4) {
        Vector4 v = new Matrix4x4.Shift(vector4).mult(this);
        p = v.p;
    }

    public double getX() {
        return get(0);
    }

    public double getY() {
        return get(1);
    }

    public double getZ() {
        return get(2);
    }

    public double getW() {
        return p[3];
    }

    @Override
    public String toString() {
        return "Vector4{" +
                "p=" + Arrays.toString(p) +
                '}';
    }
}
