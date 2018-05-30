package model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class IsolineModelImpl extends MutableIsolineModel {

    public IsolineModelImpl() {
        setFunction(getDefaultFunctionZ());
        List<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        setColors(colors);
        setPlot(true);
        setIsolines(true);
        setShowValue(true);
        setInterpolating(true);
        setGridSize(new Dimension(10, 10));
    }

    private static FunctionZ getDefaultFunctionZ() {
        return new FunctionZ() {
            {
                setBounds(new Rectangle.Double(0, 0, 4, 4));
            }
            @Override
            public double calc(double x, double y) {
                return x < 1.75 ? x + y : (y > 0.6 ? x + y - 1 : 4);
            }
        };
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        o.update(this, null);
    }
}
