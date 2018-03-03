package life_hexagon.observables;

public interface MutableFieldObservable extends FieldObservable {
    void setSize(int rows, int columns);
    void setState(int row, int column, boolean state);
    void setFirstImpact(float firstImpact);
    void setSecondImpact(float secondImpact);
    void setLifeBounds(float liveBegin, float birthBegin, float birthEnd, float liveEnd);
}
