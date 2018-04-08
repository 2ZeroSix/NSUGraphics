package view.panels;

import model.Function2x2;
import model.IsolineModel;
import model.observers.IsolineModelObserver;
import view.ContainerUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class IsolinesPanel extends JPanel implements IsolineModelObserver {
    private Legend legend;
    private Face face;

    @Override
    public void update(Function2x2 function2x2) {

    }

    @Override
    public void update(IsolineModel.Parameters parameters) {

    }

    @Override
    public void update(IsolineModel isolineModel) {

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

    public IsolinesPanel(Function2x2 function) {
        super(new GridBagLayout());
        face = new Face();
        legend = new Legend();
        ContainerUtils.add(this, face, 0, 0, 1, 1);
        ContainerUtils.add(this, legend, 0, 1, 1, 1);
    }



    private static Function2x2 getDefaultFunction2x2() {
        return new Function2x2() {
            @Override
            public Rectangle.Double getBounds() {
                return new Rectangle.Double(0, 0, .5, .5);
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
