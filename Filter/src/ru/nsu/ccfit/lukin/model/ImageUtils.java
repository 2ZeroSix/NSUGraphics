package ru.nsu.ccfit.lukin.model;

import java.awt.*;
import java.awt.image.BufferedImage;

public enum ImageUtils {;

    public static BufferedImage copy(Image source, BufferedImage destination) {
        Graphics2D g = destination.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return destination;
    }

    public static BufferedImage copy(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        return copy(source, b);
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
