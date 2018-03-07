package life_hexagon.controller;

import life_hexagon.model.LifeIO;
import life_hexagon.model.ModelFactory;
import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.model.observables.MutableEditModelObservable;
import life_hexagon.model.observables.MutableFieldObservable;
import life_hexagon.view.observers.DisplayModelObserver;
import life_hexagon.view.observers.EditModelObserver;
import life_hexagon.view.observers.FieldObserver;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Controller {
    private ModelFactory modelFactory;
    private MutableFieldObservable field;
    private MutableDisplayModelObservable display;
    private MutableEditModelObservable editModel;
    private boolean fill = true;
    private Point lastTouch = null;

    public Controller(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
        this.display = modelFactory.createDisplayModel();
        this.field = modelFactory.createField();
        this.editModel = modelFactory.createEditModel();
        this.editModel.setLoopTimer(new Timer(1000, e -> field.step()));
    }

    public synchronized void addFieldObserver(FieldObserver observer) {
        field.addObserver(observer);
    }

    public synchronized void removeFieldObserver(FieldObserver observer) {
        field.removeObserver(observer);
    }

    public synchronized void addDisplayModelObserver(DisplayModelObserver observer) {
        display.addObserver(observer);
    }

    public synchronized void removeDisplayObserver(DisplayModelObserver observer) {
        display.removeObserver(observer);
    }

    public synchronized void addEditModelObserver(EditModelObserver observer) {
        editModel.addObserver(observer);
    }

    public synchronized void removeEditModelObserver(EditModelObserver observer) {
        editModel.removeObserver(observer);
    }

    private synchronized void reset() {
        editModel.setLoop(false);
        lastTouch = null;
    }

    public synchronized void newDocument(int width, int height) {
        reset();
        for (int row = 0; row < field.getHeight(); ++row) {
            for (int column = 0; column < field.getWidth(row); ++column) {
                field.setState(row, column, false);
            }
        }
            field.setSize(height, width);
    }

    public synchronized void openDocument(String fileName) throws LifeIO.LifeIOException {
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

    public synchronized void saveDocument(String fileName) throws LifeIO.LifeIOException {
        if (!editModel.isLoop()) {
            try (PrintStream stream = new PrintStream(fileName)) {
                LifeIO lifeIO = new LifeIO();
                lifeIO.write(stream, display, field);
            } catch (FileNotFoundException e) {
                throw new LifeIO.LifeIOException(e.getLocalizedMessage(), e);
            }
        }
    }


    public synchronized void clearTouch() {
        lastTouch = null;
    }

    public synchronized void touchCell(int row, int column, boolean fill) {
        if (!editModel.isLoop() && (lastTouch == null || !(row == lastTouch.y && column == lastTouch.x))
                && row >= 0 && row < field.getHeight() && column >= 0 && column < field.getWidth(row)) {
            boolean xorMode = editModel.isXOR();
            field.setState(row, column, xorMode && !field.getState(row, column) || !xorMode && fill);
            this.fill = fill;
            lastTouch = new Point(column, row);
        }
    }

    public synchronized void touchCell(int row, int column) {
        touchCell(row, column, fill);
    }

    public synchronized void toggleLoopMode() {
        editModel.setLoop(!editModel.isLoop());
    }

    public synchronized void toggleXORMode() {
        editModel.setXOR(!editModel.isXOR());
    }

    public synchronized void toggleXORMode(boolean mode) {
        editModel.setXOR(mode);
    }

    public synchronized void makeStep() {
        if (!editModel.isLoop())
            field.step();
    }

    public synchronized void resize(int width, int height) {
        if (!editModel.isLoop())
            field.setSize(height, width);
    }
}
