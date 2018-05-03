package view.buttons;

import actions.IconAction;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

public class IconButton extends JButton implements PropertyChangeListener {



    public IconButton(IconAction action) {
        super(action);
        action.addPropertyChangeListener(this);
        setSelected((Boolean) Optional.ofNullable(action.getValue(Action.SELECTED_KEY)).orElse(false));
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
            setSelected((Boolean) evt.getNewValue());
        }
    }
}
