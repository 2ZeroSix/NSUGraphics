package model;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class IsolineModel extends Observable {
    public static final String FUNCTION = "Function";
    protected FunctionZ function;
    public static final String INTERPOLATING = "Interpolating";
    protected boolean interpolating;
    public static final String DYNAMIC = "Dynamic";
    protected boolean dynamic;
    public static final String CLICKING = "Clicking";
    protected boolean clicking;
    public static final String PLOT = "Plot";
    protected boolean plot;
    public static final String ISOLINES = "Isolines";
    protected boolean isolines;
    public static final String GRID = "Grid";
    protected boolean grid;
    public static final String GRID_DOTS = "Grid dots";
    protected boolean gridDots;
    public static final String SHOW_VALUE = "Show value";
    protected boolean showValue;
    public static final String COLORS = "colors";
    protected List<Color> colors = new ArrayList<>();
    public static final String ISOLINE_COLOR = "Isoline color";
    protected Color isolineColor = Color.BLACK;
    public static final String GRID_SIZE = "Grid size";
    protected Dimension gridSize = new Dimension(10, 10);
    public static final String USER_ISOLINES = "User isolines";
    protected Set<Double> userIsolines = new HashSet<>();
    public static final String DYNAMIC_ISOLINE_LEVEL = "Dynamic isoline level";
    protected Double dynamicIsoline;

    public boolean isInterpolating() {
        return interpolating;
    }

    public boolean isClicking() {
        return clicking;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public boolean isPlot() {
        return plot;
    }

    public boolean isIsolines() {
        return isolines;
    }

    public boolean isGrid() {
        return grid;
    }

    public Dimension getGridSize() {
        return gridSize;
    }

    public boolean isGridDots() {
        return gridDots;
    }

    public boolean isShowValue() {
        return showValue;
    }

    public List<Color> getColors() {
        return colors;
    }

    public Color getIsolineColor() {
        return isolineColor;
    }

    public FunctionZ getFunction() {
        return function;
    }

    public int getKeyValues() {
        return Integer.max(colors.size() - 1, 0);
    }

    public Set<Double> getUserIsolines() {
        return userIsolines;
    }

    public Optional<Double> getDynamicIsoline() {return Optional.ofNullable(dynamicIsoline);}

}
