package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public abstract class PixelFilter extends AbstractFilter {
    protected PixelFilter(String name) {
        super(name);
    }

    @Override
    protected void realApply(BufferedImage image) {
//        Map<Integer, Integer> countR = new HashMap<>();
//        Map<Integer, Integer> countG = new HashMap<>();
//        Map<Integer, Integer> countB = new HashMap<>();
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int filtered = filterPixel(image, x, y);
//                countR.put((filtered >> 16) & 0xFF, 1 + countR.getOrDefault(filtered, 0));
//                countG.put((filtered >> 8)  & 0xFF, 1 + countG.getOrDefault(filtered, 0));
//                countB.put((filtered)       & 0xFF, 1 + countB.getOrDefault(filtered, 0));
                image.setRGB(x, y, filtered);
            }
        }
//        System.out.println("R: " + countR.size());
//        System.out.println("G: " + countG.size());
//        System.out.println("B: " + countB.size());
    }

    protected abstract int filterPixel(BufferedImage image, int x, int y);
}
