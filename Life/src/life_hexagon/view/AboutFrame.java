package life_hexagon.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AboutFrame extends JDialog {
    public AboutFrame(JFrame parent) {
        super(parent, "About", true);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        {
            try {
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 2;
                add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/me.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH))), c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        {
            JLabel label[] = {new JLabel("Life ver. 1.0"),
                    new JLabel("Bogdan Lukin"),
                    new JLabel("FIT 15206")};
            for (int i = 0; i < label.length; ++i) {
                c.gridx = 0;
                c.gridy = i + 1;
                c.gridwidth = 2;
                add(label[i], c);
            }
        }
        pack();
        setResizable(false);
    }
}
