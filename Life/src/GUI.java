import life_hexagon.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GUI extends JFrame {

    public static void main(String[] args) {
        new GUI();
    }
    GUI() {
        super("GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
//        setMinimumSize(new Dimension(800, 600));
        JScrollPane scrollPane = new JScrollPane();
        setContentPane(scrollPane);
        Model model = new Model();
        FieldView fieldView = new FieldView();
        MyImage image = new MyImage(900, 900);
        Hexagon hexagon = new Hexagon(400, 400, 200, 1);
        hexagon.draw(image);
        scrollPane.add(new JLabel(new ImageIcon(image)));
/*
        {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    MyImage image = new MyImage(graphics.getClipBounds().width, graphics.getClipBounds().height);
                    hexagon .setX((int)(0.5f * graphics.getClipBounds().width))
                            .setY((int)(0.5f * graphics.getClipBounds().height));
                    hexagon.setRadius((int)(0.25f * Integer.min(graphics.getClipBounds().height, graphics.getClipBounds().width)));
                    hexagon.draw(image);
                    graphics.drawImage(image, 0, 0, null);
//                    super.paintComponent(graphics);
                }
            };
//            JPanel panel = new FieldView();// {
//                @Override
//                protected void paintComponent(Graphics graphics) {
//                    hexagon.setCenter(new Point(
//                            (int)(0.5f * graphics.getClipBounds().width),
//                            (int)(0.5f * graphics.getClipBounds().height)));
//                    hexagon.setRadius((int)(0.25f * Integer.min(graphics.getClipBounds().height, graphics.getClipBounds().width)));
//                    super.paintComponent(graphics);
//                }
//            };
            panel.setLayout(new OverlayLayout(panel));
//            panel.add(hexagon);
            setContentPane(panel);
        }
*/
        {
            JMenuBar menuBar = new JMenuBar();
            {
                JMenu file = new JMenu("File");
                {
                    JMenuItem exit = new JMenuItem("Exit");
                    exit.addActionListener(actionEvent -> System.exit(0));
                    file.add(exit);
                }
                menuBar.add(file);
            }
            {
                JMenu about = new JMenu("About");
                menuBar.add(about);
            }
            setJMenuBar(menuBar);
        }


        pack();
        setVisible(true);
    }
}
