package life_hexagon.observables;

public interface FieldObservable
        extends ImpactMultiplierObservable,
                LiveBoundsObservable,
                SizeObservable
                {
    boolean getState(int row, int column);
    float getImpact(int row, int column);
    void notifyField();
    void notifyState(int row, int column);
    void notifyImpact(int row, int column);
}
