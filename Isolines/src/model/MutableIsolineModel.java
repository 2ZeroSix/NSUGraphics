package model;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class MutableIsolineModel extends IsolineModel {

    public synchronized void setInterpolating(boolean interpolating) {
        if (this.interpolating != interpolating) {
            this.interpolating = interpolating;
            setChanged();
            notifyObservers(INTERPOLATING);
        }
    }

    public synchronized  void setClicking(boolean clicking) {
        if (this.clicking != clicking) {
            this.clicking = clicking;
            setChanged();
            notifyObservers(CLICKING);
        }
    }

    public synchronized void setDynamic(boolean dynamic) {
        if (this.dynamic != dynamic) {
            this.dynamic = dynamic;
            setChanged();
            if (!dynamic) {
                dynamicIsoline = null;
            }
            notifyObservers(DYNAMIC);
        }
    }

    public synchronized void setPlot(boolean plot) {
        if (this.plot != plot) {
            this.plot = plot;
            setChanged();
            notifyObservers(PLOT);
        }
    }

    public synchronized void setIsolines(boolean isolines) {
        if (this.isolines != isolines) {
            this.isolines = isolines;
            setChanged();
            notifyObservers(ISOLINES);
        }
    }

    public synchronized void setGrid(boolean grid) {
        if (this.grid != grid) {
            this.grid = grid;
            setChanged();
            notifyObservers(GRID);
        }
    }

    public synchronized void setGridSize(Dimension gridSize) {
        if (!Objects.equals(this.gridSize, gridSize)) {
            this.gridSize = gridSize;
            function.setGrid(gridSize);
            setChanged();
            notifyObservers(GRID_SIZE);
        }
    }

    public synchronized void setGridDots(boolean gridDots) {
        if (!Objects.equals(gridDots, this.gridDots)) {
            this.gridDots = gridDots;
            setChanged();
            notifyObservers(GRID_DOTS);
        }
    }

    public synchronized void setShowValue(boolean showValue) {
        if (!Objects.equals(this.showValue, showValue)) {
            this.showValue = showValue;
            setChanged();
            notifyObservers(SHOW_VALUE);
        }
    }

    public synchronized void     setColors(List<Color> colors) {
        if (!Objects.equals(this.colors, colors)) {
            this.colors = colors;
            setChanged();
            notifyObservers(COLORS);
        }
    }

    public synchronized void setIsolineColor(Color color) {
        if (!Objects.equals(this.isolineColor, color)) {
            this.isolineColor = color;
            setChanged();
            notifyObservers(ISOLINE_COLOR);
        }
    }

    public synchronized void setFunction(FunctionZ function) {
        if (!Objects.equals(this.function, function)) {
            Optional.ofNullable(this.function).ifPresent(Observable::deleteObservers);
            this.function = function;
            this.function.addObserver((func, obj) -> {
                setChanged();
                notifyObservers(FUNCTION);
            });
            setChanged();
            notifyObservers(FUNCTION);
        }
    }

    public synchronized void addUserIsoline(double level) {
        if (!userIsolines.contains(level)) {
            userIsolines.add(level);
            setChanged();
            notifyObservers(USER_ISOLINES);
        }
    }

    public synchronized void cleanUserIsolines() {
        if (!userIsolines.isEmpty()) {
            userIsolines.clear();
            setChanged();
            notifyObservers(USER_ISOLINES);
        }
    }

    public synchronized void setDynamicIsolineLevel(double level) {
        if (!Objects.equals(this.dynamicIsoline, level)) {
            this.dynamicIsoline = level;
            setChanged();
            notifyObservers(DYNAMIC_ISOLINE_LEVEL);
        }
    }


}
