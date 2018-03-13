package life_hexagon.view;

import java.awt.*;

public class Hexagon {
    private int x;
    private int y;
    private int radius;
    private int borderWidth;
    private Color borderColor = Color.BLACK;
    private Color fillColor = Color.RED;
    private int[][] dots = new int[2][6];

    void recalculateDots() {
        double multiplier = Math.sqrt(3) / 2;
        double rd = (this.radius * multiplier) + (borderWidth + 1) / 2;
        int R = (int) (rd / multiplier);
        int r = (int)rd;
        dots[0][0] = x;
        dots[1][0] = y + R;
        dots[0][1] = x + r;
        dots[1][1] = y + R / 2;
        dots[0][2] = x + r;
        dots[1][2] = y - R / 2;
        dots[0][3] = x;
        dots[1][3] = y - R;
        dots[0][4] = x - r;
        dots[1][4] = y - R / 2;
        dots[0][5] = x - r;
        dots[1][5] = y + R / 2;
    }

    public Hexagon(int x, int y, int radius, int borderWidth) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.borderWidth = borderWidth;
    }

    public Hexagon() {
        this(0, 0, 0, 1);
    }

    public Hexagon draw(MyImage image) {
        drawBorder(image);
        drawInterior(image);
        return this;
    }

    public Hexagon drawBorder(MyImage image) {
        recalculateDots();
        image.getMyGraphics().drawPolygon(dots[0], dots[1], 6, borderColor, borderWidth);
        return this;
    }

    public Hexagon drawInterior(MyImage image) {
        image.getMyGraphics().spanFill(x, y, fillColor, borderColor);
        return this;
    }

    public int getX() {
        return x;
    }

    public Hexagon setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public Hexagon setY(int y) {
        this.y = y;
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public Hexagon setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public Hexagon setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Hexagon setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Hexagon setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    private Point getPoint(int i) {
        int R = this.radius;
        int r = (int) (R * Math.sqrt(3) / 2);
        switch (i) {
            case 5:
                return new Point((x),(y + R));
            case 4:
                return new Point(x + r,(y + R / 2));
            case 3:
                return new Point(x + r,(y - R / 2));
            case 2:
                return new Point(x, y - R);
            case 1:
                return new Point(x - r,(y - R / 2));
            case 0:
                return new Point(x - r,(y + R/ 2));
            default:
                return null;
        }
    }

    protected int rotate(Point a, Point b, Point c) {
        return  (b.x - a.x) * (c.y - b.y) -
                (b.y - a.y) * (c.x - b.x);
    }

    protected boolean intersect(Point a, Point b, Point c, Point d) {
        return  rotate(a, b, c) * rotate(a, b, d) <= 0 &&
                rotate(c, d, a) * rotate(c, d, b) < 0;
    }

    public boolean isContains(Point p) {
        int l = 1, r = 5;
        if (rotate(getPoint(0), getPoint(l), p) < 0 ||
                rotate(getPoint(0), getPoint(r), p) > 0) {
            return false;
        } else {
            while (Math.abs(r - l) > 1) {
                int q = (l + r) / 2;
                if (rotate(getPoint(0), getPoint(q), p) < 0) {
                    r = q;
                } else {
                    l = q;
                }
            }
            return !intersect(getPoint(0), p, getPoint(l), getPoint(r));
        }
    }
}
