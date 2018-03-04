package life_hexagon.controller;

import life_hexagon.model.LifeIO;
import life_hexagon.model.ModelFactory;
import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.model.observables.MutableFieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Controller {
    private MutableFieldObservable field;
    private MutableDisplayModelObservable display;
    private Timer timer;
    private boolean xorMode = true;
    private ModelFactory modelFactory;
    private Point lastTouch = null;

    public Controller(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
        this.timer = new Timer(1000, e -> field.step());
        this.display = modelFactory.createDisplayModel();
        this.field = modelFactory.createField();
        this.field.setSize(100, 100);
    }

    public void addFieldObserver(FieldObserver observer) {
        field.addObserver(observer);
    }

    public void removeFieldObserver(FieldObserver observer) {
        field.removeObserver(observer);
    }

    public void addDisplayModelObserver(DisplayModelObserver observer) {
        display.addObserver(observer);
    }

    public void removeDisplayObserver(DisplayModelObserver observer) {
        display.removeObserver(observer);
    }

    private void reset() {
        timer.stop();
        xorMode = false;
    }

    public void newDocument(int width, int height) {
        reset();
        field.setSize(height, width);
    }

    public void openDocument(String fileName) throws LifeIO.LifeIOException {
        reset();
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            Scanner scanner = new Scanner(inputStream);
            LifeIO lifeIO = new LifeIO();
            lifeIO.parse(scanner);
            lifeIO.setField(field);
            lifeIO.setDisplay(display);
        } catch (LifeIO.LifeIOException ex) {
            throw ex;
        } catch (java.io.IOException ex) {
            throw new LifeIO.LifeIOException("IO problem: " + ex.getLocalizedMessage(), ex);
        } catch (Exception ex) {
            throw new LifeIO.LifeIOException("Unknown problem: " + ex.getLocalizedMessage(), ex);
        }
    }

    public void saveDocument(String fileName) throws LifeIO.LifeIOException {
        if (field != null && !timer.isRunning()) {
            try (PrintStream stream = new PrintStream(fileName)) {
                LifeIO lifeIO = new LifeIO();
                lifeIO.write(stream, display, field);
            } catch (FileNotFoundException e) {
                throw new LifeIO.LifeIOException(e.getLocalizedMessage(), e);
            }
        }
    }


    public void clearTouch() {
        lastTouch = null;
    }

    public void touchCell(int row, int column) {
        if (!timer.isRunning() && (lastTouch == null || !(row == lastTouch.y && column == lastTouch.x))
                && row >= 0 && row < field.getHeight() && column >= 0 && column < field.getWidth(row)) {
            field.setState(row, column, field.getState(row, column) ^ xorMode || !xorMode);
            lastTouch = new Point(column, row);
        }
    }

    public void toggleLoopMode(boolean on) {
        if (timer.isRunning() && !on) {
            timer.stop();
        } else if (!timer.isRunning() && on){
            timer.start();
        }
    }

    public void toggleXORMode(boolean on) {
        xorMode = on;
    }

    public void makeStep() {
        if (!timer.isRunning())
            field.step();
    }

}
