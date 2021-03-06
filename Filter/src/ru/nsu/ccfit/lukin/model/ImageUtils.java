package ru.nsu.ccfit.lukin.model;

import java.awt.*;
import java.awt.image.BufferedImage;

public enum ImageUtils {;

    public static BufferedImage copy(Image source, BufferedImage destination) {
        if (source.getHeight(null) != destination.getHeight() || source.getWidth(null) != destination.getWidth()) {
            return copy(source);
        }
        for (int x = 0; x < destination.getWidth(); ++x) {
            for (int y = 0; y < destination.getHeight(); ++y) {
                destination.setRGB(x, y, 0);
            }
        }
        Graphics2D g = destination.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return destination;
    }

    public static BufferedImage copy(Image source) {
        BufferedImage b;
        if (source instanceof BufferedImage) {
            BufferedImage src = (BufferedImage)source;
            b = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        } else {
            b = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        }
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

    public static int negativePixel(int p) {
        return (0xFF000000) |  ((255 - ((p >> 16) & 0xFF)) << 16) |  ((255 - ((p >> 8) & 0xFF)) << 8) |  (255 - ((p) & 0xFF));
    }
}
