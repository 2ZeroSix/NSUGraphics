package ru.nsu.ccfit.lukin.view.imagePanels;

import ru.nsu.ccfit.lukin.model.filters.VolumeRenderingFilter;
import ru.nsu.ccfit.lukin.view.observers.FilterObserver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VolumeConfigurationChart extends JPanel implements FilterObserver {
    Chart emissionChart;
    Chart absorptionChart;
    private VolumeRenderingFilter filter;

    public VolumeConfigurationChart(VolumeRenderingFilter filter) {
        super(new GridLayout(1, 2));
        ((GridLayout)getLayout()).setVgap(10);
        ((GridLayout)getLayout()).setHgap(10);
        this.filter = filter;
        filter.addFilterObserver(this);
        List<Color> emissionColors = new ArrayList<>();
        emissionColors.add(Color.RED);
        emissionColors.add(Color.GREEN);
        emissionColors.add(Color.BLUE);
        List<Integer>[] emissionValues = new ArrayList[3];
        for (int i = 0; i < 3; ++i) {
            emissionValues[i] = new ArrayList<>(101);
            for (int x = 0; x <= 100; ++x) {
                emissionValues[i].add(0);
            }
        }
        emissionChart = new Chart(emissionColors, 0, 255, emissionValues);
        List<Color> absorptionColors = new ArrayList<>();
        absorptionColors.add(Color.BLACK);
        List<Integer> absorptionValues = new ArrayList<>(101);
        for (int x = 0; x <= 100; ++x) {
            absorptionValues.add(0);
        }
        absorptionChart = new Chart(absorptionColors, 0, 10000, absorptionValues);
        add(emissionChart);
        add(absorptionChart);
        setPreferredSize(new Dimension(1050, 175));
    }

    @Override
    public void updateOption(String name, Object value) {
        switch (name) {
            case "emission configuration": {
                int[] emission = (int[]) value;
                if (emission != null) {
                    List<Integer>[] emissionValues = new ArrayList[3];
                    for (int i = 0; i < 3; ++i) {
                        emissionValues[i] = new ArrayList<>(101);
                        for (int x = 0; x <= 100; ++x) {
                            emissionValues[i].add((emission[x] >> (8 * (2 - i))) & 0xFF);
                        }
                    }
                    emissionChart.update(emissionValues);
                }
                break;
            }
            case "absorption configuration": {
                double[] absorption = (double[]) value;
                if (absorption != null) {
                    List<Integer> absorptionValues = new ArrayList<>(101);
                    for (int x = 0; x <= 100; ++x) {
                        absorptionValues.add((int) Math.round(absorption[x] * 10000));
                    }
                    absorptionChart.update(absorptionValues);
                }
                break;
            }

        }
    }
}
