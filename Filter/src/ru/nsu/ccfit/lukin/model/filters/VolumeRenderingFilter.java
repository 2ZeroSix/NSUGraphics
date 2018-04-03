package ru.nsu.ccfit.lukin.model.filters;

import ru.nsu.ccfit.lukin.model.filters.options.FilterOption;
import ru.nsu.ccfit.lukin.model.filters.options.NumberFilterOption;

import java.awt.image.BufferedImage;
import java.util.List;

public class VolumeRenderingFilter extends PixelFilter {
    private int Nx;
    private int Ny;
    private int Nz;
    private boolean absorption;
    private boolean emission;
    private int[] emissionConfiguration;
    private double[] absorptionConfiguration;
    private List<Charge> charges;
    private double minCharge;
    private double maxCharge;
    private double depth = 2;

    public static class Charge {
        double x;
        double y;
        double z;
        double q;
    }
    private double dx;
    private double dy;
    private double dz;
    private int w;
    private int h;


    public VolumeRenderingFilter() {
        super("Volume rendering filter");
        addOption("Nx", new NumberFilterOption<>(1, 350).setValue(350));
        addOption("Ny", new NumberFilterOption<>(1, 350).setValue(350));
        addOption("Nz", new NumberFilterOption<>(1, 350).setValue(350));
        addOption("absorption", new FilterOption<>(true));
        addOption("emission", new FilterOption<>(true));
        addOption("emission configuration", new FilterOption<int[]>(null));
        addOption("absorption configuration", new FilterOption<double[]>(null));
        addOption("charges", new FilterOption<List<Charge>>(null));
    }

    @Override
    protected void assignOptions() {
        try {
            Nx = (Integer) getOption("Nx").getValue();
            Ny = (Integer) getOption("Ny").getValue();
            Nz = (Integer) getOption("Nz").getValue();
            absorption = (Boolean) getOption("absorption").getValue();
            emission = (Boolean) getOption("emission").getValue();
            emissionConfiguration = (int[]) getOption("emission configuration").getValue();
            if (emissionConfiguration.length != 101)
                throw new IllegalArgumentException("wrong length of emission array");
            absorptionConfiguration = (double[]) getOption("absorption configuration").getValue();
            if (absorptionConfiguration.length != 101) {
                throw new IllegalArgumentException("wrong length of absorption array");
            }
            charges = (List<Charge>) getOption("charges").getValue();
            dx = depth / Nx;
            dy = depth / Ny;
            dz = depth / Nz;
            maxCharge = Double.MIN_VALUE;
            minCharge = Double.MAX_VALUE;
            for (int x = 0; x < Nx; ++x) {
                for (int y = 0; y < Ny; ++y) {
                    for (int z = 0; z < Nz; ++z) {
                        double charge = getCharge(x, y, z);
                        maxCharge = Math.max(charge, maxCharge);
                        minCharge = Math.min(charge, minCharge);
                    }
                }
            }
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("there is no configuration");
        }
    }

    @Override
    public void realApply(BufferedImage image) {
        w = image.getWidth();
        h = image.getHeight();
        super.realApply(image);
    }

    @Override
    protected int filterPixel(BufferedImage image, int x, int y) {
        int chunkX = getChunk(Math.round((float)x * Nx / w), Nx);
        int chunkY = getChunk(Math.round((float)y * Ny / h), Ny);
        int oldRgb = image.getRGB(x, y);
        int r = component(image, x, y, 2);
        int g = component(image, x, y, 1);
        int b = component(image, x, y, 0);
        for (int i = 0; i < Nz; ++i) {
            int charge = (int) Math.round(normalizeCharge(getCharge(chunkX, chunkY, i)));
            double absorptionMultiplier = Math.exp(-absorptionConfiguration[charge] * dz);
            int emissionR = (int) (((emissionConfiguration[charge] >> 16) & 0xFF) * dz);
            int emissionG = (int) (((emissionConfiguration[charge] >> 8) & 0xFF) * dz);
            int emissionB = (int) (((emissionConfiguration[charge]) & 0xFF) * dz);
            r = (int) ((absorption ? r *  absorptionMultiplier : r)
                                + (emission ?  emissionR : 0));
            g = (int) ((absorption ? g * absorptionMultiplier : g)
                                + (emission ? emissionG : 0));
            b = (int) ((absorption ? b * absorptionMultiplier : b)
                                + (emission ? emissionB : 0));
        }
        int newrgb = 0xFF000000 | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
        return newrgb;
    }

    private double normalizeCharge(double charge) {
        return Math.max(((charge - minCharge) * 100. / (maxCharge - minCharge)), 0);
    }

    private double getCharge(int chunkX, int chunkY, int chunkZ) {
        double q = 0;
        for (Charge charge : charges) {
            double x = (dx * chunkX - charge.x*depth);
            double y = (dy * chunkY - charge.y*depth);
            double z = (dz * chunkZ - charge.z*depth);
            double r = Math.max(x*x + y*y + z*z, .1);
            q += charge.q / Math.sqrt(r);
        }
        return q;
    }

    int getChunk(int coord, int chunks) {
        double dc = (depth / chunks);
        return (int)(Math.round(coord / dc) * dc);
    }
}
