package actions;

import model.IsolineModel;
import model.observers.IsolineModelObserver;

public class IsolineAction extends IconAction implements IsolineModelObserver {
    public IsolineAction(String name, IsolineModel model) {
        this(name, name, model);
    }

    public IsolineAction(String name, String description, IsolineModel model) {
        super(name, description);
        model.addObserver(this);
    }

    @Override
    public void update(IsolineModel isolineModel) {

    }
}
