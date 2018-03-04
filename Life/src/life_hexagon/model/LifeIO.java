package life_hexagon.model;

import life_hexagon.model.observables.DisplayModelObservable;
import life_hexagon.model.observables.FieldObservable;
import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.model.observables.MutableFieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;

import java.awt.Point;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LifeIO {
    public static class LifeIOException extends Exception {
        private LifeIOException(String msg) {
            super(msg);
        }

        public LifeIOException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private int cellSize;
    private int borderWidth;
    private List<Point> alive;
    private int width;
    private int height;

    public void parse(Scanner scanner) throws LifeIOException {
        try {
            if (!scanner.hasNextInt()) throw new LifeIOException("wrong format: max width expected");
            width = scanner.nextInt();
            if (!scanner.hasNextInt()) throw new LifeIOException("wrong format: height expected");
            height = scanner.nextInt();
            if (!scanner.hasNextInt()) throw new LifeIOException("wrong format: separator size expected");
            borderWidth = scanner.nextInt();
            if (!scanner.hasNextInt()) throw new LifeIOException("wrong format: cell size expected");
            cellSize = scanner.nextInt();
            if (!scanner.hasNextInt()) throw new LifeIOException("wrong format: count of points expected");
            int all = scanner.nextInt();
            alive = new ArrayList<>(all);
            for (int i = 0; i < all; ) {
                if (!scanner.hasNextInt()) throw new LifeIOException("wrong format: not enough points");
                int column = scanner.nextInt();
                if (!scanner.hasNextInt())
                    throw new LifeIOException("wrong format: second coordinate of point expected");
                int row = scanner.nextInt();
                alive.set(i, new Point(row, column));
            }
        } catch (Exception ex) {
            alive = null;
            throw ex;
        }
    }

    public void write(PrintStream stream, DisplayModelObservable display, FieldObservable field) {
        stream.println(field.getWidth(0) + " " + field.getHeight());
        stream.println(display.getBorderWidth());
        stream.println(display.getHexagonSize());
        int count = 0;
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getHeight(); ++column) {
                if (field.getState(row, column)) ++count;
            }
        }
        stream.println(count);
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getHeight(); ++column) {
                if (field.getState(row, column))
                    stream.println(column + " " + row);
            }
        }
    }

    public void setDisplay(MutableDisplayModelObservable displayModel) {
        displayModel.setBorderWidth(borderWidth);
        displayModel.setHexagonSize(cellSize);
    }

    public void setField(MutableFieldObservable field) {
        field.setSize(height, width);
        for (Point p : alive) {
            field.setState(p.x, p.y, true);
        }
    }
}
