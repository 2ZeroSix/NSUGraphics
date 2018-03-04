package life_hexagon.model.observables;

public interface MutableDisplayModelObservable extends DisplayModelObservable {
    MutableDisplayModelObservable setBorderWidth(int width);
    MutableDisplayModelObservable setHexagonSize(int size);
    MutableDisplayModelObservable setDisplayImpact(boolean displayImpact);
}
