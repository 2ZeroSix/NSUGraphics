package model;

import java.awt.*;

public interface FunctionZ {
    Rectangle.Double getBounds();
    double calc(double x, double y);
    double getMax();
    double getMin();
}
