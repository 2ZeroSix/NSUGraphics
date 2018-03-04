package life_hexagon.view;

import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class FieldCell extends Hexagon implements DisplayModelObserver {
    static Color textColor = new Color(0x010101);
    static Color DEAD = (new Color(0xFFFFFF));
    static Color DEAD_NEXT_ALIVE = (new Color(0xFF8C14));
    static Color DEAD_NEARLY_ALIVE = (new Color(0xF3FF28));
    static Color ALIVE_NEXT_DEAD = (new Color(0x75FF78));
    static Color ALIVE_NEARLY_DEAD = (new Color(0x11FF27));
    static Color ALIVE = (new Color(0x008000));
    private boolean printImpact = false;

    public FieldCell() {
        super(0, 0, 0, 0);
    }


    private Color calculateFillColor(FieldObservable fieldFieldObservable, int x, int y) {
        boolean state = fieldFieldObservable.getState(x, y);
        float impact = fieldFieldObservable.getImpact(x, y);
        if (state) {
            return calculateFillColor(fieldFieldObservable, impact, ALIVE, ALIVE_NEARLY_DEAD, ALIVE_NEXT_DEAD);
        } else {
            return calculateFillColor(fieldFieldObservable, impact, DEAD_NEXT_ALIVE, DEAD_NEARLY_ALIVE, DEAD);
        }
    }

    private Color calculateFillColor(FieldObservable fieldFieldObservable, float impact, Color next, Color nearly, Color normal) {
        if (fieldFieldObservable.getBirthBegin() <= impact && impact <= fieldFieldObservable.getBirthEnd()) {
            return next;
        } else if (fieldFieldObservable.getLiveBegin() <= impact && impact <= fieldFieldObservable.getLiveEnd()) {
            return nearly;
        } else {
            return normal;
        }
    }

    public void draw(MyImage image, FieldObservable fieldFieldObservable, int row, int column) {
        setFillColor(calculateFillColor(fieldFieldObservable, row, column));
        Point pos = calculatePositionOnScreen(row, column);
        super.setX(pos.x).setY(pos.y).draw(image);
        if (isPrintImpact()) {
            {
                MyGraphics mg = image.getMyGraphics();
                int R = getRadius();
                int r = (int) (R * Math.sqrt(3) / 2);
                mg.fillRectangle(new Rectangle(getX() - r, getY() - R / 2, r*2, R), getFillColor());
            }
            Graphics2D g = image.createGraphics();
            g.setPaint(textColor);
            float impactValue = fieldFieldObservable.getImpact(row, column);
            String impact;
            if (impactValue == (long) impactValue) {
                impact = String.format("%d", (long) impactValue);
            } else {
                impact = String.format("%.2f", impactValue);
            }
            Font font = new Font("Serif", Font.PLAIN, 10);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(impact, g);
            if (r.getWidth() <= getRadius() * Math.cos(Math.PI / 3) * 2 &&
                    r.getHeight() <= getRadius() * Math.sin(Math.PI / 3) * 2) {
                int startX = getX() - (int) r.getWidth() / 2;
                int startY = getY() - (int) r.getHeight() / 2 + fm.getAscent();
                g.drawString(impact, startX, startY);
            } else {
                impact = String.format("%d", (long) impactValue);
                font = new Font("Serif", Font.PLAIN, 10);
                g.setFont(font);
                fm = g.getFontMetrics();
                r = fm.getStringBounds(impact, g);
                if (r.getWidth() <= getRadius() * Math.cos(Math.PI / 3) * 2 &&
                        r.getHeight() <= getRadius() * Math.sin(Math.PI / 3) * 2) {
                    int startX = getX() - (int) r.getWidth() / 2;
                    int startY = getY() - (int) r.getHeight() / 2 + fm.getAscent();
                    g.drawString(impact, startX, startY);
                }
            }
            g.dispose();
        }
    }

    @Override
    public void updateDisplayMode(DisplayModelObservable displayModel) {
        updateBorderWidth(displayModel);
        updateDisplayImpact(displayModel);
        updateHexagonSize(displayModel);
    }

    @Override
    public void updateBorderWidth(DisplayModelObservable displayModel) {
        setBorderWidth(displayModel.getBorderWidth());
    }

    @Override
    public void updateHexagonSize(DisplayModelObservable displayModel) {
        setRadius(displayModel.getHexagonSize());
    }

    @Override
    public void updateDisplayImpact(DisplayModelObservable displayModel) {
        setPrintImpact(displayModel.isDisplayImpact());
    }

    public boolean isPrintImpact() {
        return printImpact;
    }

    public void setPrintImpact(boolean printImpact) {
        this.printImpact = printImpact;
    }

    public Point calculateSize(int rows, int columns) {
        // TODO fix size, current bigger than needed
        Point size = calculatePositionOnScreen(rows, columns);
        size.x -= (getRadius() * Math.sqrt(3) / 2) - 1;
        size.y -= getRadius() / 2 - 1;
        return size;
    }

    private Point calculatePositionOnScreen(int row, int column) {
        double r = (getRadius() * Math.sqrt(3) / 2);
        int R = getRadius();
        // TODO add border to shift and step
        int horizontalShift = (int) (r * ((row % 2) + 1) + getBorderWidth());
        int horizontalStep = (int) (2 * r) + getBorderWidth();
        int verticalShift = R + getBorderWidth() / 2;
        int verticalStep = 3 * R / 2 + getBorderWidth() / 2;
        return new Point(
                (int) (horizontalShift + horizontalStep * column),
                (int) (verticalShift + verticalStep * row)
        );
    }

    public Point calculatePositionOnField(Point point) {
        Point result = null;
        Hexagon hex = new Hexagon(0, 0, getRadius(), getBorderWidth());
        if (point.x >= 0 && point.y >= 0) {
            double r = (getRadius() * Math.sqrt(3) / 2);
            int R = getRadius();
            int borderWidth = getBorderWidth();
            // TODO add border to shift and step
            int horizontalShift = (int) (r) + getBorderWidth();
            int horizontalStep = (int) (2 * r) + getBorderWidth();
            int verticalShift = R + getBorderWidth() / 2;
            int verticalStep = 3 * R / 2 + getBorderWidth() / 2;
            int row = point.y / verticalStep;
            int rowShift = point.y % verticalStep;

            if (rowShift < R / 2) {
                for (int i = -1; i <= 0; ++i) {
                    if (row + i >= 0) {
                        int nearestColumn = point.x - horizontalShift * ((row + i) % 2);
                        if (nearestColumn >= 0) {
                            int column = (nearestColumn) / horizontalStep;
                            Point p = calculatePositionOnScreen(row + i, column);
                            hex.setX(p.x).setY(p.y);
                            if (hex.isInside(point)) {
                                result = new Point(column, row + i);
                                break;
                            }
                        }
                    }
                }
            } else {
                int nearestColumn = point.x - horizontalShift * ((row) % 2);
                if (nearestColumn >= 0) {
                    int column = (nearestColumn) / horizontalStep;
                    result = new Point(column, row);
                }
            }
        }
        return result;
    }
}
