package life_hexagon;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Stack;

public class MyImage extends BufferedImage {

    public MyImage(int width, int height) {
        super(width, height, TYPE_INT_ARGB);
    }

    private void drawPixel(int x, int y, Color color) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
            setRGB(x, y, color.getRGB());
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
            int currentWidth = 0;
            if (dx >= dy) {
                int ty = y;
                while (++currentWidth < width) {
                    ty += currentWidth;
                    drawPixel(x, ty, color);
                    if (++currentWidth > width) break;
                    ty -= currentWidth;
                    drawPixel(x, ty, color);
                }
            } else {
                int tx = x;
                while (++currentWidth < width) {
                    tx += currentWidth;
                    drawPixel(tx, y, color);
                    if (++currentWidth > width) break;
                    tx -= currentWidth;
                    drawPixel(tx, y, color);
                }
            }
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
        Stack<Point> stack = new Stack();
        stack.push(new Point(x, y));
        int w = getWidth();
        int h = getHeight();
        int spanLeft = 0, spanRight = 0;

        int fill = color.getRGB();
        int bord = border.getRGB();

        while (!stack.empty()) {
            Point p = stack.pop();

            while (p.y >= 0 && getRGB(p.x, p.y) != bord && getRGB(p.x, p.y) != fill)
                --p.y;
            ++p.y;
            spanLeft = 0;
            spanRight = 0;
            while (p.y < h && getRGB(p.x, p.y) != bord && getRGB(p.x, p.y) != fill) {
                drawPixel(p.x, p.y, color);
                --p.x;
                if (spanLeft == 0 && p.x >= 0 && getRGB(p.x, p.y) != bord && getRGB(p.x, p.y) != fill) {
                    stack.push(new Point(p.x, p.y));
                    spanLeft = 1;
                } else if (p.x >= 0 && (getRGB(p.x, p.y) == bord || getRGB(p.x, p.y) == fill)) {
                    spanLeft = 0;
                }
                p.x += 2;
                if (spanRight == 0 && p.x < w && getRGB(p.x, p.y) != bord && getRGB(p.x, p.y) != fill) {
                    stack.push(new Point(p.x, p.y));
                    spanRight = 1;
                } else if (spanRight == 1 && p.x < w && (getRGB(p.x, p.y) == bord || getRGB(p.x, p.y) == fill)){
                    spanRight = 0;
                }
                --p.x;
                ++p.y;
            }
        }
    }
}
