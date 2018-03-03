package life_hexagon.model;

import life_hexagon.observers.FieldObserver;
import life_hexagon.observables.MutableFieldObservable;

import java.util.HashSet;
import java.util.Set;

public class Model implements MutableFieldObservable {

    private Set<FieldObserver> observers;

    private static int defaultWidth = 10;
    private static int defaultHeight = 10;

    private float impact[][];
    private boolean field[][];

    private static float defaultFirstImpact = 1f;
    private float firstImpact;
    private static float defaultSecondImpact = .3f;
    private float secondImpact;
    private static float defaultBirthBegin = 2.3f;
    private float birthBegin;
    private static float defaultBirthEnd = 2.9f;
    private float birthEnd;
    private static float defaultLiveBegin = 2f;
    private float liveBegin;
    private static float defaultLiveEnd = 3.3f;
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
        for (int row = 0; row < rows; ++row) {
            System.arraycopy(this.impact[row], 0, impact[row], 0, Integer.min(0, getWidth(row)));
            System.arraycopy(this.field[row], 0, field[row], 0, Integer.min(0, getWidth(row)));
        }
        int oldHeight = getHeight();
        int oldWidth = getWidth(0);
        this.impact = impact;
        this.field = field;
        notifyField();
        for (int row = Integer.max(0, Integer.min(getHeight(), oldHeight) - 2);
             row < Integer.max(0, Integer.min(getHeight() + 2, oldHeight));
             ++row) {
            for (int column = Integer.max(0, Integer.min(oldWidth - row % 2, getWidth(row)) - 2);
                 column < Integer.max(0, Integer.min(oldWidth - row % 2, getWidth(row)));
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
        this.firstImpact = firstImpact;
        notifyImpact();
    }

    @Override
    public float getSecondImpact() {
        return secondImpact;
    }

    @Override
    public void setSecondImpact(float secondImpact) throws IllegalArgumentException {
        if (secondImpact < 0) throw new IllegalArgumentException("second impact must be >= 0");
        this.secondImpact = secondImpact;
        notifyImpact();
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
    public void setLifeBounds(float liveBegin, float birthBegin, float birthEnd, float liveEnd) throws IllegalArgumentException {
        if (0 <= liveBegin &&
                liveBegin <= birthBegin &&
                birthBegin <= birthEnd &&
                birthEnd <= liveEnd) {
            this.liveBegin = liveBegin;
            this.birthBegin = birthBegin;
            this.birthEnd = birthEnd;
            this.liveEnd = liveEnd;
        } else {
            throw new IllegalArgumentException("assert: (0 <= liveBegin <= birthBegin <= birthEnd <= liveEnd)");
        }
        notifyLifeBounds();
    }

    @Override
    public int getWidth(int row) {
        return Integer.max(field.length - row % 2, 0);
    }

    @Override
    public int getHeight() {
        return field[0].length;
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

    public Model() {
        float[][] impact = new float[defaultHeight][];
        boolean[][] field = new boolean[defaultHeight][];
        for (int row = 0; row < defaultHeight; ++row) {
            impact[row] = new float[defaultWidth - row % 2];
            field[row] = new boolean[defaultWidth - row % 2];
        }
        init(impact, field, defaultFirstImpact, defaultSecondImpact,
                defaultBirthBegin, defaultBirthEnd, defaultLiveBegin, defaultLiveEnd);
    }

    public Model(int width, int height, float firstImpact, float secondImpact, float birthBegin, float birthEnd, float liveBegin, float liveEnd) {
        float[][] impact = new float[height][];
        boolean[][] field = new boolean[height][];
        for (int row = 0; row < height; ++row) {
            impact[row] = new float[width - row % 2];
            field[row] = new boolean[width - row % 2];
        }
        init(impact, field, firstImpact, secondImpact, birthBegin, birthEnd, liveBegin, liveEnd);
    }

    public Model(boolean[][] field, float firstImpact, float secondImpact, float birthBegin, float birthEnd, float liveBegin, float liveEnd) {
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
        int width = field[0].length;
        int shift = row % 2;
        int count = 0;
        int minRow = Integer.max(row - 1, 0);
        int maxRow = Integer.min(row + 1, height);

        for (int r = minRow; r < maxRow; ++r) {
            int subShift = r != row ? 1 : 0;
            int minColumn = Integer.max(column - 1 + subShift * shift, 0);
            int maxColumn = Integer.min(column - subShift * shift, width - (shift == 1 ? 1 - subShift : subShift));
            for (int c = minColumn; c < maxColumn; ++c) {
                if (r == row && c == column) ++c;
                if (field[r][c]) ++count;
            }
        }

        return count;
    }

    private int calculateSecondNeighbors(int row, int column) {
        int shift = row % 2;
        int count = 0;

        if (row - 1 >= 0) {
            if (column - 2 + shift >= 0 && field[row - 1][column - 2 + shift]) ++count;
            if (column + 1 + shift < getWidth(row - 1) && field[row - 1][column + 1 + shift]) ++count;
            if (row - 2 >= 0 && field[row - 2][column]) ++count;
        }
        if (row + 1 < getHeight()) {
            if (column - 2 + shift >= 0 && field[row + 1][column - 2 + shift]) ++count;
            if (column + 1 + shift < getWidth(row + 1) && field[row + 1][column + 1 + shift]) ++count;
            if (row + 2 < getHeight() && field[row + 2][column]) ++count;
        }
        return count;
    }

    private void recalculateImpact(int row, int column) {
        int firstNeighbors = calculateFirstNeighbors(row, column);
        int secondNeighbors = calculateSecondNeighbors(row, column);
        setImpact(row, column, firstImpact * firstNeighbors + secondImpact * secondNeighbors);
    }

    private void recalculateImpact(int row, int column, boolean state) {
        for (int x = Integer.max(row, 0); x < Integer.min(row, getHeight()); ++x) {
            for (int y = Integer.max(column, 0); x < Integer.min(column, getWidth(x)); ++y) {
                // TODO optimize
                recalculateImpact(row, column);
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
                    setState(row, column, liveBegin <= impact[row][column] && impact[row][column] <= liveEnd);
                } else {
                    setState(row, column, birthBegin <= impact[row][column] && impact[row][column] <= birthEnd);
                }
            }
        }
    }

    @Override
    public void addObserver(FieldObserver observer) {
        observers.add(observer);
        observer.updateFull(this);
    }

    @Override
    public void removeObserver(FieldObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyFull() {
        for (FieldObserver observer : observers) {
            observer.updateField(this);
        }
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
    public void notifyImpact() {
        for (FieldObserver observer : observers) {
            observer.updateImpact(this);
        }
    }

    @Override
    public void notifyLifeBounds() {
        for (FieldObserver observer : observers) {
            observer.updateLifeBounds(this);
        }
    }
}
