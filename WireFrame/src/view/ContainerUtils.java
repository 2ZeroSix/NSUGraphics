package view;

import javax.swing.*;
import java.awt.*;

public enum ContainerUtils {;
    public static void add(Container container, JComponent component, int x, int y, int width, int height) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5 , 5);
        container.add(component, c);
    }
}
