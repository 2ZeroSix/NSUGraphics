package model;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Scene {
    private final Camera camera = new Camera();
    public final ListProperty<Object3D> object3DS = new SimpleListProperty<>(new ObservableListWrapper<>(new ArrayList<>()));
    private final SceneSettings settings;
    public final Property<Dimension> displaySize = new SimpleObjectProperty<>(new Dimension(800, 600));
    public final Binding<Matrix4x4.ViewPort> viewPort = Bindings.createObjectBinding(() -> new Matrix4x4.ViewPort(displaySize.getValue().width, displaySize.getValue().height, camera.frontWidth.get(), camera.frontHeight.get()),
            camera.frontWidth, camera.frontHeight, displaySize);
    public final ObjectBinding<BufferedImage> canvas;
    public final Property<BSpline3D> selected = new SimpleObjectProperty<>();
    public final Property<Matrix4x4> globalRotation = new SimpleObjectProperty<>(new Matrix4x4.Rotation(0, 0, 0));//(Math.PI / 4, Math.PI / 4, Math.PI / 4);
    public Scene(SceneSettings settings) {
        this.settings = settings;
        int counter[] = new int[]{0};
        canvas = Bindings.createObjectBinding(() -> {
                    Dimension size = displaySize.getValue();
                    if (counter[0]++ % 25 == 0) {
                        System.gc();
                    }
                    double multiplier = Math.min(size.width, size.height);
                    if (size.width <= 0 || size.height <= 0)
                        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                    double width = (multiplier * camera.frontWidth.get() / Math.max(camera.frontWidth.get(), camera.frontHeight.get()));
                    double height = (multiplier * camera.frontHeight.get() / Math.max(camera.frontWidth.get(), camera.frontHeight.get()));
                    if ((double)size.width / width <= (double)size.height / height) {
                        height *= size.width / width;
                        width *= size.width / width;
                    } else {
                        width *= size.height / height;
                        height *= size.height / height;
                    }
                    BufferedImage image = new BufferedImage((int)width, (int) height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = image.createGraphics();
                    g.setColor(settings.backgroundColor.getValue());
                    g.fillRect(0, 0, (int)width - 1, (int)height - 1);
                    computeMatrixToScene();
                    object3DS.forEach(object3D -> {
                        Matrix4x4 world = object3D.getWorldMatrix();
                        Matrix4x4 wvp = camera.viewProj.getValue()
                                .mult(camera.orientation.getValue().getTransposed())
                                .mult(globalRotation.getValue())
                                .mult(object3D.viewSpaceRotation.getValue())
                                .mult(camera.orientation.getValue())
                                .mult(world);
                        if (object3D.equals(selected.getValue())) {
                            g.setStroke(new BasicStroke(2));
                        } else {
                            g.setStroke(new BasicStroke(1));
                        }
                        object3D.getEdges().forEach(edge -> {
                            Vector4 start = wvp.mult(edge.getPoints()[0]).normalize();
                            Vector4 end = wvp.mult(edge.getPoints()[1]).normalize();
                            Point2D.Double[] p = projectEdge(start, end);
                            if (p != null) {
                                Vector4 from = viewPort.getValue().mult(new Vector4(p[0].getX(), p[0].getY()));
                                Vector4 to = viewPort.getValue().mult(new Vector4(p[1].getX(), p[1].getY()));
                                int x1 = (int) Math.round((from.getX()));
                                int y1 = image.getHeight() - (int) Math.round((from.getY()));
                                int x2 = (int) Math.round((to.getX()));
                                int y2 = image.getHeight() - (int) Math.round((to.getY()));
                                g.setColor(edge.getColor());
                                g.drawLine(x1, y1, x2, y2);
                            }
                        });
                    });
                    g.dispose();
                    return image;
                },
                settings.backgroundColor, settings.legendColor,
                camera.viewProj, displaySize, object3DS, globalRotation, selected);
        settings.zn.bindBidirectional(camera.frontClip);
        settings.zf.bindBidirectional(camera.backClip);
        settings.sw.bindBidirectional(camera.frontWidth);
        settings.sh.bindBidirectional(camera.frontHeight);
        Object3D brick = Object3D.getBrick(1, 1, 1);
        object3DS.add(brick);
        brick.edges.forEach(edge -> settings.legendColor.addListener((observable, oldValue, newValue) -> {
            edge.setColor(newValue);
            canvas.invalidate();
        }));
        brick.addObserver((o, arg) -> canvas.invalidate());
    }

    private void computeMatrixToScene() {
        object3DS.stream().filter(object3D -> object3D instanceof BSpline3D).forEach(object3D -> {
            object3D.scale.setValue(new Matrix4x4.Diagonal());
            object3D.viewSpaceCenter.setValue(new Vector4());
        });
        Vector4[] minMaxVectors = object3DS.stream()
                .flatMap(obj3D -> Arrays.stream(obj3D.getMinMaxPoints()))
                .toArray(Vector4[]::new);
        if (minMaxVectors.length == 0) return;

        double[] minMaxX = { minMaxVectors[0].getX(), minMaxVectors[1].getX() };
        double[] minMaxY = { minMaxVectors[0].getY(), minMaxVectors[1].getY() };
        double[] minMaxZ = { minMaxVectors[0].getZ(), minMaxVectors[1].getZ() };
        Arrays.stream(minMaxVectors)
                .forEach(vector -> {
                    minMaxX[0] = Double.min(vector.getX(), minMaxX[0]);
                    minMaxX[1] = Double.max(vector.getX(), minMaxX[1]);

                    minMaxY[0] = Double.min(vector.getY(), minMaxY[0]);
                    minMaxY[1] = Double.max(vector.getY(), minMaxY[1]);

                    minMaxZ[0] = Double.min(vector.getZ(), minMaxZ[0]);
                    minMaxZ[1] = Double.max(vector.getZ(), minMaxZ[1]);
                });

        Vector4 center = new Vector4((minMaxX[1] + minMaxX[0]) / 2,
                (minMaxY[1] + minMaxY[0]) / 2,
                (minMaxZ[1] + minMaxZ[0]) / 2);

        double sizeX = Double.max(Math.abs(minMaxX[0] - center.getX()),
                Math.abs(minMaxX[1] - center.getX()));
        double sizeY = Double.max(Math.abs(minMaxY[0] - center.getY()),
                Math.abs(minMaxY[1] - center.getY()));
        double sizeZ = Double.max(Math.abs(minMaxZ[0] - center.getZ()),
                Math.abs(minMaxZ[1] - center.getZ()));
        double size = Double.max(sizeX, Double.max(sizeY, sizeZ));
        if (size == 0)
            size = 1;
        double finalSize = size;
        object3DS.stream().filter(object3D -> object3D instanceof BSpline3D).forEach(object3D -> {
            object3D.scale.setValue(new Matrix4x4.Scale(1. / finalSize));
            object3D.viewSpaceCenter.setValue(new Matrix4x4.Scale(-1).mult(center));
        });

//        return Matrix4x4.identity()
//                .shift(center.resize(-1))
//                .resize(1 / size);
    }

    private Point2D.Double[] projectEdge(Vector4 start, Vector4 end) {
        double[] x = {start.getX(), end.getX()};
        double[] y = {start.getY(), end.getY()};
        double[] z = {start.getZ(), end.getZ()};

        try {
            clipping(x, y, z, -1, 1);
            clipping(y, x, z, -1, 1);
            clipping(z, x, y, 0, 1);
        } catch (Exception e) {
            return null;
        }

        return new Point2D.Double[]{new Point2D.Double(x[0], y[0]), new Point2D.Double(x[1], y[1])};
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
//            main[iMin] = min;
            off1[iMin] = off1[iMin] - k * (off1[1] - off1[0]);
            off2[iMin] = off2[iMin] - k * (off2[1] - off2[0]);
        }

        if (main[iMax] > max) {
            double k = (main[iMax] - max) / (main[1] - main[0]);
//            main[iMax] = max;
            off1[iMax] = off1[iMax] - k * (off1[1] - off1[0]);
            off2[iMax] = off2[iMax] - k * (off2[1] - off2[0]);
        }
    }

    public BSpline3D addBSpline() {
        BSpline3D bspline3D = new BSpline3D(settings);
        object3DS.add(bspline3D);
        bspline3D.addObserver((observable, o) -> canvas.invalidate());
        return bspline3D;
    }

    public void removeBSpline(int i) {
        object3DS.remove(object3DS.stream().filter(o -> o instanceof BSpline3D).skip(i).findFirst().get());
    }

    public List<BSpline3D> getBSplines3D() {
        return object3DS.stream()
                .filter(object3D -> object3D instanceof BSpline3D)
                .map(o -> (BSpline3D) o)
                .collect(Collectors.toList());
    }

    public SceneSettings getSettings() {
        return settings;
    }

    public void load(Scanner scanner) {
        scanner.useDelimiter("(([\\v\\h])|([\\v\\h]*(//.*[\\v])[\\v\\h]*))+");
        scanner.useLocale(Locale.US);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int k = scanner.nextInt();
        double a = scanner.nextDouble();
        double b = scanner.nextDouble();
        int c = scanner.nextInt();
        int d = scanner.nextInt();

        double zn = scanner.nextDouble();
        double zf = scanner.nextDouble();
        double sw = scanner.nextDouble();
        double sh = scanner.nextDouble();
        Matrix4x4 rotation = new Matrix4x4.Diagonal();
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                rotation.set(row, column, scanner.nextDouble());
            }
        }
        Color backgroundColor = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
        int splinesCount = scanner.nextInt();
        List<BSpline3D> splines = new ArrayList<>(splinesCount);
        for (int i = 0; i < splinesCount; ++i) {
            BSpline3D spline = new BSpline3D(settings);
            spline.getSpline().color.setValue(new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt()));
            spline.center.setValue(new Vector4(scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble()));
            Matrix4x4 splineRotation = new Matrix4x4.Diagonal();
            for (int row = 0; row < 3; ++row) {
                for (int column = 0; column < 3; ++column) {
                    splineRotation.set(row, column, scanner.nextDouble());
                }
            }
            int pointsCount = scanner.nextInt();
            for (int p = 0; p < pointsCount; ++p) {
                spline.getSpline().addPoint(new Point2D.Double(scanner.nextDouble(), scanner.nextDouble()));
            }
            splines.add(spline);
        }
        settings.n.setValue(n);
        settings.m.setValue(m);
        settings.k.setValue(k);
        settings.a.setValue(a);
        settings.b.setValue(b);
        settings.c.setValue(c);
        settings.d.setValue(d);
        settings.zn.setValue(zn);
        settings.zf.setValue(zf);
        settings.sw.setValue(sw);
        settings.sh.setValue(sh);
        globalRotation.setValue(rotation);
        settings.backgroundColor.setValue(backgroundColor);
        object3DS.removeIf(object3D -> object3D instanceof BSpline3D);
        object3DS.addAll(splines);
        splines.forEach(bSpline3D -> bSpline3D.addObserver((o, arg) -> canvas.invalidate()));
    }

    public void save(PrintStream stream) {
        stream.format(Locale.US, "%d %d %d %f %f %d %d\n",
                settings.n.get(),
                settings.m.get(),
                settings.k.get(),
                settings.a.get(),
                settings.b.get(),
                settings.c.get(),
                settings.d.get()
        );
        stream.format(Locale.US, "%f %f %f %f\n",
                settings.zn.get(),
                settings.zf.get(),
                settings.sw.get(),
                settings.sh.get()
        );
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                stream.print(globalRotation.getValue().get(row, column) + " ");
            }
            stream.println();
        }
        stream.format(Locale.US, "%d %d %d\n",
                settings.backgroundColor.getValue().getRed(),
                settings.backgroundColor.getValue().getGreen(),
                settings.backgroundColor.getValue().getBlue()
        );
        List<BSpline3D> splines = object3DS.stream()
                .filter(object3D -> object3D instanceof BSpline3D)
                .map(o -> (BSpline3D)o)
                .collect(Collectors.toList());
        stream.println(splines.size());
        for (BSpline3D spline3D : splines) {
            stream.format(Locale.US, "%d %d %d\n",
                    spline3D.getSpline().color.getValue().getRed(),
                    spline3D.getSpline().color.getValue().getGreen(),
                    spline3D.getSpline().color.getValue().getBlue()
            );
            stream.format(Locale.US, "%f %f %f\n",
                    spline3D.center.getValue().getX(),
                    spline3D.center.getValue().getY(),
                    spline3D.center.getValue().getZ()
                    );
            for (int row = 0; row < 3; ++row) {
                for (int column = 0; column < 3; ++column) {
                    stream.print(spline3D.rotation.getValue().get(row, column) + " ");
                }
                stream.println();
            }
            stream.println(spline3D.getSpline().points.size());
            spline3D.getSpline().points.stream()
                    .map(Property::getValue)
                    .forEach(point2D -> stream.format(Locale.US, "%f %f\n", point2D.getX(), point2D.getY()));
        }
    }
}
