package life_hexagon;

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
        double angle = Math.PI / 2.;
        for (int i = 0; i < 6; ++i) {
            angle += Math.PI / 3.;
            dots[0][i] = (int) (x + radius * Math.cos(angle));
            dots[1][i] = (int) (y + radius * Math.sin(angle));
        }
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
}
