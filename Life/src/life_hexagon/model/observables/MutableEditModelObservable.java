package life_hexagon.model.observables;

import javax.swing.*;

public interface MutableEditModelObservable extends EditModelObservable {
    void setXOR(boolean on);
    void setLoop(boolean on);
    void setLoopTimer(Timer timer);
//    void setFill(boolean alive);
}
