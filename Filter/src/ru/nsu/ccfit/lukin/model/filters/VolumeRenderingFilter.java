package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;

public class VolumeRenderingFilter extends PixelFilter{
    private int Nx;
    private int Ny;
    private int Nz;
    private boolean absorption;
    private boolean emission;
    protected VolumeRenderingFilter() {
        super("Volume rendering filter");
        addOption("Nx", new NumberFilterOption<>(1, 350).setValue(350));
        addOption("Ny", new NumberFilterOption<>(1, 350).setValue(350));
        addOption("Nz", new NumberFilterOption<>(1, 350).setValue(350));
        addOption("absorption", new FilterOption<>(true));
        addOption("emission", new FilterOption<>(true));
    }

    @Override
    protected void assignOptions() {
        Nx = (Integer)getOption("Nx").getValue();
        Ny = (Integer)getOption("Ny").getValue();
        Nz = (Integer)getOption("Nz").getValue();
        absorption = (Boolean)getOption("absorption").getValue();
        emission = (Boolean)getOption("emission").getValue();
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
//        double dz = (float)Nz / ;
        return 0;
    }
}
