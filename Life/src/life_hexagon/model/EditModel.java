package life_hexagon.model;

import life_hexagon.model.observables.MutableEditModelObservable;
import life_hexagon.view.observers.EditModelObserver;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public class EditModel implements MutableEditModelObservable {
    private Set<EditModelObserver> observers = new HashSet<>();
    private boolean xor;
    private boolean loop;
    private Timer timer;

    public EditModel(boolean xor) {
        this.xor = xor;
    }

    @Override
    public void setXOR(boolean on) {
        if (xor != on) {
            xor = on;
            notifyXOR();
        }
    }

    @Override
    public void setLoop(boolean on) {
        if (on != this.loop) {
            this.loop = on;
            if (timer != null) {
                if (on) {
                    timer.start();
                } else {
                    timer.stop();
                }
            }
            notifyLoop();
        }
    }

    @Override
    public void setLoopTimer(Timer timer) {
        this.timer = timer;
    }

    @Override
    public boolean isXOR() {
        return xor;
    }

    @Override
    public boolean isLoop() {
        return loop;
    }

    @Override
    public void notifyEdit() {
        for (EditModelObserver observer : observers)
            observer.updateEdit(this);
    }

    @Override
    public void notifyLoop() {
        for (EditModelObserver observer : observers)
            observer.updateLoop(this);
    }

    @Override
    public void notifyXOR() {
        for (EditModelObserver observer : observers)
            observer.updateXOR(this);
    }

    @Override
    public void addObserver(EditModelObserver observer) {
        observers.add(observer);
        observer.updateEdit(this);
    }

    @Override
    public void removeObserver(EditModelObserver observer) {
        observers.remove(observer);
    }
}
