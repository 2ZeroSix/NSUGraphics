package life_hexagon.observables;

public interface LiveBoundsObservable {
    float getLiveBegin();
    float getLiveEnd();
    float getBirthBegin();
    float getBirthEnd();
    void notifyLifeBounds();
}
