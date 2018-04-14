package view.panels;

import model.FunctionZ;
import model.IsolineModel;
import model.MutableIsolineModel;
import model.observers.IsolineModelObserver;
import view.ContainerUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class IsolinesPanel extends JPanel implements IsolineModelObserver {
    private Legend legend;
    private Face face;
    private MutableIsolineModel model;

    @Override
    public void update(IsolineModel isolineModel) {

    }

    class FunctionMap extends BufferedImage {
        private FunctionZ function;
        private int w;
        private int h;

        FunctionMap(FunctionZ function, Dimension size) {
            super(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            this.function = function;
            this.w = size.width;
            this.h = size.height;
        }


        void drawMap() {
            double xStep = (function.getBounds().width) / w;
            double yStep = (function.getBounds().height) / h;
            double step = ((function.getMax() - function.getMin()) / (model.getKeyValues().size() + 1));
            for (int j = 0; j < h; ++j) {
                for (int i = 0; i < w; ++i) {
                    double x = (i) * xStep;
                    double y = (j) * yStep;
                    double val = function.calc(x, y);
                    int level_index = Integer.min((int) ((val - function.getMin()) / step), model.getKeyValues().size());
                    Color color = model.getColors().get(level_index);
                    if (model.isInterpolating()) {
                        double rel = val - function.getMin() - level_index * step;
                        int sign = (rel > step / 2) ? 1 : -1;
                        int neighbor_index = level_index + sign;
                        if (!(neighbor_index < 0 || neighbor_index > model.getKeyValues().size())) {
                            Color neighbor_color = model.getColors().get(neighbor_index);
                            double factor_neighbor;
                            double factor_this;
                            if (sign > 0) {
                                factor_neighbor = (rel - step / 2) / step;
                                factor_this = 1.0 - factor_neighbor;
                            } else {
                                factor_neighbor = (step / 2 - rel) / step;
                                factor_this = 1.0 - factor_neighbor;
                            }
                            int r = Integer.max(0, Integer.min(255, (int) (color.getRed() * factor_this + neighbor_color.getRed() * factor_neighbor)));
                            int g = Integer.max(0, Integer.min(255, (int) (color.getGreen() * factor_this + neighbor_color.getGreen() * factor_neighbor)));
                            int b = Integer.max(0, Integer.min(255, (int) (color.getBlue() * factor_this + neighbor_color.getBlue() * factor_neighbor)));
                            color = new Color(r, g, b);
                        }
                    }
                    setRGB(i, (h - 1 - j), color.getRGB());
                }
            }
        }
        AbstractMap.SimpleEntry[] handleCell1(AbstractMap.SimpleEntry<Point, Double>[] vertices,
                                              double value,
                                              int bigger) {
            Comparator<AbstractMap.SimpleEntry<Point, Double>> comp = bigger == 1 ?
                    Comparator.comparingDouble(java.util.AbstractMap.SimpleEntry::getValue):
            (a,b) -> (int) (b.getValue() - a.getValue());

            Arrays.sort(vertices, comp);
            AbstractMap.SimpleEntry<Point, Double> point = vertices[0];

            Point start = new Point(point.getKey().x, 0);
            Point end = new Point(0, point.getKey().y);

            for (AbstractMap.SimpleEntry<Point, Double> vertice : vertices) {
                if (point.getKey().x == vertice.getKey().x) {
                    int offset = point.getValue() > vertice.getValue() ? point.getKey().y : vertice.getKey().y;
                    int sign = offset == Integer.max(point.getKey().y, vertice.getKey().y) ? -1 : 1;
                    start.y = (int) (sign
                                                * Math.abs(value - Math.max(point.getValue(), vertice.getValue()))
                                                * Math.abs(point.getKey().y - vertice.getKey().y) / Math.abs(point.getValue() - vertice.getValue())
                                                + offset);
                }
                if (point.getKey().y == vertice.getKey().y) {
                    int offset = point.getValue() > vertice.getValue() ? point.getKey().x : vertice.getKey().x;
                    int sign = offset == Math.max(point.getKey().x, vertice.getKey().x) ? -1 : 1;
                    end.x = (int) (sign * Math.abs(value - Math.max(point.getValue(), vertice.getValue())) * Math.abs(point.getKey().x - vertice.getKey().x) / Math.abs(point.getValue() - vertice.getValue())
                                                + offset);
                }
            }
            return new AbstractMap.SimpleEntry[]{new AbstractMap.SimpleEntry<Point, Point>(start, end)};
        }

        java.util.AbstractMap.SimpleEntry[] handleCell2(AbstractMap.SimpleEntry<Point, Double>[] vertices,
                                                        double value,
                                                        double middle_value) {
            Arrays.sort(vertices, Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue));

            java.util.AbstractMap.SimpleEntry<Point, Double> p1 = vertices[0];
            java.util.AbstractMap.SimpleEntry<Point, Double> p2 = vertices[1];
            java.util.AbstractMap.SimpleEntry<Point, Double> p3 = vertices[2];
            java.util.AbstractMap.SimpleEntry<Point, Double> p4 = vertices[3];
            if (p1.getKey().x == p2.getKey().x) {
                Point start = new Point(0, p1.getKey().y);
                Point end = new Point(0, p2.getKey().y);
                if (p1.getKey().y != p3.getKey().y) {
                    java.util.AbstractMap.SimpleEntry<Point, Double> tmp = p3;
                    p3 = p4;
                    p4 = tmp;
                }
                int offset = p1.getKey().x;
                int sign = offset == Math.max(p1.getKey().x, p3.getKey().x) ? -1 : 1;
                start.x = (int) (offset + sign * Math.abs(value - p1.getValue()) * Math.abs(p1.getKey().x - p3.getKey().x) / Math.abs(p1.getValue() - p3.getValue()));
                offset = p2.getKey().x;
                sign = offset == Math.max(p2.getKey().x, p4.getKey().x) ? -1 : 1;
                end.x = (int) (offset + sign * Math.abs(value - p2.getValue()) * Math.abs(p2.getKey().x - p4.getKey().x) / Math.abs(p2.getValue() - p4.getValue()));
                return new AbstractMap.SimpleEntry[]{new AbstractMap.SimpleEntry<Point, Point>(start, end)};
            } else if (p1.getKey().y == p2.getKey().y) {
                Point start = new Point(p1.getKey().x, 0);
                Point end = new Point(p2.getKey().x, 0);
                if (p1.getKey().x != p3.getKey().x) {
                    java.util.AbstractMap.SimpleEntry<Point, Double> tmp = p3;
                    p3 = p4;
                    p4 = tmp;
                }
                int offset = p1.getKey().y;
                int sign = offset == Math.max(p1.getKey().y, p3.getKey().y) ? -1 : 1;
                start.y = (int) (offset + sign * Math.abs(value - p1.getValue()) * Math.abs(p1.getKey().y - p3.getKey().y) / Math.abs(p1.getValue() - p3.getValue()));
                offset = p2.getKey().y;
                sign = offset == Math.max(p2.getKey().y, p4.getKey().y) ? -1 : 1;
                end.y = (int) (offset + sign * Math.abs(value - p2.getValue()) * Math.abs(p2.getKey().y - p4.getKey().y) / Math.abs(p2.getValue() - p4.getValue()));
                return new AbstractMap.SimpleEntry[]{new AbstractMap.SimpleEntry<Point, Point>(start, end)};
            } else if (middle_value > value){
                Point start1 = new Point(p1.getKey().x, 0);
                Point end1 = new Point(0, p2.getKey().y);
                Point start2 = new Point(p2.getKey().x, 0);
                Point end2 = new Point(0, p1.getKey().y);
                if (p1.getKey().x != p3.getKey().x) {
                    java.util.AbstractMap.SimpleEntry<Point, Double> tmp = p3;
                    p3 = p4;
                    p4 = tmp;
                }
                int offset = p2.getKey().x;
                int sign = offset == Math.max(p2.getKey().x, p3.getKey().x) ? -1 : 1;
                end1.x = (int) (offset + sign * Math.abs(value - p2.getValue()) * Math.abs(p2.getKey().x - p3.getKey().x) / Math.abs(p2.getValue() - p3.getValue()));
                offset = p1.getKey().y;
                sign = offset == Math.max(p1.getKey().y, p3.getKey().y) ? -1 : 1;
                start1.y = (int) (offset + sign * Math.abs(value - p1.getValue()) * Math.abs(p1.getKey().y - p3.getKey().y) / Math.abs(p1.getValue() - p3.getValue()));
                offset = p1.getKey().x;
                sign = offset == Math.max(p1.getKey().x, p4.getKey().x) ? -1 : 1;
                end2.x = (int) (offset + sign * Math.abs(value - p1.getValue()) * Math.abs(p4.getKey().x - p1.getKey().x) / Math.abs(p1.getValue() - p4.getValue()));
                offset = p2.getKey().y;
                sign = offset == Math.max(p2.getKey().y, p4.getKey().y) ? -1 : 1;
                start2.y = (int) (offset + sign * Math.abs(value - p2.getValue()) * Math.abs(p2.getKey().y - p4.getKey().y) / Math.abs(p2.getValue() - p4.getValue()));
                return new AbstractMap.SimpleEntry[] {new AbstractMap.SimpleEntry<Point, Point>(start1, end1), new AbstractMap.SimpleEntry<>(start2, end2)};
            } else {
                Point start1 = new Point(p1.getKey().x, 0);
                Point end1 = new Point(0, p1.getKey().y);
                Point start2 = new Point(p2.getKey().x, 0);
                Point end2 = new Point(0, p2.getKey().y);
                if (p1.getKey().x != p3.getKey().x) {
                    java.util.AbstractMap.SimpleEntry<Point, Double> tmp = p3;
                    p3 = p4;
                    p4 = tmp;
                }
                int offset = p1.getKey().x;
                int sign = offset == Math.max(p1.getKey().x, p4.getKey().x) ? -1 : 1;
                end1.x = (int) (offset + sign * Math.abs(value - p1.getValue()) * Math.abs(p1.getKey().x - p4.getKey().x) / Math.abs(p1.getValue() - p4.getValue()));
                offset = p1.getKey().y;
                sign = offset == Math.max(p1.getKey().y, p3.getKey().y) ? -1 : 1;
                start1.y = (int) (offset + sign * Math.abs(value - p1.getValue()) * Math.abs(p1.getKey().y - p3.getKey().y) / Math.abs(p1.getValue() - p3.getValue()));
                offset = p2.getKey().x;
                sign = offset == Math.max(p2.getKey().x, p3.getKey().x) ? -1 : 1;
                end2.x = (int) (offset + sign * Math.abs(value - p2.getValue()) * Math.abs(p2.getKey().x - p3.getKey().x) / Math.abs(p2.getValue() - p3.getValue()));
                offset = p2.getKey().y;
                sign = offset == Math.max(p2.getKey().y, p4.getKey().y) ? -1 : 1;
                start2.y = (int) (offset + sign * Math.abs(value - p2.getValue()) * Math.abs(p2.getKey().y - p4.getKey().y) / Math.abs(p2.getValue() - p4.getValue()));
                return new AbstractMap.SimpleEntry[] {new AbstractMap.SimpleEntry<>(start1, end1), new AbstractMap.SimpleEntry<>(start2, end2)};
            }
        }

        java.util.AbstractMap.SimpleEntry<Point, Point>[] handleCell(AbstractMap.SimpleEntry<Point, Double>[] vertices,
                                                                   double value, double middle_value) {
            int bigger = 0;
            for (AbstractMap.SimpleEntry<Point, Double> vertice : vertices) {
                if (vertice.getValue() > value) {
                    ++bigger;
                }
            };
            if (bigger == 4 || bigger == 0) {
                return new AbstractMap.SimpleEntry[0];
            }
            if (bigger == 1 || bigger == 3) {
                return handleCell1(vertices, value, bigger);
            }
            if (bigger == 2) {
                return handleCell2(vertices, value, middle_value);
            }
            return new AbstractMap.SimpleEntry[0];
        }

        void drawGrid() {} {
            Graphics2D g2 = createGraphics();
            g2.setColor(Color.BLACK);
            for (int x = 0; x < model.getGridSize().width; ++x) {
                g2.drawLine(w * x / model.getGridSize().width,0, w * x / model.getGridSize().width,h - 1);
            }
            for (int y = 0; y < model.getGridSize().height; ++y) {
                g2.drawLine(0, h * y / model.getGridSize().height,w-1, h * y / model.getGridSize().height);
            }
            g2.dispose();
        }

        void drawIsolines() {
            Color color = model.getIsolineColor();
            int horizontal_cells = model.getGridSize().width;
            int vertical_cells = model.getGridSize().height;
            double xStep = (function.getBounds().width) / w;
            double yStep = (function.getBounds().height) / h;
            double step = ((function.getMax() - function.getMin()) / (model.getKeyValues().size() + 1));
            double cell_width = (function.getBounds().width + 1.0 * xStep) / horizontal_cells;
            double cell_height = (function.getBounds().height + 1.0 * yStep) / vertical_cells;
            double scaled_cell_width = cell_width / xStep;
            double scaled_cell_height = cell_height / yStep;
            for (int j = 0; j < vertical_cells; ++j) {
                for (int i = 0; i < horizontal_cells; ++i) {
                    int x = (int) (Math.round(i * scaled_cell_width) - 1);
                    int x_next = (int) (Math.round((i + 1) * scaled_cell_width) - 1);
                    int y = (int) (Math.round(j * scaled_cell_height) - 1);
                    int y_next = (int) (Math.round((j + 1) * scaled_cell_height) - 1);
                    double f_x = i * cell_width - 1.0 * xStep;
                    double f_x_next = (i + 1) * cell_width - 1.0 * xStep;
                    double f_y = j * cell_height - 1.0 *yStep;
                    double f_y_next = (j + 1) * cell_height - 1.0 * yStep;
                    AbstractMap.SimpleEntry<Point, Double>[] cell = new AbstractMap.SimpleEntry[]{
                            new AbstractMap.SimpleEntry<>(new Point(x, h - 1 - y),
                                    function.calc(f_x, f_y)),

                            new AbstractMap.SimpleEntry<>(new Point(x_next, h - 1 - y),
                                    function.calc(f_x_next, f_y)),

                            new AbstractMap.SimpleEntry<>(new Point(x, h - 1 - y_next),
                                    function.calc(f_x, f_y_next)),

                            new AbstractMap.SimpleEntry<>(new Point(x_next, h - 1 - y_next),
                                    function.calc(f_x_next, f_y_next))
                    };
                    double middle_value = function.calc(i * cell_width / 2, (j * cell_height / 2));
                    Graphics2D g2 = createGraphics();
                    g2.setColor(Color.BLACK);
                    for (int k = 0; k < model.getKeyValues().size(); ++k) {
                        double isoline_level = model.getFunction().getMin() + (k + 1) * step;
                        AbstractMap.SimpleEntry<Point, Point>[] isolines = handleCell(cell, isoline_level, middle_value);
                        for (AbstractMap.SimpleEntry<Point, Point> isoline : isolines) {
                            if (model.isGridDots()) {
                                g2.fillOval(isoline.getKey().x, isoline.getKey().y, 3, 3);
                                g2.fillOval(isoline.getValue().x, isoline.getValue().y, 3, 3);
                            }
                            g2.drawLine(isoline.getKey().x, isoline.getKey().y, isoline.getValue().x, isoline.getValue().y);
                        }
                    }
                    for (double level : model.getUserIsolines()) {
                        AbstractMap.SimpleEntry<Point, Point>[] isolines = handleCell(cell, level, middle_value);
                        for (AbstractMap.SimpleEntry<Point, Point> isoline : isolines) {
                            if (model.isGridDots()) {
                                g2.fillOval(isoline.getKey().x, isoline.getKey().y, 3, 3);
                                g2.fillOval(isoline.getValue().x, isoline.getValue().y, 3, 3);
                            }
                            g2.drawLine(isoline.getKey().x, isoline.getKey().y, isoline.getValue().x, isoline.getValue().y);
                        }
                    }
                    g2.dispose();
                }
            }
        }

        public void drawValue(Point2D.Double aDouble) {

        }
    }

    public class Legend extends JPanel implements IsolineModelObserver {
        JLabel label = new JLabel();

        Legend() {
            super(new GridLayout(1, 1));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(800, 100));
            add(label);
        }

        @Override
        public void update(IsolineModel isolineModel) {
            FunctionMap map = new FunctionMap(new FunctionZ() {
                @Override
                public Rectangle.Double getBounds() {
                    return new Rectangle.Double(0, 0, 1, 0);
                }

                @Override
                public double calc(double x, double y) {
                    return x * (model.getFunction().getMax() - model.getFunction().getMin()) + model.getFunction().getMin();
                }

                @Override
                public double getMax() {
                    return model.getFunction().getMax();
                }

                @Override
                public double getMin() {
                    return model.getFunction().getMin();
                }
            }, getPreferredSize());
            if (model.isPlot()) {
                map.drawMap();
            }
            if (model.isIsolines()) {
                map.drawIsolines();
            }
            if (model.isShowValue() && model.getCursor() != null) {
                map.drawValue(new Point.Double(
                        (model.getFunction().calc(model.getCursor().x, model.getCursor().y) - model.getFunction().getMin()) / (model.getFunction().getMax() - model.getFunction().getMin()),
                        0));
            }
            label.setIcon(new ImageIcon(map));
        }
    }

    public class Face extends JPanel implements IsolineModelObserver {
        JLabel label = new JLabel();

        Face() {
            super(new GridLayout(1, 1));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(800, 500));
            add(label);
        }

        @Override
        public void update(IsolineModel isolineModel) {
            FunctionMap map = new FunctionMap(model.getFunction(), getPreferredSize());
            if (model.isPlot()) {
                map.drawMap();
            }
            if (model.isGrid()) {
                map.drawGrid();
            }
            if (model.isIsolines()) {
                map.drawIsolines();
            }
            if (model.isShowValue() && model.getCursor() != null) {
                map.drawValue(model.getCursor());
            }
            label.setIcon(new ImageIcon(map));
        }
    }

    public IsolinesPanel(MutableIsolineModel model) {
        super(new GridBagLayout());
        this.model = model;
        face = new Face();
        legend = new Legend();
        model.addObserver(this);
        model.addObserver(face);
        model.addObserver(legend);
        ContainerUtils.add(this, face, 0, 0, 1, 1);
        ContainerUtils.add(this, legend, 0, 1, 1, 1);
    }
}
