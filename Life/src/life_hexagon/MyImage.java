package life_hexagon;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Stack;

public class MyImage extends BufferedImage {

    public MyImage(int width, int height) {
        super(width, height, TYPE_INT_ARGB);
    }


    public MyGraphics getMyGraphics() {
        return new MyGraphics(this);
    }

}
