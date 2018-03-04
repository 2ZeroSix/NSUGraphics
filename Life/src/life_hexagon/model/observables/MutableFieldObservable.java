package life_hexagon.model.observables;

public interface MutableFieldObservable
        extends FieldObservable {
    void setSize(int rows, int columns);
    void setState(int row, int column, boolean state);
    void setLifeBounds(float liveBegin, float birthBegin, float birthEnd, float liveEnd);
    void setFirstImpact(float firstImpact);
    void setSecondImpact(float secondImpact);
    void step();
}
