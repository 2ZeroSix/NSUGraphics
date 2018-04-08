package model;

import java.awt.*;

public interface Function2x2 {
    Rectangle.Double getBounds();
    double calcX(double x, double y);
    double calcY(double x, double y);
}
