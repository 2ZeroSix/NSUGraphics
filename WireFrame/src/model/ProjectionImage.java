package model;

//import javafx.scene.Camera;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class ProjectionImage extends BufferedImage {

    private final static double FROM = -1;
    private final static double TO   = 1;
    private final Camera camera;

//    private Vector4.Translation translation;
//    private Vector4.Projection projection;

    public ProjectionImage(int width, int height, Matrix4x4 toScene, Camera camera, Object3D Object3D) {
        super(width, height, TYPE_INT_ARGB);
        this.camera = camera;

//        translation = new Vector4.Translation(camera.position, camera.axisX, camera.axisY, camera.axisZ);
//        projection = new Vector4.Projection(camera.getFrontZ(), camera.getBackZ(),
//                camera.getWidth(), camera.getHeight());

        double scaleX = width / (TO - FROM);
        double scaleY = height / (TO - FROM);

        Graphics2D g2D = createGraphics();
//        if (Object3D == FigureMover.getInstance().getFigure())
//            g2D.setStroke(new BasicStroke(2)); TODO

        List<Object3D.Edge> edges = Object3D.getEdges(/*toScene, camera*/);
        for (Object3D.Edge edge : edges) {
            Point2D.Double[] edgeProjection = projectEdge(edge);
            if (edgeProjection == null)
                continue;

            int x1 = (int) Math.round((edgeProjection[0].getX() - FROM) * scaleX);
            int y1 = (int) Math.round((edgeProjection[0].getY() - FROM) * scaleY);
            int x2 = (int) Math.round((edgeProjection[1].getX() - FROM) * scaleX);
            int y2 = (int) Math.round((edgeProjection[1].getY() - FROM) * scaleY);

            g2D.setColor(edge.getColor());

            g2D.drawLine(x1, y1, x2, y2);
        }
    }

    private Point2D.Double[] projectEdge(Object3D.Edge edge) {
        Vector4 from = camera.getViewProj().mult(edge.getPoints()[0]);
        Vector4 to = camera.getViewProj().mult(edge.getPoints()[1]);

        double[] x = { from.getX(), to.getX() };
        double[] y = { from.getY(), to.getY() };
        double[] z = { from.getZ(), to.getZ() };

        try {
            clipping(x, y, z, FROM, TO);
            clipping(y, x, z, FROM, TO);
            clipping(z, x, y, 0, 1);
        } catch (Exception e) {
            return null;
        }

        return new Point2D.Double[]{ new Point2D.Double(x[0], y[0]), new Point2D.Double(x[1], y[1]) };
    }

    private void clipping(double[] main, double[] off1, double[] off2,
                          double min, double max) throws Exception {
        if (main[0] < min && main[1] < min || main[0] > max && main[1] > max) {
            throw new Exception();
        }

        int iMin = (main[0] < main[1]) ? 0 : 1;
        int iMax = 1 - iMin;

        if (main[iMin] < min) {
            double k = (main[iMin] - min) / (main[1] - main[0]);
            off1[iMin] = off1[iMin] - k * (off1[1] - off1[0]);
            off2[iMin] = off2[iMin] - k * (off2[1] - off2[0]);
        }

        if (main[iMax] > max) {
            double k = (main[iMax] - max) / (main[1] - main[0]);
            off1[iMax] = off1[iMax] - k * (off1[1] - off1[0]);
            off2[iMax] = off2[iMax] - k * (off2[1] - off2[0]);
        }
    }
}
