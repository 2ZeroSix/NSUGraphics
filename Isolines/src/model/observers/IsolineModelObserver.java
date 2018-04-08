package model.observers;

import model.Function2x2;
import model.IsolineModel;

public interface IsolineModelObserver {
    void updateFunction(IsolineModel isolineModel);
    void updateParameters(IsolineModel isolineModel);
    void update(IsolineModel isolineModel);
}
