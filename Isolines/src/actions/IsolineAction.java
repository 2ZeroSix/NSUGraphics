package actions;

import model.IsolineModel;

import java.util.Observable;
import java.util.Observer;

public class IsolineAction extends IconAction implements Observer {
    public IsolineAction(String name, IsolineModel model) {
        this(name, name, model);
    }

    public IsolineAction(String name, String description, IsolineModel model) {
        super(name, description);
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
