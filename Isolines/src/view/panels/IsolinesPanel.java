package view.panels;

import model.FunctionZ;
import model.MutableIsolineModel;
import view.ContainerUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class IsolinesPanel extends JPanel {
    private final MutableIsolineModel model;
    private Consumer<String> setStatus;


    class FunctionMap {
        private final FunctionZ function;
        private final int w;
        private final int h;
        private final BufferedImage image;
        private int radius = 5;

        FunctionMap(FunctionZ function, Dimension size) {
            image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            this.function = function;
            this.w = size.width;
            this.h = size.height;
        }


        void drawMap() {
            Function<Point.Double, Double> func;
            if (model.isPlot()) {
                func = function::calcByGrid;
            } else {
                func = function::calc;
            }
            double step = function.getRange() / (model.getKeyValues() + 1);
            for (int j = 0; j < h; ++j) {
                for (int i = 0; i < w; ++i) {
                    double val = func.apply(function.cast(i, j, new Dimension(w, h)));
                    int level_index = Integer.max(Integer.min((int) ((val - function.getMin()) / step), model.getKeyValues()), 0);
                    Color color = model.getColors().get(level_index);
                    if (model.isInterpolating()) {
                        double rel = val - function.getMin() - level_index * step;
                        int sign = (rel - step / 2 > 0) ? 1 : -1;
                        int neighbor_index = level_index + sign;
                        if (!(neighbor_index < 0 || neighbor_index > model.getKeyValues())) {
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
                    image.setRGB(i, j, color.getRGB());
                }
            }
        }


        void drawGrid() {
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int x = 0; x < model.getGridSize().width; ++x) {
                g2.drawLine(w * x / model.getGridSize().width, 0, w * x / model.getGridSize().width, h - 1);
            }
            for (int y = 0; y < model.getGridSize().height; ++y) {
                g2.drawLine(0, h * y / model.getGridSize().height, w - 1, h * y / model.getGridSize().height);
            }
            g2.dispose();
        }


        class Cell {
            Point.Double[] coords = new Point.Double[4];
            double[] values = new double[5];

            Cell(Rectangle.Double rectangle) {
                coords[0] = new Point.Double(rectangle.getMinX(), rectangle.getMinY());
                coords[1] = new Point.Double(rectangle.getMaxX(), rectangle.getMinY());
                coords[2] = new Point.Double(rectangle.getMaxX(), rectangle.getMaxY());
                coords[3] = new Point.Double(rectangle.getMinX(), rectangle.getMaxY());
                for (int i = 0; i < 4; ++i) {
                    values[i] = function.calcByGrid(coords[i]);
                    values[4] += values[i];
                }
                values[4] /= 4.;
            }


            int getCount(double level) {
                int count = 0;
                for (int i = 0; i < 4; ++i) {
                    count += values[i] >= level ? 1 : 0;
                }
                return Math.abs(count) % 4;
            }

            void setRotation(int rotation) {
                int rotate = (rotation + (rotation / 4 + 1) * 4) % 4;
                Point.Double[] tmp = new Point.Double[4];
                double[] val = new double[5];
                for (int i = 0; i < 4; ++i) {
                    tmp[i] = coords[(i + rotate) % 4];
                    val[i] = values[(i + rotate) % 4];
                }
                for (int i = 0; i < 4; ++i) {
                    coords[i] = tmp[i];
                    values[i] = val[i];
                }

            }

            List<Point.Double> findIsoline(double level) {
                int i;
                switch (getCount(level)) {
                    case 0:
                        return null;
                    case 1:
                        for (i = 0; i < 4; ++i) {
                            if (values[i] >= level) break;
                        }
                        setRotation((4 + i - 1) % 4);
                        return findNormilizedIsolineOneDot(level);
                    case 3:
                        for (i = 0; i < 4; ++i) {
                            if (values[i] < level) break;
                        }
                        setRotation((4 + i - 1) % 4);
                        return findNormilizedIsolineOneDot(level);
                    case 2:
                        boolean check = false;
                        int rotation = 0;
                        for (i = 0; i < 4; ++i) {
                            if (values[i] >= level) rotation = i + 1;
                            if (values[i] >= level && values[(i + 1) % 4] >= level) {
                                check = true;
                                rotation = i;
                                break;
                            }
                        }
                        setRotation((4 + rotation - 1) % 4);
                        if (check) {
                            return findNormilizedIsolineOneSide(level);
                        } else {
                            return findNormilizedIsolineTwoSides(level);
                        }
                    default:
                        throw new AssertionError("never happened case");
                }
            }

            private List<Point.Double> findNormilizedIsolineTwoSides(double level) {
                double multiplier;
                Point.Double point;
                List<Point.Double> list = new ArrayList<>();
                double centerValue = values[4];
                Point.Double centerPoint = new Point.Double();
                for (int i = 0; i < 4; ++i) {
                    centerPoint.x += coords[i].x;
                    centerPoint.y += coords[i].y;
                }
                centerPoint.x /= 4.;
                centerPoint.y /= 4.;

                if (values[4] >= level) {
                    multiplier = (level - values[0]) / (values[1] - values[0]);
                    point = new Point.Double((coords[0].x + (coords[1].x - coords[0].x) * multiplier),
                            (coords[0].y + (coords[1].y - coords[0].y) * multiplier));
                    list.add(point);

                    multiplier = (level - values[1]) / (centerValue - values[1]);
                    point = new Point.Double((coords[1].x + (centerPoint.x - coords[1].x) * multiplier),
                            (coords[1].y + (centerPoint.y - coords[1].y) * multiplier));
                    list.add(point);
                    list.add(point);

                    multiplier = (level - values[1]) / (values[2] - values[1]);
                    point = new Point.Double((coords[1].x + (coords[2].x - coords[1].x) * multiplier),
                            (coords[1].y + (coords[2].y - coords[1].y) * multiplier));
                    list.add(point);

                    multiplier = (level - values[2]) / (values[3] - values[2]);
                    point = new Point.Double((coords[2].x + (coords[3].x - coords[2].x) * multiplier),
                            (coords[2].y + (coords[3].y - coords[2].y) * multiplier));
                    list.add(point);

                    multiplier = (level - values[3]) / (centerValue - values[3]);
                    point = new Point.Double((coords[3].x + (centerPoint.x - coords[3].x) * multiplier),
                            (coords[3].y + (centerPoint.y - coords[3].y) * multiplier));
                    list.add(point);
                    list.add(point);

                    multiplier = (level - values[3]) / (values[0] - values[3]);
                    point = new Point.Double((coords[3].x + (coords[0].x - coords[3].x) * multiplier),
                            (coords[3].y + (coords[0].y - coords[3].y) * multiplier));
                    list.add(point);
                    return list;
                } else {
                    multiplier = (level - values[1]) / (values[2] - values[1]);
                    point = new Point.Double((coords[1].x + (coords[2].x - coords[1].x) * multiplier),
                            (coords[1].y + (coords[2].y - coords[1].y) * multiplier));
                    list.add(point);

                    multiplier = (level - values[2]) / (centerValue - values[2]);
                    point = new Point.Double((coords[2].x + (centerPoint.x - coords[2].x) * multiplier),
                            (coords[2].y + (centerPoint.y - coords[2].y) * multiplier));
                    list.add(point);
                    list.add(point);

                    multiplier = (level - values[2]) / (values[3] - values[2]);
                    point = new Point.Double((coords[2].x + (coords[3].x - coords[2].x) * multiplier),
                            (coords[2].y + (coords[3].y - coords[2].y) * multiplier));
                    list.add(point);

                    multiplier = (level - values[3]) / (values[0] - values[3]);
                    point = new Point.Double((coords[3].x + (coords[0].x - coords[3].x) * multiplier),
                            (coords[3].y + (coords[0].y - coords[3].y) * multiplier));
                    list.add(point);

                    multiplier = (level - values[0]) / (centerValue - values[0]);
                    point = new Point.Double((coords[0].x + (centerPoint.x - coords[0].x) * multiplier),
                            (coords[0].y + (centerPoint.y - coords[0].y) * multiplier));
                    list.add(point);
                    list.add(point);

                    multiplier = (level - values[0]) / (values[1] - values[0]);
                    point = new Point.Double((coords[0].x + (coords[1].x - coords[0].x) * multiplier),
                            (coords[0].y + (coords[1].y - coords[0].y) * multiplier));
                    list.add(point);
                    return list;

                }
            }

            private List<Point.Double> findNormilizedIsolineOneSide(double level) {
                double multiplier;
                Point.Double point;
                List<Point.Double> list = new ArrayList<>();
                for (int i = 0; i < 3; i += 2) {
                    multiplier = (level - values[i]) / (values[i + 1] - values[i]);
                    point = new Point.Double((coords[i].x + (coords[i + 1].x - coords[i].x) * multiplier),
                            (coords[i].y + (coords[i + 1].y - coords[i].y) * multiplier));
                    list.add(point);
                }
                return list;
            }

            private List<Point.Double> findNormilizedIsolineOneDot(double level) {
                double multiplier;
                Point.Double point;
                List<Point.Double> list = new ArrayList<>();
                for (int i = 0; i < 2; ++i) {
                    multiplier = (level - values[i]) / (values[i + 1] - values[i]);
                    point = new Point.Double((coords[i].x + (coords[i + 1].x - coords[i].x) * multiplier),
                            (coords[i].y + (coords[i + 1].y - coords[i].y) * multiplier));
                    list.add(point);
                }
                return list;
            }
        }


        void drawIsolines(boolean isIsolines, boolean isDots) {
            double step = function.getRange() / (model.getKeyValues() + 1);
            for (int i = 1; i <= model.getKeyValues(); ++i) {
                drawIsolineByLevel(i * step + function.getMin(), isIsolines, isDots);
            }
            model.getUserIsolines().forEach((level) -> drawIsolineByLevel(level, isIsolines, isDots));
            model.getDynamicIsoline().ifPresent((level) -> drawIsolineByLevel(level, isIsolines, isDots));
        }

        void drawIsolineByLevel(double level, boolean isIsolines, boolean isDots) {
            int width = model.getGridSize().width;
            int height = model.getGridSize().height;
            double cellWidth = model.getFunction().getWidth() / width;
            double cellHeight = model.getFunction().getHeight() / height;
            Cell cell;
            Graphics2D g2 = image.createGraphics();
            g2.setColor(model.getIsolineColor());
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Dimension size = new Dimension(w, h);
            for (int x = 0; x <= width; ++x) {
                for (int y = 0; y <= height; ++y) {
                    Point.Double p = function.cast(x, y, model.getGridSize());
                    cell = new Cell(new Rectangle.Double(p.x, p.y, cellWidth, cellHeight));
                    List<Point.Double> isolines = cell.findIsoline(level);
                    if (isolines != null)
                        for (int i = 0; i < isolines.size(); i += 2) {
                            Point p1 = function.cast(isolines.get(i), size);
                            Point p2 = function.cast(isolines.get(i + 1), size);
                            if (isIsolines) {
                                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                            }
                            if (isDots) {
                                g2.fillOval(p1.x - radius / 2, p1.y - radius / 2, radius, radius);
                                g2.fillOval(p2.x - radius / 2, p2.y - radius / 2, radius, radius);
                            }
                        }
                }
            }
        }

        BufferedImage getImage() {
            return image;
        }
    }

    public class Legend extends JPanel implements Observer, MouseMotionListener, MouseListener {
        JLabel label = new JLabel();
        FunctionZ function;
        BufferedImage image;

        Legend() {
            super(new GridLayout(1, 1));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(800, 120));
            add(label);

            label.addMouseMotionListener(this);
            label.addMouseListener(this);
        }

        @Override
        public void update(Observable o, Object arg) {
            synchronized (model) {
                function = new FunctionZ() {
                    {
                        setBounds(new Rectangle.Double(0, 0, 1, 1));
                        setGrid(getPreferredSize());
                    }

                    @Override
                    public double calc(double x, double y) {
                        return x * model.getFunction().getRange() + model.getFunction().getMin();
                    }
                };
                FunctionMap map = new FunctionMap(function, new Dimension(getPreferredSize().width, getPreferredSize().height - 20));
                map.drawMap();
                if (model.isIsolines()) {
                    map.drawIsolines(model.isIsolines(), false);
                }
                this.image = new BufferedImage(800, 120, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = this.image.createGraphics();
                Font font = new Font("Serif", Font.PLAIN, 10);
                g2.setFont(font);
                g2.setColor(Color.BLACK);
                FontMetrics fm = g2.getFontMetrics();
                int step = getPreferredSize().width / (model.getKeyValues() + 1);
                g2.drawImage(map.getImage(), 0, 20, null);
                for (int i = 1; i <= model.getKeyValues(); ++i) {
                    String str = String.format("%2.4f", function.calc(function.cast(i * step, 0, getPreferredSize())));
                    Rectangle2D bounds = fm.getStringBounds(str, g2);
                    g2.drawString(str, (int) (i * step - bounds.getWidth() / 2), 10 - (int) bounds.getHeight() / 2 + fm.getAscent());
                }
                g2.dispose();
                label.setIcon(new ImageIcon(image));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            double value = function.calc(function.cast(e.getPoint(), label.getSize()));
            if (model.isShowValue()) {
                if (setStatus != null)
                    setStatus.accept(" val=" + value);
            }
            if (model.isDynamic()) {
                model.setDynamicIsolineLevel(value);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (model.isShowValue()) {
                if (setStatus != null)
                    setStatus.accept(" val=" + function.calc(function.cast(e.getPoint(), label.getSize())));
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (model.isClicking()) {
                model.addUserIsoline(function.calc(function.cast(e.getPoint(), label.getSize())));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            double value = function.calc(function.cast(e.getPoint(), label.getSize()));
            if (model.isDynamic()) {
                model.setDynamicIsolineLevel(value);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public class Face extends JPanel implements Observer, MouseListener, MouseMotionListener {
        JLabel label = new JLabel();

        Face() {
            super(new GridLayout(1, 1));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(800, 500));
            add(label);
            label.addMouseListener(this);
            label.addMouseMotionListener(this);
        }

        @Override
        public void update(Observable o, Object arg) {
            synchronized (model) {
                FunctionMap map = new FunctionMap(model.getFunction(), getPreferredSize());
                map.drawMap();
                if (model.isGrid()) {
                    map.drawGrid();
                }
                if (model.isIsolines() || model.isGridDots()) {
                    map.drawIsolines(model.isIsolines(), model.isGridDots());
                }
                label.setIcon(new ImageIcon(map.getImage()));
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (model.isClicking()) {
                model.addUserIsoline(model.getFunction().calc(model.getFunction().cast(e.getPoint(), label.getSize())));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (model.isDynamic()) {
                Point.Double funcCoords = model.getFunction().cast(e.getPoint(), label.getSize());
                double value = model.getFunction().calc(funcCoords);
                model.setDynamicIsolineLevel(value);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point.Double funcCoords = model.getFunction().cast(e.getPoint(), label.getSize());
            double value = model.getFunction().calc(funcCoords);
            if (model.isShowValue()) {
                if (setStatus != null)
                    setStatus.accept("x=" + funcCoords.x + " y=" + funcCoords.y + " val=" + value);
            }
            if (model.isDynamic()) {
                model.setDynamicIsolineLevel(value);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (model.isShowValue()) {
                Point.Double funcCoords = model.getFunction().cast(e.getPoint(), label.getSize());
                if (setStatus != null)
                    setStatus.accept("x=" + funcCoords.x + " y=" + funcCoords.y + " val=" + model.getFunction().calc(funcCoords));
            }
        }

    }

    public IsolinesPanel(MutableIsolineModel model, Consumer<String> setStatus) {
        super(new GridBagLayout());
        this.model = model;
        this.setStatus = setStatus;
        Face face = new Face();
        Legend legend = new Legend();
        model.addObserver(face);
        model.addObserver(legend);
        ContainerUtils.add(this, face, 0, 0, 1, 1);
        ContainerUtils.add(this, legend, 0, 1, 1, 1);
    }
}
