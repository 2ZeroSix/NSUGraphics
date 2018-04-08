package view.panels;

import view.ContainerUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class IsolinesPanel extends JPanel {
    private Legend legend;
    private Face face;
    private Function2x2 func;
    public interface Function2x2 {
        Rectangle.Double getBounds();
        double calcX(double x, double y);
        double calcY(double x, double y);
    }

    public class Legend extends JPanel {
        Legend() {
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(800, 100));
        }
    }

    public class Face extends JPanel {
        private Function2x2 func;

        Face() {
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            setPreferredSize(new Dimension(800, 500));
        }
    }
    public IsolinesPanel() {
        this(getDefaultFunction2x2());
    }

    public IsolinesPanel(Function2x2 func) {
        super(new GridBagLayout());
        this.func = func;
        face = new Face();
        legend = new Legend();
        ContainerUtils.add(this, face, 0, 0, 1, 1);
        ContainerUtils.add(this, legend, 0, 1, 1, 1);
    }

    private static Function2x2 getDefaultFunction2x2() {
        return new Function2x2() {
            @Override
            public Rectangle.Double getBounds() {
                return new Rectangle2D.Double(0, 0, .5, .5);
            }

            @Override
            public double calcX(double x, double y) {
                return -(y - 0.5) * Math.abs(Math.sin(3 * Math.atan((y - 0.5) / (x - 0.51))));
            }

            @Override
            public double calcY(double x, double y) {
                return (x - 0.5) * Math.abs(Math.cos(2 * Math.atan((y - 0.5) / (x - 0.51))));
            }
        };
    }
}
