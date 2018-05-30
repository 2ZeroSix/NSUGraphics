package life_hexagon.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Stack;
import java.util.function.Predicate;

public class MyGraphics {
    private MyImage image;

    public MyGraphics(MyImage image) {
        this.image = image;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }

    public void drawPixelUnsafe(int x, int y, Color color) {
        image.setRGB(x, y, color.getRGB());
    }

    public void drawPixel(int x, int y, Color color) {
        if (isValid(x, y))
            drawPixelUnsafe(x, y, color);
    }

    public void drawLine(int x0, int y0, int x1, int y1, Color color) {
        int dx = Math.abs(x1 - x0);
        dx = dx == 0 ? 1 : dx;
        int dy = Math.abs(y1 - y0);
        int error = 0;
        int dError = dy;
        int y = y0;
        int diry = Integer.signum(y1 - y0);
        int dirx = x1 - x0 >= 0 ? 1 : -1;
        for (int x = x0; x != x1 + dirx; x += dirx) {
            drawPixel(x, y, color);
            if (2 * error > dx) {
                y += diry;
                error -= dx;
            } else {
                error += dError;
            }
            if (2 * error > dx) {
                x -= dirx;
            }
        }
    }

    public void drawLine(int x0, int y0, int x1, int y1, Color color, int width) {
        if (width == 1) {
            drawLine(x0, y0, x1, y1, color);
        } else {
            Graphics2D g = image.createGraphics();
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
            g.drawLine(x0, y0, x1, y1);
            g.dispose();
        }
    }

    public void drawPolyline(int[] x, int[] y, int count, Color color) {
        for (int i = 0; i < count - 1; ++i) {
            drawLine(x[i], y[i], x[(i + 1) % count], y[(i + 1) % count], color);
        }
    }

    public void drawPolygon(int[] x, int[] y, int count, Color color) {
        for (int i = 0; i < count; ++i) {
            drawLine(x[i], y[i], x[(i + 1) % count], y[(i + 1) % count], color);
        }
    }

    public void drawPolygon(int[] x, int[] y, int count, Color color, int width) {
        for (int i = 0; i < count; ++i) {
            drawLine(x[i], y[i], x[(i + 1) % count], y[(i + 1) % count], color, width);
        }
    }

    public void spanFill(int x, int y, Color color, Color border) {
        Stack<Point> stack = new Stack<>();
        if (isValid(x, y))
            stack.push(new Point(x, y));
        int w = image.getWidth();
        int h = image.getHeight();
        boolean spanLeft, spanRight;

        int fill = color.getRGB();
        int bord = border.getRGB();

        Predicate<Point> isOldColor = (p) -> image.getRGB(p.x, p.y) != bord && image.getRGB(p.x, p.y) != fill;
        while (!stack.empty()) {
            Point p = stack.pop();

            while (p.y >= 0 && isOldColor.test(p)) --p.y;
            ++p.y;

            spanLeft = false;
            spanRight = false;
            while (p.y < h && isOldColor.test(p)) {
                drawPixelUnsafe(p.x, p.y, color);

                --p.x;
                if (!spanLeft && p.x >= 0 && isOldColor.test(p)) {
                    stack.push(new Point(p.x, p.y));
                    spanLeft = true;
                } else if (spanLeft && p.x >= 0 && !isOldColor.test(p)) {
                    spanLeft = false;
                }

                p.x += 2;
                if (!spanRight && p.x < w && isOldColor.test(p)) {
                    stack.push(new Point(p.x, p.y));
                    spanRight = true;
                } else if (spanRight && p.x < w && !isOldColor.test(p)){
                    spanRight = false;
                }

                --p.x;
                ++p.y;
            }
        }
    }

    public void fillRectangle(Rectangle2D r, Color fillColor) {
        for (int x = Integer.max((int) r.getMinX(), 0); x < Integer.min((int) r.getMaxX(), image.getWidth()); ++x) {
            for (int y = Integer.max((int) r.getMinY(), 0); y < Integer.min((int) r.getMaxY(), image.getWidth()); ++y) {
                drawPixel(x, y, fillColor);
            }
        }
    }
}
