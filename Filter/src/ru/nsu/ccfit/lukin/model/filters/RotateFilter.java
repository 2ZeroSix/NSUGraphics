package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;

public class RotateFilter extends PixelFilterWithTemp {
    private double angle;
    public RotateFilter(double angle) {
        super("Rotate filter");
        addOption("angle", new NumberFilterOption<>(-180., 180.).setValue(angle));
    }

    @Override
    protected void assignOptions() {
        this.angle = Math.toRadians((Double) getOption("angle").getValue());
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int centX = image.getWidth() / 2;
        int centY = image.getHeight() / 2;
        int newx = (int) (Math.cos(angle) * (x - centX) + Math.sin(angle) * (y - centY) + centX);
        int newy = (int) (-Math.sin(angle) * (x - centX) + Math.cos(angle) * (y - centY) + centY);
        if(newx >= 0 && newx < image.getWidth() && newy >= 0 && newy < image.getHeight()){
            return image.getRGB(newx, newy);
        } else {
            return 0xFFFFFFFF;
        }
    }
}
