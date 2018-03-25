package ru.nsu.ccfit.lukin.view;

import ru.nsu.ccfit.lukin.model.observables.ImageObservable;
import ru.nsu.ccfit.lukin.view.observers.ImageObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class ImagePanel extends JPanel implements ImageObservable{
//    protected final JLabel              iconLabel;
    private BufferedImage image;
    private final Set<ImageObserver>    imageObservers = new HashSet<>();

    protected ImagePanel() {
        ((FlowLayout)getLayout()).setHgap(0);
        ((FlowLayout)getLayout()).setVgap(0);
        ((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);

//        iconLabel = new JLabel(new ImageIcon());
        if (MouseListener.class.isAssignableFrom(this.getClass()))
            /*iconLabel.*/addMouseListener((MouseListener) this);
        if (MouseMotionListener.class.isAssignableFrom(this.getClass()))
            /*iconLabel.*/addMouseMotionListener((MouseMotionListener) this);
//        iconLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        iconLabel.setVerticalAlignment(SwingConstants.TOP);
//        add(iconLabel);
    }

    @Override
    public BufferedImage getImage() {
//        ImageIcon icon = (ImageIcon)iconLabel.getIcon();
//        return (BufferedImage) (icon != null ? (icon).getImage() : null);
        return image;
    }

    @Override
    public void notifyImage() {
        for (ImageObserver imageObserver : imageObservers) {
            imageObserver.updateImage(this);
        }
    }

    @Override
    public void addImageObserver(ImageObserver imageObserver) {
        imageObservers.add(imageObserver);
    }

    @Override
    public void removeImageObserver(ImageObserver imageObserver) {
        imageObservers.remove(imageObserver);
    }

    protected ImagePanel setImage(BufferedImage image) {
//        if (getImage() != image) {
//            iconLabel.setIcon(image != null ? new ImageIcon(image) : null);
//        }
//        iconLabel.repaint();
        this.image = image;
        repaint();
        return this;
    }

    public void clean() {
        setImage(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
