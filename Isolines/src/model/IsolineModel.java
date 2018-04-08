package model;

import model.observers.IsolineModelObserver;

import java.awt.*;
import java.util.List;

public interface IsolineModel {
    Parameters getParameters();
    Function2x2 getFunction();

    class Parameters {
        private int keyValuesCount;
        private List<Color> colors;
    }

    void addObserver(IsolineModelObserver observer);
    void removeObserver(IsolineModelObserver observer);
    void notifyFunction(IsolineModel isolineModel);
    void notifyParameters(IsolineModel isolineModel);
}
