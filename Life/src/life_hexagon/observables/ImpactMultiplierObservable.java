package life_hexagon.observables;

public interface ImpactMultiplierObservable {
    float getFirstImpact();
    float getSecondImpact();
    void notifyImpact();
}
