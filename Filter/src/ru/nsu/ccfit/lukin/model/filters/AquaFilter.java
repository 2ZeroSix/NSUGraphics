package ru.nsu.ccfit.lukin.model.filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AquaFilter extends PixelFilterWithTemp {
    public AquaFilter() {
        super("Aqua filter");
    }

    @Override
    protected void realApply(BufferedImage image) {
        super.realApply(image);
//        new SharpFilter().realApply(image);
    }

    private ArrayList<Integer> R = new ArrayList<>();
    private ArrayList<Integer> G = new ArrayList<>();
    private ArrayList<Integer> B = new ArrayList<>();
    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                R.add(component(image, x + i, y + j, 2));
                G.add(component(image, x + i, y + j, 1));
                B.add(component(image, x + i, y + j, 0));
            }
        }
        R.sort(Integer::compareTo);
        G.sort(Integer::compareTo);
        B.sort(Integer::compareTo);
        int r = R.get(R.size() / 2);
        int g = G.get(G.size() / 2);
        int b = B.get(B.size() / 2);
        R.clear();
        G.clear();
        B.clear();
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
