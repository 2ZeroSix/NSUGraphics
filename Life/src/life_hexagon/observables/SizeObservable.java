package life_hexagon.observables;

import java.awt.*;

public interface SizeObservable{
    int getWidth(int row);
    int getHeight();
    void notifySize();
}
