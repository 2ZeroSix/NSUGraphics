package ru.nsu.ccfit.lukin.view.imagePanels;

import ru.nsu.ccfit.lukin.model.ImageUtils;
import ru.nsu.ccfit.lukin.model.observables.FullImageObservable;
import ru.nsu.ccfit.lukin.view.observers.FullImageObserver;
import ru.nsu.ccfit.lukin.view.observers.ImageObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class FullImage extends ImagePanel implements FullImageObservable, MouseListener, MouseMotionListener {
    private boolean selectable = false;
    private Rectangle selection = new Rectangle();
    private Set<FullImageObserver> fullImageObservers = new HashSet<>();
    private BufferedImage fullSizeImage;

    public FullImage(Dimension size) {
        setMaximumSize(size);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));

        addMouseListener(this);
        addMouseMotionListener(this);

//        setSelection(new Point(100, 100));
    }


    public FullImage setImage(BufferedImage image) {
        this.fullSizeImage = image;
        if (image != null) {
            int w = image.getWidth();
            int h = image.getHeight();
            super.setImage(ImageUtils.toBufferedImage(image.getScaledInstance(w > h ? (w > 350 ? 350 : -1) : -1, w > h ? -1 : (h > 350 ? 350 : -1), Image.SCALE_SMOOTH)));
        } else {
            super.setImage(null);
        }
        return this;
    }


    @Override
    public BufferedImage getImage() {
        return this.fullSizeImage;
    }

    private void setSelection(Point p) {
        BufferedImage image = getImage();
        int w = 0;
        int h = 0;
        double multiplier = 1;
        if (image != null) {
            w = image.getWidth(null);
            h = image.getHeight(null);
            multiplier = 350. / (w > h ? w : h);
            if (multiplier > 1.) multiplier = 1.;
        }
        Dimension size = getSize();
        Rectangle selection = new Rectangle();
        selection.x = Integer.max(Integer.min((int) (p.x / multiplier) - size.width / 2, w - size.width), 0);
        selection.width = Integer.max(Integer.min(size.width, w - selection.x), 0);
        selection.y = Integer.max(Integer.min((int) (p.y / multiplier) - size.height / 2, h - size.height), 0);
        selection.height = Integer.max(Integer.min(size.height, h - selection.y), 0);
        setSelection(selection);
    }

    private void setSelection(Rectangle selection) {
        if (selectable) {
            this.selection = selection;
            repaint();
            notifySelection();
        }
    }

    @Override
    public Rectangle getSelection() {
        return this.selection;
    }

    @Override
    public void notifySelectable() {
        for (FullImageObserver fullImageObserver : fullImageObservers) {
            fullImageObserver.updateSelectable(this);
        }
    }

    @Override
    public void notifySelection() {
        for (FullImageObserver fullImageObserver : fullImageObservers) {
            fullImageObserver.updateSelection(this);
        }
    }

    @Override
    public void addImageObserver(ImageObserver imageObserver) {
        if (imageObserver instanceof FullImageObserver) {
            fullImageObservers.add((FullImageObserver) imageObserver);
        }
        super.addImageObserver(imageObserver);
    }

    @Override
    public void removeImageObserver(ImageObserver imageObserver) {
        fullImageObservers.remove(imageObserver);
        super.addImageObserver(imageObserver);
    }

    @Override
    public void clean() {
        super.clean();
        setImage(null);
        setSelectable(false);
        selection = new Rectangle(0, 0, 0, 0);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        setSelection(mouseEvent.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        setSelection(mouseEvent.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        setSelection(mouseEvent.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        if (fullSizeImage != null && selectable && selection.width > 0 && selection.height > 0) {
            int w = fullSizeImage.getWidth();
            int h = fullSizeImage.getHeight();
            double multiplier = 350. / (w > h ? w : h);
            if (multiplier > 1.) multiplier = 1.;
            BufferedImage image = super.getImage();
            BufferedImage rect = new BufferedImage(selection.width, selection.height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < selection.width; ++x) {
                rect.setRGB((int) (x*multiplier), 0, ImageUtils.negativePixel(image.getRGB((int) ((x + selection.x)*multiplier), (int)(selection.y * multiplier))));
                rect.setRGB((int) (x*multiplier), (int) ((selection.height - 1)*multiplier), ImageUtils.negativePixel(image.getRGB((int) ((x + selection.x) * multiplier), (int) ((selection.height - 1 + selection.y) * multiplier))));
            }
            for (int y = 0; y < selection.height; ++y) {
                rect.setRGB(0, (int) (y*multiplier), ImageUtils.negativePixel(image.getRGB((int) (selection.x * multiplier), (int) ((selection.y + y) * multiplier))));
                rect.setRGB((int) ((selection.width - 1)*multiplier), (int) (y*multiplier), ImageUtils.negativePixel(image.getRGB((int) ((selection.width - 1 + selection.x) * multiplier), (int) ((selection.y + y)*multiplier))));
            }
            graphics.drawImage(rect, (int)(selection.x * multiplier), (int) (selection.y * multiplier), null);

//            Graphics2D g2d = (Graphics2D) graphics;
//            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 3, 2, 3}, 0);
//            g2d.setStroke(dashed);
//            g2d.drawRect((int) (selection.x * multiplier), (int) (selection.y * multiplier), (int) (selection.width * multiplier), (int) (selection.height * multiplier));
        }
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }


    public FullImage setSelectable(boolean selectable) {
//        if (!selectable) {
//            setSelection(new Rectangle(0, 0, 0, 0));
//        }
        if (this.selectable != selectable) {
            this.selectable = selectable;
            notifySelectable();
            repaint();
        }
        return this;
    }
}
