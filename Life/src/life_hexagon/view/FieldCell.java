package life_hexagon.view;

import life_hexagon.model.observables.FieldObservable;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class FieldCell extends Hexagon {
    private static Color textColor = Color.BLACK;
    private static Color DEAD = (new Color(0xFFFFFF));
    private static Color DEAD_NEXT_ALIVE = (new Color(0xFF8C14));
    private static Color DEAD_NEARLY_ALIVE = (new Color(0xF3FF28));
    private static Color ALIVE_NEXT_DEAD = (new Color(0x75FF78));
    private static Color ALIVE_NEARLY_DEAD = (new Color(0x11FF27));
    private static Color ALIVE = (new Color(0x008000));
    private boolean printImpact = false;
    private boolean fullColor = false;
    private float impact = 0.f;

    public FieldCell() {
        super(0, 0, 0, 0);
    }


    private Color calculateFillColor(FieldObservable field, int row, int column) {
        boolean state = field.getState(row, column);
        if (state) {
            if (fullColor)
                return calculateFillColor(field, ALIVE, ALIVE_NEARLY_DEAD, ALIVE_NEXT_DEAD);
            else
                return calculateFillColor(field, ALIVE, ALIVE, ALIVE);
        } else {
            if (fullColor)
                return calculateFillColor(field, DEAD_NEXT_ALIVE, DEAD_NEARLY_ALIVE, DEAD);
            else
                return calculateFillColor(field, DEAD, DEAD, DEAD);
        }
    }

    private Color calculateFillColor(FieldObservable field,
                                     Color birthBounds, Color liveBounds, Color notBounds) {
        if (field.getBirthBegin() <= impact && impact <= field.getBirthEnd()) {
            return birthBounds;
        } else if (field.getLiveBegin() <= impact && impact <= field.getLiveEnd()) {
            return liveBounds;
        } else {
            return notBounds;
        }
    }

    public FieldCell updateParams(FieldObservable field, int row, int column) {
        impact = field.getImpact(row, column);
        setFillColor(calculateFillColor(field, row, column));
        Point pos = calculatePositionOnScreen(row, column);
        setX(pos.x).setY(pos.y);
        return this;
    }

    public FieldCell drawWithoutBorder(MyImage image) {
        if (isPrintImpact())
            cleanImpact(image);
        drawInterior(image);
        if (isPrintImpact())
            drawImpact(image);
        return this;
    }

    @Override
    public FieldCell draw(MyImage image) {
        drawBorder(image).drawWithoutBorder(image);
        return this;
    }

    @Override
    public FieldCell drawBorder(MyImage image) {
        super.drawBorder(image);
        return this;
    }

    @Override
    public FieldCell drawInterior(MyImage image) {
        super.drawInterior(image);
        return this;
    }

    public FieldCell cleanImpact(MyImage image) {
        int R = getRadius();
        int r = (int) (R * Math.sqrt(3) / 2);
        image
                .getMyGraphics()
                .fillRectangle(
                        new Rectangle(getX() - r, getY() - R / 2, r * 2, R),
                        new Color(0, true));
        return this;
    }

    public FieldCell drawImpact(MyImage image) {
        if (isPrintImpact()) {
            Graphics2D g = image.createGraphics();
            g.setPaint(textColor);
            float impactValue = impact;
            List<String> impactStrings = new ArrayList<>();
            if (impactValue != (long) impactValue) {
                if ((long)(impactValue * 10) * 10 != (long) (impactValue * 100))
                    impactStrings.add(String.format("%.2f", impactValue));
                impactStrings.add(String.format("%.1f", impactValue));
            }
            impactStrings.add(String.format("%d", (long) impactValue));
            Font font = new Font("Serif", Font.PLAIN, 2 * getRadius() / 3 < 8 ? 8 : 2 * getRadius() / 3);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            for (String impact : impactStrings) {
                Rectangle2D r = fm.getStringBounds(impact, g);
                if (r.getWidth() <= getRadius() * Math.sqrt(3) &&
                        r.getHeight() <= getRadius()) {
                    int startX = getX() - (int) r.getWidth() / 2;
                    int startY = getY() - (int) r.getHeight() / 2 + fm.getAscent();
                    g.drawString(impact, startX, startY);
                    break;
                }
            }
            impactStrings.clear();
            g.dispose();
        }
        return this;
    }

    public boolean isPrintImpact() {
        return printImpact;
    }

    public void setPrintImpact(boolean printImpact) {
        this.printImpact = printImpact;
    }

    public Point calculateSize(int rows, int columns) {
        Point size = calculatePositionOnScreen(rows, columns);
        size.x -= (getRadius() * Math.sqrt(3) / 2) - 1;
        size.y -= getRadius() / 2 - (getBorderWidth() + 3) / 4;
        return size;
    }

    private Point calculatePositionOnScreen(int row, int column) {
        double multiplier = Math.sqrt(3) / 2;
        double rd = (getRadius() * multiplier) + (getBorderWidth() + 1) / 2;
        int R = (int) (rd / multiplier);
        int r = (int) rd;
        int horizontalShift = ((r) * ((row % 2) + 1) + getBorderWidth() / 2);
        int horizontalStep = (2 * r);
        int verticalShift = R + (int) (getBorderWidth() / 2 / multiplier);
        int verticalStep = 3 * R / 2;
        return new Point(
                (horizontalShift + horizontalStep * column),
                (verticalShift + verticalStep * row)
        );
    }

    public Point calculatePositionOnField(Point clickPoint) {
        Point result = null;
        if (clickPoint.x >= 0 && clickPoint.y >= 0) {
            int borderWidth = getBorderWidth();
            int radius = getRadius();
            Hexagon hex = new Hexagon(0, 0, radius, borderWidth);
            double multiplier = Math.sqrt(3) / 2;
            double rd = (radius * multiplier) + (borderWidth + 1) / 2;
            int R = (int) (rd / multiplier);
            int r = (int) rd;
            int horizontalStep = (2 * r);
            int verticalShift = (int) (borderWidth / 2 / multiplier);
            int verticalStep = 3 * R / 2;
            int row = (clickPoint.y - verticalShift) / verticalStep;
            for (int i = 0; i >= -1; --i) {
                if (row + i >= 0) {
                    int horizontalShift = (r) * (((row + i) % 2)) + borderWidth / 2;
                    int column = (clickPoint.x - horizontalShift) / horizontalStep;
                    Point p = calculatePositionOnScreen(row + i, column);
                    hex.setX(p.x).setY(p.y);
                    if (hex.isContains(clickPoint)) {
                        p.setLocation(column, row + i);
                        result = p;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public boolean isFullColor() {
        return fullColor;
    }

    public FieldCell setFullColor(boolean fullColor) {
        this.fullColor = fullColor;
        return this;
    }
}
