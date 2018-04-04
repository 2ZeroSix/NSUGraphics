package view.buttons;

import actions.IconAction;

import javax.swing.*;
import java.awt.*;

public class IconButton extends JButton {



    public IconButton(IconAction action) {
        super(action);
        setHideActionText( true );
        setName((String) action.getValue(Action.NAME));
//        setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
        setIcon((Icon)action.getValue(IconAction.ICON));
        setSelectedIcon((Icon)action.getValue(IconAction.SELECTED_ICON));
        setDisabledIcon((Icon)action.getValue(IconAction.DISABLED_ICON));
        setPressedIcon((Icon)action.getValue(IconAction.PRESSED_ICON));
        setRolloverIcon((Icon)action.getValue(IconAction.ROLLOVER_ICON));
        setDisabledSelectedIcon((Icon)action.getValue(IconAction.DISABLED_SELECETD_ICON));
        setRolloverSelectedIcon((Icon)action.getValue(IconAction.ROLLOVER_SELECTED_ICON));
    }

}
