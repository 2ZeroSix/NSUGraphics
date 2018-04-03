package ru.nsu.ccfit.lukin.view.imagePanels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Chart extends JLabel {
    private List<Color> colors;
    private int min;
    private int max;
    private List<Integer>[] values;
    public Chart(List<Color> colors, int min, int max, List<Integer>... values) {
        update(colors, min, max, values);
    }

    public void update(List<Integer>... values) {
        update(colors, min, max, values);
    }

    public void update(List<Color> colors, int min, int max, List<Integer>... values) {
        this.colors = new ArrayList<>(colors);
        this.min = min;
        this.max = max;
        this.values = (List<Integer>[]) new List[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.values[i] = new ArrayList<>(values[i]);
        }
        repaint();
    }

    @Override
    public void paint(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        Rectangle bounds = getBounds();
        int w = bounds.width;
        int h = bounds.height;
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g.drawLine(0, 0, 0, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        for (int i = 0; i < colors.size(); ++i) {
            g.setColor(colors.get(i));
            for (int x = 0; x < values[i].size() - 1; ++x) {
                g.drawLine(
                        x*w / values[i].size() + i + 2, h - (calcY(values[i].get(x), h - colors.size() - 2) + i + 2),
                        (x + 1)*w / values[i].size() + i + 2, h - (calcY(values[i].get(x + 1), h - colors.size() - 2) + i + 2));
            }
        }
    }
    private int calcY(int val, int height) {
        return (val - min) * height / (max - min);
    }
}
