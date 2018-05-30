package life_hexagon.model;

import life_hexagon.view.observers.FieldObserver;
import life_hexagon.model.observables.MutableFieldObservable;

import java.util.HashSet;
import java.util.Set;

public class GameModel implements MutableFieldObservable {

    private Set<FieldObserver> observers;

    private float impact[][];
    private boolean field[][];

    private float firstImpact;
    private float secondImpact;
    private float birthBegin;
    private float birthEnd;
    private float liveBegin;
    private float liveEnd;

    @Override
    public boolean getState(int row, int column) {
        return field[row][column];
    }

    @Override
    public void setSize(int rows, int columns) {
        float[][] impact = new float[rows][];
        boolean[][] field = new boolean[rows][];
        for (int row = 0; row < rows; ++row) {
            impact[row] = new float[columns - row % 2];
            field[row] = new boolean[columns - row % 2];
        }
        for (int row = 0; row < Integer.min(rows, getHeight()); ++row) {
            System.arraycopy(this.impact[row], 0, impact[row], 0, Integer.min(columns - row % 2, getWidth(row)));
            System.arraycopy(this.field[row], 0, field[row], 0, Integer.min(columns - row % 2, getWidth(row)));
        }
        int oldHeight = getHeight();
        int oldWidth = getWidth(0);
        this.impact = impact;
        this.field = field;
        notifySize();
        for (int row = Integer.max(0, Integer.min(rows, oldHeight) - 2);
             row < Integer.max(0, Integer.min(rows, oldHeight + 3));
             ++row) {
            for (int column = 0; column < columns - row % 2; ++column) {
                recalculateImpact(row, column);
            }
        }
        for (int row = 0; row < rows; ++row) {
            for (int column = Integer.max(0, Integer.min(oldWidth, columns) - row % 2 - 2);
                 column < Integer.max(0, Integer.min(oldWidth + 3, columns) - row % 2);
                 ++column) {
                recalculateImpact(row, column);
            }
        }

    }

    @Override
    public void setState(int row, int column, boolean state) {
        if (state != field[row][column]) {
            field[row][column] = state;
            recalculateImpact(row, column, state);
            notifyState(row, column);
        }
    }

    private void setStateWithoutImpact(int row, int column, boolean state) {
        if (state != field[row][column]) {
            field[row][column] = state;
            notifyState(row, column);
        }
    }


    @Override
    public float getImpact(int row, int column) {
        return impact[row][column];
    }

    private void setImpact(int row, int column, float impact) {
        if (this.impact[row][column] != impact) {
            this.impact[row][column] = impact;
            notifyImpact(row, column);
        }
    }

    @Override
    public float getFirstImpact() {
        return firstImpact;
    }

    @Override
    public void setFirstImpact(float firstImpact) throws IllegalArgumentException {
        if (firstImpact < 0) throw new IllegalArgumentException("first impact must be >= 0");
        if (this.firstImpact != firstImpact) {
            this.firstImpact = firstImpact;
            recalculateImpact();
            notifyImpact();
        }
    }

    @Override
    public float getSecondImpact() {
        return secondImpact;
    }

    @Override
    public void setSecondImpact(float secondImpact) throws IllegalArgumentException {
        if (secondImpact < 0) throw new IllegalArgumentException("second impact must be >= 0");
        if (this.secondImpact != secondImpact) {
            this.secondImpact = secondImpact;
            recalculateImpact();
            notifyImpact();
        }
    }

    @Override
    public float getLiveBegin() {
        return liveBegin;
    }


    @Override
    public float getLiveEnd() {
        return liveEnd;
    }

    @Override
    public float getBirthBegin() {
        return birthBegin;
    }

    @Override
    public float getBirthEnd() {
        return birthEnd;
    }

    @Override
    public void setLiveBounds(float liveBegin, float birthBegin, float birthEnd, float liveEnd) throws IllegalArgumentException {
        if ((   this.liveBegin != liveBegin ||
                this.liveEnd != liveEnd ||
                this.birthBegin != birthBegin ||
                this.birthEnd != birthEnd)) {
            if (0 <= liveBegin &&
                    liveBegin <= birthBegin &&
                    birthBegin <= birthEnd &&
                    birthEnd <= liveEnd) {
                this.liveBegin = liveBegin;
                this.birthBegin = birthBegin;
                this.birthEnd = birthEnd;
                this.liveEnd = liveEnd;
                notifyLifeBounds();
            } else {
                throw new IllegalArgumentException("assert: (0 <= liveBegin <= birthBegin <= birthEnd <= liveEnd)");
            }
        }
    }

    @Override
    public int getWidth(int row) {
        return field.length == 0 ? 0 : Integer.max(field[0].length - row % 2, 0);
    }

    @Override
    public int getHeight() {
        return field.length;
    }



    private void init(float[][] impact, boolean[][] field, float firstImpact, float secondImpact, float birthBegin, float birthEnd, float liveBegin, float liveEnd) {
        this.impact = impact;
        this.field = field;
        this.firstImpact = firstImpact;
        this.secondImpact = secondImpact;
        this.birthBegin = birthBegin;
        this.birthEnd = birthEnd;
        this.liveBegin = liveBegin;
        this.liveEnd = liveEnd;
        this.observers = new HashSet<>();
        recalculateImpact();
    }

    public GameModel(int width, int height, float firstImpact, float secondImpact, float birthBegin, float birthEnd, float liveBegin, float liveEnd) {
        float[][] impact = new float[height][];
        boolean[][] field = new boolean[height][];
        for (int row = 0; row < height; ++row) {
            impact[row] = new float[width - row % 2];
            field[row] = new boolean[width - row % 2];
        }
        init(impact, field, firstImpact, secondImpact, birthBegin, birthEnd, liveBegin, liveEnd);
    }

