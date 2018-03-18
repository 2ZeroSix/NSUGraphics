package life_hexagon.view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class StatusBarFrame extends JFrame implements MouseListener {

    public JPanel getWorkWindow() {
        return workWindow;
    }

    public StatusBarFrame setWorkWindow(JPanel workWindow) {
        this.workWindow = workWindow;
        return this;
    }

    private JPanel workWindow;
    private JLabel statusLabel;

    @Override
    public Component add(Component component) {
        if (workWindow != null) {
            if (JComponent.class.isAssignableFrom(component.getClass())) {
                component.addMouseListener(this);
            }
            return workWindow.add(component);
        } else {
            return super.add(component);
        }
    }

    @Override
    public Component add(String s, Component component) {
        if (workWindow != null) {
            if (JComponent.class.isAssignableFrom(component.getClass())) {
                component.addMouseListener(this);
            }
            return workWindow.add(s, component);
        } else {
            return super.add(s, component);
        }
    }

    @Override
    public Component add(Component component, int i) {
        if (workWindow != null) {
            if (JComponent.class.isAssignableFrom(component.getClass())) {
                component.addMouseListener(this);
            }
            return workWindow.add(component, i);
        } else {
            return super.add(component, i);
        }
    }

    @Override
    public void add(Component component, Object o) {
        if (workWindow != null) {
            if (JComponent.class.isAssignableFrom(component.getClass())) {
                component.addMouseListener(this);
            }
            workWindow.add(component, o);
        } else {
            super.add(component, o);
        }
    }

    @Override
    public void add(Component component, Object o, int i) {
        if (workWindow != null) {
            if (JComponent.class.isAssignableFrom(component.getClass())) {
                component.addMouseListener(this);
            }
            workWindow.add(component, o, i);
        } else {
            super.add(component, o, i);
        }
    }

    public StatusBarFrame(String s) {
        super(s);
        setLayout(new BorderLayout());
        workWindow = new JPanel();
        super.add(workWindow, BorderLayout.CENTER);
        initStatusBar();
    }

    private void initStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        super.add(statusBar, BorderLayout.PAGE_END);
        statusBar.setPreferredSize(new Dimension(getWidth(), 16));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusLabel = new JLabel();
        statusLabel.setFont(Font.getFont("Serif"));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel);
    }

    private void setStatus(String status) {
        if (!Objects.equals(status, statusLabel.getText())) {
            this.statusLabel.setText(status);
            this.statusLabel.repaint();
        }
    }

    private void setStatus(JComponent component) {
        String tooltip = component.getToolTipText();
        setStatus(tooltip);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        setStatus((JComponent) mouseEvent.getComponent());
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        setStatus((String) null);
    }
}
