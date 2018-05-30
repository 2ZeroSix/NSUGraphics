package ru.nsu.ccfit.lukin.model.filters;

import javafx.geometry.Point3D;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class VRFilterLoader {
    VolumeRenderingFilter filter;

    public VRFilterLoader(VolumeRenderingFilter filter) {
        this.filter = filter;
    }

    public void load(File file) {
        try (Scanner scanner = new Scanner(file)){
            scanner.useDelimiter("(([\\v\\h])|([\\v\\h]*(//.*[\\v])[\\v\\h]*))+");
            int absorptionCount = scanner.nextInt();
            AbstractMap.SimpleEntry<Integer, Double>[] absorption = new AbstractMap.SimpleEntry[absorptionCount];
            for (int i = 0; i < absorptionCount; ++i) {
                int coord = scanner.nextInt();
                double value = scanner.nextDouble();
                absorption[i] = new AbstractMap.SimpleEntry<>(coord, value);
            }

            int emissionCount = scanner.nextInt();
            Point[] emission = new Point[emissionCount];
            for (int i = 0; i < emissionCount; ++i) {
                int coord = scanner.nextInt();
                int r = scanner.nextInt();
                int g = scanner.nextInt();
                int b = scanner.nextInt();
                int rgb = 0xFF000000 | (AbstractFilter.clamp(r) << 16) | (AbstractFilter.clamp(g) << 8) | AbstractFilter.clamp(b);
                emission[i] = new Point(coord, rgb);
            }

            int chargesCount = scanner.nextInt();
            List<VolumeRenderingFilter.Charge> charges = new ArrayList<>(101);

            for (int i = 0; i < chargesCount; ++i) {
                VolumeRenderingFilter.Charge charge = new VolumeRenderingFilter.Charge();
                charge.x = scanner.nextDouble();
                charge.y = scanner.nextDouble();
                charge.z = scanner.nextDouble();
                charge.q = scanner.nextDouble();
                charges.add(charge);
            }

            Arrays.sort(absorption, Comparator.comparingInt(AbstractMap.SimpleEntry::getKey));
            double[] absorption_configuration = (double[])filter.getOption("absorption configuration").getValue();
            if (absorption_configuration == null) {
                absorption_configuration = new double[101];
            }
            int j = 0;
            for (int i = 0; i < 101; ++i) {
                if (j + 1 < absorptionCount && absorption[j + 1].getKey() > i || j + 1 == absorptionCount) {
                    int mincoord = absorption[j].getKey();
                    int maxcoord = j + 1 < absorptionCount ? absorption[j + 1].getKey() : 101;
                    double min = absorption[j].getValue();
                    double max = j + 1 < absorptionCount ? absorption[j + 1].getValue() : min;
                    absorption_configuration[i] = (min * (maxcoord - i) + max * (i - mincoord)) / (maxcoord - mincoord);
                } else {
                    --i;
                    ++j;
                }
            }
            Arrays.sort(emission, (a, b) -> (a.x - b.x));
            int[] emission_configuration = (int[])filter.getOption("emission configuration").getValue();
            if (emission_configuration == null) {
                emission_configuration = new int[101];
            }
            j = 0;
            for (int i = 0; i < 101; ++i) {
                if (j + 1 < emissionCount && emission[j + 1].x > i || j + 1 == emissionCount) {
                    int mincoord = emission[j].x;
                    int maxcoord = j + 1 < emissionCount ? emission[j + 1].x : 101;
                    int minR = (emission[j].y >> 16) & 0xFF;
                    int minG = (emission[j].y >> 8) & 0xFF;
                    int minB = (emission[j].y) & 0xFF;
                    int maxR = j + 1 < emissionCount ? (emission[j + 1].y >> 16) & 0xFF : minR;
                    int maxG = j + 1 < emissionCount ? (emission[j + 1].y >> 8) & 0xFF : minG;
                    int maxB = j + 1 < emissionCount ? (emission[j + 1].y) & 0xFF : minB;
                    emission_configuration[i] = 0xFF000000
                            | ((minR * (maxcoord - i) + maxR * (i - mincoord)) / (maxcoord - mincoord)) << 16
                            | ((minG * (maxcoord - i) + maxG * (i - mincoord)) / (maxcoord - mincoord)) << 8
                            | ((minB * (maxcoord - i) + maxB * (i - mincoord)) / (maxcoord - mincoord));
                } else {
                    --i;
                    ++j;
                }
            }
            filter.setOption("emission configuration", emission_configuration);
            filter.setOption("absorption configuration", absorption_configuration);
            filter.setOption("charges", charges);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }
}
