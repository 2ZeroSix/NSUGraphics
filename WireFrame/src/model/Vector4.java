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
        for (int i = 0; i < 4; ++i)
            result += p[i] * v.p[i];
        return result;
    }

    public Vector4 add(Vector4 v) {
        Vector4 result = this.clone();
        for (int i = 0; i < 4; ++i)
            result.p[i] += v.p[i];
        return result;
    }

    public Vector4 sub(Vector4 v) {
        Vector4 result = this.clone();
        for (int i = 0; i < 4; ++i)
            result.p[i] -= v.p[i];
        return result;
    }

    public Vector4 normalise() {
        Vector4 result = this.clone();
        for (int i = 0; i < 3; ++i)
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
}
