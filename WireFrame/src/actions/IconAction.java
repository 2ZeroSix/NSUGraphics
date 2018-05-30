package actions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.function.Consumer;

public class IconAction extends AbstractAction {
    public static final String ICON = "SwingLargeIconKey";
    public static final String SELECTED_ICON = "SelectedIconKey";
    public static final String DISABLED_ICON = "DisabledIconKey";
    public static final String PRESSED_ICON = "PressedIconKey";
    public static final String ROLLOVER_ICON = "RolloverIconKey";
    public static final String DISABLED_SELECETD_ICON = "DisabledSelectedIconKey";
    public static final String ROLLOVER_SELECTED_ICON = "RolloverSelectedIconKey";
    private static final String[] suffixes = {
            "",
            "-selected",
            "-disabled",
            "-pressed",
            "-rollover",
            "-disabled-selected",
            "-rollover-selected",
    };
    private static final String[] extensions = {
            "png",
            "bmp",
            "svg",
            "jpg",
            "jpeg"
    };

    private final Consumer<Icon>[] iconSetter = (Consumer<Icon>[]) new Consumer[]{
            (Consumer<Icon>) (icon) -> putValue(ICON, icon),
            (Consumer<Icon>) (icon) -> putValue(SELECTED_ICON, icon),
            (Consumer<Icon>) (icon) -> putValue(DISABLED_ICON, icon),
            (Consumer<Icon>) (icon) -> putValue(PRESSED_ICON, icon),
            (Consumer<Icon>) (icon) -> putValue(ROLLOVER_ICON, icon),
            (Consumer<Icon>) (icon) -> putValue(DISABLED_SELECETD_ICON, icon),
            (Consumer<Icon>) (icon) -> putValue(ROLLOVER_SELECTED_ICON, icon),
    };

    private static boolean debug  = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().contains("jdwp");

    public IconAction(String name) {
        this(name, name);
    }
    public IconAction(String name, String description) {
        super(name);
        putValue(AbstractAction.SHORT_DESCRIPTION, description);
        for (int i = 0; i < suffixes.length; ++i) {
            boolean isSet = false;
            for (String extension : extensions) {
                try {
                    iconSetter[i].accept(new ImageIcon(
                            ImageIO.read(getClass()
                                    .getResource("/icons/" + name + suffixes[i] + "." + extension)).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
                    isSet = true;
                } catch (IllegalArgumentException | IOException ex) {
                }
            }
            if (debug && !isSet)
                System.err.println(name + suffixes[i] + " is not specified");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
