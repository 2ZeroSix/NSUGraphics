package life_hexagon;

public interface Field {
    int getWidth(int row);
    int getHeight();
    boolean getState(int row, int column);
    float getImpact(int row, int column);
    float getFirstImpact();
    float getSecondImpact();
    float getLiveBegin();
    float getLiveEnd();
    float getBirthBegin();
    float getBirthEnd();
    void addObserver(FieldObserver observer);
    void removeObserver(FieldObserver observer);
    void notifyFull();
    void notifyField();
    void notifyState(int row, int column);
    void notifyImpact(int row, int column);
    void notifyImpact();
    void notifyLifeBounds();
}
