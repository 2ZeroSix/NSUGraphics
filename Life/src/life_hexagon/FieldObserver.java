package life_hexagon;

public interface FieldObserver {
    void updateFull(Field field);
    void updateField(Field field);
    void updateState(Field field, int row, int column);
    void updateImpact(Field field, int row, int column);
    void updateImpact(Field field);
    void updateLifeBounds(Field field);
}
