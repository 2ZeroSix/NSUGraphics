package life_hexagon.model;

import life_hexagon.model.observables.MutableDisplayModelObservable;
import life_hexagon.model.observables.MutableEditModelObservable;
import life_hexagon.model.observables.MutableFieldObservable;

public class ModelFactoryImpl implements ModelFactory {
    private static int defaultWidth = 10;
    private static int defaultHeight = 10;
    private static float defaultFirstImpact = 1f;
    private static float defaultSecondImpact = .3f;
    private static float defaultBirthBegin = 2.3f;
    private static float defaultBirthEnd = 2.9f;
    private static float defaultLiveBegin = 2f;
    private static float defaultLiveEnd = 3.3f;
    @Override
    public MutableFieldObservable createField() {
        return new GameModel(defaultWidth, defaultHeight,
                defaultFirstImpact, defaultSecondImpact,
                defaultBirthBegin, defaultBirthEnd,
                defaultLiveBegin, defaultLiveEnd);
    }

    @Override
    public MutableDisplayModelObservable createDisplayModel() {
        return new DisplayModel(1, 30, false);
    }

    @Override
    public MutableEditModelObservable createEditModel() {
        return new EditModel(true);
    }
}
