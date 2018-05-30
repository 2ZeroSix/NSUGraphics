package life_hexagon.view;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MyImage extends BufferedImage {

    public MyImage(int width, int height) {
        super(width, height, TYPE_INT_ARGB);
    }


    public MyGraphics getMyGraphics() {
        return new MyGraphics(this);
    }
}
