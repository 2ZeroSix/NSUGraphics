package model;

import java.awt.*;
import java.util.List;

public interface MutableIsolineModel extends IsolineModel {
    void setInterpolating(boolean interpolating);
    void setClicking(boolean clicking);
    void setPlot(boolean plot);
    void setIsolines(boolean isolines);
    void setGrid(boolean grid);
    void setGridSize(Dimension gridSize);
    void setGridDots(boolean gridDots);
    void setShowValue(boolean showValue);
    void setColors(List<Color> colors);
    void setIsolineColor(Color color);
    void setFunction(FunctionZ function);
    void setKeyValuesCount(int count);
    void setCursor(Point.Double point);
    void addUserIsoline(double level);
    void cleanUserIsolines();

}