    public GameModel(boolean[][] field, float firstImpact, float secondImpact, float birthBegin, float birthEnd, float liveBegin, float liveEnd) {
        int height = field.length;
        int width = field[0].length;
        float[][] impact = new float[height][];
        for (int row = 0; row < height; ++row) {
            impact[row] = new float[width - row % 2];
        }
        init(impact, field, firstImpact, secondImpact, birthBegin, birthEnd, liveBegin, liveEnd);
    }


    public void step() {
        recalculateAlive();
        recalculateImpact();
    }

    private int calculateFirstNeighbors(int row, int column) {
        int height = field.length;
        boolean shift = row % 2 == 1;
        int count = 0;
        int minRow = Integer.max(row - 1, 0);
        int maxRow = Integer.min(row + 1, height - 1);

        for (int r = minRow; r <= maxRow; ++r) {
            boolean subShift = r != row;
            int minColumn = Integer.max(column - 1 + (subShift ? (shift ? 1 : 0): 0), 0);
            int maxColumn = Integer.min(column + 1 + (subShift ? (shift ? 0 : -1): 0), getWidth(r) - 1);
            for (int c = minColumn; c <= maxColumn; ++c) {
                if (r == row && c == column) continue;
                if (field[r][c]) ++count;
            }
        }
//        int shift = row % 2;
//        int count = 0;
//
//        int drow = row - 1;
//        int urow = row + 1;
//        int lcolumn = column - 2 + shift;
//        int rcolumn = column + 1 + shift;
//
//        if (drow >= 0) {
//            if (lcolumn >= 0                && field[drow][lcolumn])    ++count;
//            if (rcolumn < getWidth(drow)    && field[drow][rcolumn])    ++count;
//            if (ddrow   >= 0                && field[ddrow][column])    ++count;
//        }
//        if (urow < getHeight()) {
//            if (lcolumn >= 0                && field[urow][lcolumn])    ++count;
//            if (rcolumn < getWidth(urow)    && field[urow][rcolumn])    ++count;
//            if (uurow   < getHeight()       && field[uurow][column])    ++count;
//        }

        return count;


    }

    private int calculateSecondNeighbors(int row, int column) {
        int shift = row % 2;
        int count = 0;

        int drow = row - 1;
        int ddrow = row - 2;
        int urow = row + 1;
        int uurow = row + 2;
        int lcolumn = column - 2 + shift;
        int rcolumn = column + 1 + shift;

        if (drow >= 0) {
            if (lcolumn >= 0                && field[drow][lcolumn])    ++count;
            if (rcolumn < getWidth(drow)    && field[drow][rcolumn])    ++count;
            if (ddrow   >= 0                && field[ddrow][column])    ++count;
        }
        if (urow < getHeight()) {
            if (lcolumn >= 0                && field[urow][lcolumn])    ++count;
            if (rcolumn < getWidth(urow)    && field[urow][rcolumn])    ++count;
            if (uurow   < getHeight()       && field[uurow][column])    ++count;
        }

        return count;
    }

    private void recalculateImpact(int row, int column) {
        int firstNeighbors = calculateFirstNeighbors(row, column);
        int secondNeighbors = calculateSecondNeighbors(row, column);
        setImpact(row, column, firstImpact * firstNeighbors + secondImpact * secondNeighbors);
    }

    private void recalculateImpact(int row, int column, boolean state) {
        // TODO optimize
        for (int r = Integer.max(row - 2, 0); r < Integer.min(row + 3, getHeight()); ++r) {
            for (int c = Integer.max(column - 2, 0); c < Integer.min(column + 3, getWidth(r)); ++c) {
                recalculateImpact(r, c);
            }
        }
    }

    private void recalculateImpact() {
        for (int row = 0; row < getHeight(); ++row) {
            for (int column = 0; column < getWidth(row); ++column) {
                recalculateImpact(row, column);
            }
        }
    }

    private void recalculateAlive() {
        for (int row = 0; row < getHeight(); ++row) {
            for (int column = 0; column < getWidth(row); ++column) {
                if (field[row][column]) {
                    setStateWithoutImpact(row, column, liveBegin <= impact[row][column] && impact[row][column] <= liveEnd);
                } else {
                    setStateWithoutImpact(row, column, birthBegin <= impact[row][column] && impact[row][column] <= birthEnd);
                }
            }
        }
    }

    @Override
    public void addObserver(FieldObserver observer) {
        observers.add(observer);
        observer.updateField(this);
    }

    @Override
    public void removeObserver(FieldObserver observer) {
        observers.remove(observer);
    }


    @Override
    public void notifyField() {
        for (FieldObserver observer : observers) {
            observer.updateField(this);
        }
    }

    @Override
    public void notifyState(int row, int column) {
        for (FieldObserver observer : observers) {
            observer.updateState(this, row, column);
        }
    }

    @Override
    public void notifyImpact(int row, int column) {
        for (FieldObserver observer : observers) {
            observer.updateImpact(this, row, column);
        }
    }

    @Override
    public void notifySize() {
        for (FieldObserver observer : observers) {
            observer.updateSize(this);
        }
    }

    @Override
    public void notifyLifeBounds() {
        for (FieldObserver observer : observers) {
            observer.updateLifeBounds(this);
        }
    }

    @Override
    public void notifyImpact() {
        for (FieldObserver observer : observers) {
            observer.updateImpact(this);
        }
    }
}
