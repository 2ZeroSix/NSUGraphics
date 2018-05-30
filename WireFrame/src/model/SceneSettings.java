package model;

import javafx.beans.property.*;

import java.awt.*;

public class SceneSettings {
    final private BooleanProperty showSettings;

    public final IntegerProperty n, m, k;
    public final DoubleProperty a, b;
    public final IntegerProperty c, d;
    public final DoubleProperty zn, zf, sw, sh;
    public final Property<Color> backgroundColor;
    public final Property<Color> selectColor;
    public final Property<Color> pointsColor;
    public final Property<Color> legendColor;

    public SceneSettings()
    {
        this.showSettings = new SimpleBooleanProperty(false);

        this.n = new SimpleIntegerProperty(10);
        this.m = new SimpleIntegerProperty(10);
        this.k = new SimpleIntegerProperty(10);

        this.a = new SimpleDoubleProperty(0);
        this.b = new SimpleDoubleProperty(1);

        this.c = new SimpleIntegerProperty(0);
        this.d = new SimpleIntegerProperty(360);

        this.zn = new SimpleDoubleProperty(10);
        this.zf = new SimpleDoubleProperty(10);
        this.sw = new SimpleDoubleProperty(10);
        this.sh = new SimpleDoubleProperty(10);

        this.backgroundColor = new SimpleObjectProperty<>(Color.LIGHT_GRAY);
        this.selectColor = new SimpleObjectProperty<>(Color.RED);
        this.pointsColor = new SimpleObjectProperty<>(Color.GREEN);
        this.legendColor = new SimpleObjectProperty<>(Color.BLACK);

    }}
