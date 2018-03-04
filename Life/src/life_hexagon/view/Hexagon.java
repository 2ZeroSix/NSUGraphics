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
//        double angle = Math.PI / 2.;
        double mult = Math.sqrt(3) / 2;
//        for (int i = 0; i < 6; ++i) {
//            angle += Math.PI / 3.;
//            dots[0][i] = (int) (x + radius * Math.cos(angle));
//            dots[1][i] = (int) (y + radius * Math.sin(angle));
//        }
        radius = this.radius + borderWidth / 2;
        dots[0][0] = (int) (x);
        dots[1][0] = (int) (y + radius);
        dots[0][1] = (int) (x + radius * mult);
        dots[1][1] = (int) (y + radius / 2);
        dots[0][2] = (int) (x + radius * mult);
        dots[1][2] = (int) (y - radius / 2);
        dots[0][3] = (int) (x);
        dots[1][3] = (int) (y - radius );
        dots[0][4] = (int) (x - radius * mult);
        dots[1][4] = (int) (y - radius / 2);
        dots[0][5] = (int) (x - radius * mult);
        dots[1][5] = (int) (y + radius / 2);
    }

    public Hexagon(int x, int y, int radius, int borderWidth) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.borderWidth = borderWidth;
        recalculateDots();
    }

    public Hexagon() {
        this(0, 0, 0, 1);
    }

    public void draw(MyImage image) {
        image.getMyGraphics().drawPolygon(dots[0], dots[1], 6, borderColor, borderWidth);
//        Graphics g = image.createGraphics();
//        g.setColor(borderColor);
//        g.drawPolygon(dots[0], dots[1], 6);
//        g.dispose();
        image.getMyGraphics().spanFill(x, y, fillColor, borderColor);
    }

    public int getX() {
        return x;
    }

    public Hexagon setX(int x) {
        this.x = x;
        recalculateDots();
        return this;
    }

    public int getY() {
        return y;
    }

    public Hexagon setY(int y) {
        this.y = y;
        recalculateDots();
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public Hexagon setRadius(int radius) {
        this.radius = radius;
        recalculateDots();
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
        double mult = Math.sqrt(3) / 2;
        switch (i) {
            case 5:
                return new Point((x),(y + radius));
            case 4:
                return new Point((int)(x + radius * mult),(y + radius / 2));
            case 3:
                return new Point((int)(x + radius * mult),(y - radius / 2));
            case 2:
                return new Point(x, y - radius);
            case 1:
                return new Point((int)(x - radius * mult),(y - radius / 2));
            case 0:
                return new Point((int)(x - radius * mult),(y + radius / 2));
            default:
                return null;
        }
    }

    protected int rotate(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x);
    }

    protected boolean intersect(Point a, Point b, Point c, Point d) {
        return rotate(a, b, c) * rotate(a, b, d) < 0 && rotate (c, d, a)*rotate(c, d, b) < 0;
    }

    public boolean isInside(Point p) {
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
