package life_hexagon.model.observables;

import life_hexagon.view.observers.FieldObserver;

public interface FieldObservable {
    void notifyField();


    boolean getState(int row, int column);

    void notifyState(int row, int column);


    float getImpact(int row, int column);

    void notifyImpact(int row, int column);


    int getWidth(int row);

    int getHeight();

    void notifySize();


    float getLiveBegin();

    float getLiveEnd();

    float getBirthBegin();

    float getBirthEnd();

    void notifyLifeBounds();


    float getFirstImpact();

    float getSecondImpact();

    void notifyImpact();


    void addObserver(FieldObserver observer);

    void removeObserver(FieldObserver observer);
}
