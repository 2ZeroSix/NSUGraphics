package life_hexagon;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FieldCell extends Hexagon{
    static Color textColor = Color.BLACK;
    static Color DEAD               = (new Color(0xFFFFFF));
    static Color DEAD_NEXT_ALIVE    = (new Color(0xFF8C14));
    static Color DEAD_NEARLY_ALIVE  = (new Color(0xF3FF28));
    static Color ALIVE_NEXT_DEAD    = (new Color(0x75FF78));
    static Color ALIVE_NEARLY_DEAD  = (new Color(0x11FF27));
    static Color ALIVE              = (new Color(0x008000));

    public FieldCell(int radius, int borderWidth) {
        super(0, 0, radius, borderWidth);
    }

    public Point calculateSize(int rows, int columns) {
        // TODO fix
        return calculatePosition(rows, columns);
    }

    private Point calculatePosition(int row, int column) {
        double width = getRadius()*Math.cos(Math.PI/3);
        double height = getRadius();
        double verticalShift    = getBorderWidth() / 2 + height * (1 - 2 * ((row + 1) % 2));
        double verticalStep     = 2 * height;
        double horizontalShift  = getBorderWidth() / 2 + width * (2 * (row % 2) + 1);
        double horizontalStep   = 2 * width;
        return new Point(
                (int)(horizontalShift + horizontalStep * row),
                (int)(verticalShift + verticalStep * column)
        );
    }

    private Color calculateFillColor(Field field, int x, int y) {
        boolean state = field.getState(x, y);
        float impact = field.getImpact(x, y);
        if (state) {
            return calculateFillColor(field, impact, ALIVE, ALIVE_NEARLY_DEAD, ALIVE_NEXT_DEAD);
        } else {
            return calculateFillColor(field, impact, DEAD_NEXT_ALIVE, DEAD_NEARLY_ALIVE, DEAD);
        }
    }

    private Color calculateFillColor(Field field, float impact, Color next, Color nearly, Color normal) {
        if (field.getBirthBegin() <= impact && impact <= field.getBirthEnd()) {
            return next;
        } else if (field.getLiveBegin() <= impact && impact <= field.getLiveEnd()) {
            return nearly;
        } else {
            return normal;
        }
    }

    public void draw(MyImage image, Field field, int row, int column) {
        setFillColor(calculateFillColor(field, row, column));
        Point pos = calculatePosition(row, column);
        setX(pos.x).setY(pos.y);
        super.draw(image);
        Graphics2D g = image.createGraphics();
        g.setPaint(textColor);
        float impactValue = field.getImpact(row, column);
        String impact;
        if (impactValue == (long)impactValue) {
            impact = String.format("%d", (long)impactValue);
        } else {
            impact = String.format("%.2f", impactValue);
        }
        Font font = new Font("Serif", Font.PLAIN, 10);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(impact, g);
        if (    r.getWidth() <= getRadius() * Math.cos(Math.PI / 3) * 2 &&
                r.getHeight() <= getRadius() * Math.sin(Math.PI / 3) * 2) {
            int startX = getX() - (int) r.getWidth() / 2;
            int startY = getY() - (int) r.getHeight() / 2 + fm.getAscent();
            g.drawString(impact, startX, startY);
        }
        g.dispose();
    }
}
