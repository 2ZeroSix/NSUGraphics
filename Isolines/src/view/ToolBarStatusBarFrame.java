package view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class ToolBarStatusBarFrame extends JFrame implements MouseListener, ContainerListener {

    public JToolBar getToolBar() {
        return toolBar;
    }

    private JToolBar toolBar;

    public JPanel getWorkWindow() {
        return workWindow;
    }

    private JPanel workWindow;
    private JLabel statusLabel;

    @Override
    public Component add(Component component) {
        if (workWindow != null) {
            return workWindow.add(component);
        } else {
            return super.add(component);
        }
    }

    @Override
    public Component add(String s, Component component) {
        if (workWindow != null) {
            return workWindow.add(s, component);
        } else {
            return super.add(s, component);
        }
    }

    @Override
    public Component add(Component component, int i) {
        if (workWindow != null) {
            return workWindow.add(component, i);
        } else {
            return super.add(component, i);
        }
    }

    @Override
    public void add(Component component, Object o) {
        if (workWindow != null) {
            workWindow.add(component, o);
        } else {
            super.add(component, o);
        }
    }

    @Override
    public void add(Component component, Object o, int i) {
        if (workWindow != null) {
            workWindow.add(component, o, i);
        } else {
            super.add(component, o, i);
        }
    }

    public ToolBarStatusBarFrame(String s) {
        super(s);
        addMouseListeners(this);
        setLayout(new BorderLayout());
        initWorkWindow();
        initToolBar();
        initStatusBar();
    }

    private void initWorkWindow() {
        workWindow = new JPanel();
        super.add(workWindow, BorderLayout.CENTER);
    }

    protected void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        super.add(toolBar, BorderLayout.PAGE_START);
    }

    private void initStatusBar() {
        Box statusBar = new Box(BoxLayout.X_AXIS);
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        super.add(statusBar, BorderLayout.PAGE_END);
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Serif", Font.PLAIN , 12));
        statusBar.setPreferredSize(new Dimension(getWidth(), 20));
        statusBar.add(statusLabel);
    }

    public void setStatus(String status) {
        if (!Objects.equals(status, statusLabel.getText())) {
            this.statusLabel.setText(status);
            this.statusLabel.repaint();
        }
    }

    private void setStatus(Component component) {
        if (JComponent.class.isAssignableFrom(component.getClass())) {
            setStatus(((JComponent)component).getToolTipText());
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        setStatus(mouseEvent.getComponent());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        setStatus(mouseEvent.getComponent());

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        setStatus(mouseEvent.getComponent());
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        setStatus(mouseEvent.getComponent());
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        setStatus((String) null);
    }


    protected void add(Container container, JComponent component, int x, int y, int width, int height) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = width;
        c.gridheight = height;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5 , 5);
        container.add(component, c);
    }

    @Override
    public void componentAdded(ContainerEvent containerEvent) {
        addMouseListeners(containerEvent.getChild());
    }

    @Override
    public void componentRemoved(ContainerEvent containerEvent) {
        removeMouseListeners(containerEvent.getChild());
    }


    private void addMouseListeners(Component component) {
        component.addMouseListener(this);
        if (Container.class.isAssignableFrom(component.getClass())) {
            for (Component child : ((Container) component).getComponents()) {
                addMouseListeners(child);
            }
            ((Container)component).addContainerListener(this);
        }

    }

    private void removeMouseListeners(Component component) {
        component.removeMouseListener(this);
        if (Container.class.isAssignableFrom(component.getClass())) {
            for (Component child : ((Container) component).getComponents()) {
                removeMouseListeners(child);
            }
        }

    }
}
