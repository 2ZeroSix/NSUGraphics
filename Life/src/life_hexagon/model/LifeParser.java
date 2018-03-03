package life_hexagon.model;

import life_hexagon.observables.MutableFieldObservable;

import java.util.Scanner;

public class LifeParser {
    int cellSize;
    int separatorSize;
    void parse(Scanner scanner) {
        scanner.nextInt();
    }
    int getSeparatorSize() {
        return separatorSize;
    }
    int getCellSize() {
        return cellSize;
    }
    void setField(MutableFieldObservable field) {

    }
}
