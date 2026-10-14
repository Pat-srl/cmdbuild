/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.reverse;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static java.util.Arrays.asList;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import org.cmdbuild.utils.cad.dxfparser.DxfArcImpl;
import org.cmdbuild.utils.cad.dxfparser.DxfExtendedDataImpl;
import org.cmdbuild.utils.cad.dxfparser.DxfVertexImpl;
import org.cmdbuild.utils.cad.dxfparser.model.DxfArc;
import org.cmdbuild.utils.cad.dxfparser.model.DxfEntity;
import org.cmdbuild.utils.cad.dxfparser.model.DxfVertex;
import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.cad.model.CadPoint.point;
import org.cmdbuild.utils.cad.model.CadPolyline;
import org.cmdbuild.utils.cad.model.CadRectangle;
import static org.cmdbuild.utils.cad.model.CadRectangle.rectangle;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CadGeometryUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static byte[] plotToPng(int width, int height, boolean allowDistorsion, CadPolyline... items) {
        return plotToPng(width, height, allowDistorsion, asList(items));
    }

    public static byte[] plotToPng(int width, int height, boolean allowDistorsion, Collection<CadPolyline> items) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            double minX = items.stream().flatMap(p -> p.getVertexes().stream()).mapToDouble(CadPoint::getX).min().getAsDouble(),
                    minY = items.stream().flatMap(p -> p.getVertexes().stream()).mapToDouble(CadPoint::getY).min().getAsDouble(),
                    maxX = items.stream().flatMap(p -> p.getVertexes().stream()).mapToDouble(CadPoint::getX).max().getAsDouble(),
                    maxY = items.stream().flatMap(p -> p.getVertexes().stream()).mapToDouble(CadPoint::getY).max().getAsDouble();

            double scaleX, scaleY;
            if (allowDistorsion) {
                scaleX = width * 0.9 / (maxX - minX);
                scaleY = height * 0.9 / (maxY - minY);
            } else {
                scaleX = scaleY = Math.min(width * 0.9 / (maxX - minX), height * 0.9 / (maxY - minY));
            }
            double offsetX = -minX + width * 0.05 / scaleX, offsetY = -minY + height * 0.05 / scaleY;

            items.forEach(p -> {
                Color lineColor = new Color(new Random().nextInt()), labelColor = Color.BLACK;
                AtomicInteger i = new AtomicInteger(1);
                p.getLines().forEach(l -> {
                    graphics.setColor(lineColor);
                    graphics.draw(new Line2D.Double((l.getLeft().getX() + offsetX) * scaleX, (l.getLeft().getY() + offsetY) * scaleY, (l.getRight().getX() + offsetX) * scaleX, (l.getRight().getY() + offsetY) * scaleY));
                    graphics.setColor(labelColor);
                    graphics.setFont(new Font("Courier", Font.BOLD, max(10, image.getWidth() / 50)));
                    graphics.drawString(format("%s", i.getAndIncrement()), (float) (((l.getLeft().getX() + l.getRight().getX()) / 2 + offsetX) * scaleX), (float) (((l.getLeft().getY() + l.getRight().getY()) / 2 + offsetY) * scaleY));
                });
            });

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static double normalizeAngle(double angle) {
        if (angle >= 360) {
            return normalizeAngle(angle - 360);
        } else if (angle < 0) {
            return normalizeAngle(angle + 360);
        } else {
            return angle;
        }
    }

    public static CadPolyline arcToPolyline(DxfArc arc) {
        return arcToPolyline(arc, 32);
    }

    public static CadPolyline getBBoxOfMultipleEntities(List<DxfEntity> dxfEntities) {
        checkArgument(!dxfEntities.isEmpty());
        List<CadPoint> vertexes = list();
        dxfEntities.forEach(e -> {
            vertexes.addAll(e.getPerimeter().getVertexes());
        });
        Double minX = null, minY = null, maxX = null, maxY = null;
        for (int i = 0; i < vertexes.size(); i++) {
            if (minX == null) {
                minX = vertexes.get(i).getX();
                maxX = vertexes.get(i).getX();
                minY = vertexes.get(i).getY();
                maxY = vertexes.get(i).getY();
            }
            if (vertexes.get(i).getX() < minX) {
                minX = vertexes.get(i).getX();
            }
            if (vertexes.get(i).getX() > maxX) {
                maxX = vertexes.get(i).getX();
            }
            if (vertexes.get(i).getY() < minY) {
                minY = vertexes.get(i).getY();
            }
            if (vertexes.get(i).getY() > maxY) {
                maxY = vertexes.get(i).getY();
            }
        }
        List<CadPoint> bBoxVertexes = list();
        bBoxVertexes.add(new CadPoint(minX, minY));
        bBoxVertexes.add(new CadPoint(maxX, minY));
        bBoxVertexes.add(new CadPoint(maxX, maxY));
        bBoxVertexes.add(new CadPoint(minX, maxY));
        bBoxVertexes.add(new CadPoint(minX, minY));
        return new CadPolyline(list(getBoundingBox(bBoxVertexes).getCenter()));
    }

    public static CadPolyline arcToPolyline(DxfArc arc, int n) {
        double start = normalizeAngle(arc.getStartAngle()), end = normalizeAngle(arc.getEndAngle());
        LOGGER.debug("convert arc = {} to polyline with {} points", arc, n);
        List<CadPoint> points = list();
        double step = ((start == end) ? 360 : ((end + 360 - start) % 360)) / n;
        LOGGER.trace("step = {}", step);
        for (int i = 0; i <= n; i++) {
            double deg = start + i * step,
                    a = PI / 2 - deg * PI / 180,
                    x = arc.getCenter().getX() + cos(a) * arc.getRadius(),
                    y = arc.getCenter().getY() + sin(a) * arc.getRadius();
            LOGGER.trace("a = {} ({}) x = {} y = {}", a, deg, x, y);
            points.add(point(x, y));
        }
        return new CadPolyline(points);
    }

    public static List<DxfVertex> arcFromBulge(DxfVertex v1, DxfVertex v2, double bulge) {
        return arcFromBulge(v1, v2, bulge, 32);
    }

    public static List<DxfVertex> arcFromBulge(DxfVertex v1, DxfVertex v2, double bulge, int n) {
        if (bulge == 0d) {
            return singletonList(v1);
        } else {
            LOGGER.debug("processing arc-bulge with v1 = {}, v2 = {}, bulge = {}", v1, v2, bulge);
            boolean pos = bulge > 0;
            double angle = abs(4 * atan(bulge)),
                    chord = distance(v1, v2),
                    sagitta = chord * abs(bulge) / 2,
                    radius = (chord * chord / 4 + sagitta * sagitta) / (2 * sagitta),
                    direction = getDirection(v1, v2);
            DxfVertex mid = mid(v1, v2),
                    center = findPoint(mid, direction + (pos ? PI / 2 : -PI / 2), radius - sagitta);
            LOGGER.debug("processing arc-bulge with angle = {}, radius = {}, center = {}", angle, radius, center);
            double startAngle = getDirectionAcadDeg(center, v1), endAngle = getDirectionAcadDeg(center, v2);
            List<DxfVertex> list = list(arcToPolyline(new DxfArcImpl(point(center.getX(), center.getY()), pos ? endAngle : startAngle, pos ? startAngle : endAngle, radius, "_", new DxfExtendedDataImpl()), n)
                    .getVertexes()).map(p -> DxfVertexImpl.builder().withX(p.getX()).withY(p.getY()).build());
            if (pos) {
                list = reverse(list);
            }
            return list.subList(0, n);
        }
    }

    public static CadRectangle getBoundingBox(CadPolyline polilyne) {
        return getBoundingBox(polilyne.getVertexes());
    }

    public static CadRectangle getBoundingBox(Iterable<CadPoint> points) {
        return getBoundingBox(points.iterator());
    }

    public static CadRectangle getBoundingBox(Iterator<CadPoint> points) {
        CadPoint point = points.next();
        double minX = point.getX(), minY = point.getY(), maxX = point.getX(), maxY = point.getY();
        while (points.hasNext()) {
            point = points.next();
            minX = min(minX, point.getX());
            minY = min(minY, point.getY());
            maxX = max(maxX, point.getX());
            maxY = max(maxY, point.getY());
        }
        return rectangle(minX, minY, maxX, maxY);

    }

    public static double getSurfaceArea(CadPolyline polyline) {
        return getSurfaceArea(polyline.getVertexes());
    }

    public static double getSurfaceArea(List<CadPoint> vertexes) {
        return abs(getSurfaceAreaSigned(vertexes));
    }

    public static CadPoint getCenter(CadPolyline polyline) {
        return getCenter(polyline.getVertexes());
    }

    public static CadPoint getCenter(List<CadPoint> vertexes) {
        if (vertexes.size() == 1) {
            return getOnlyElement(vertexes);
        } else {
            BigDecimal x = BigDecimal.ZERO, y = BigDecimal.ZERO, area6 = BigDecimal.valueOf(getSurfaceAreaSigned(vertexes)).multiply(BigDecimal.valueOf(6));
            if (area6.compareTo(BigDecimal.ZERO) == 0) {
                return point((vertexes.stream().mapToDouble(CadPoint::getX).max().getAsDouble() + vertexes.stream().mapToDouble(CadPoint::getX).min().getAsDouble()) / 2d, (vertexes.stream().mapToDouble(CadPoint::getY).max().getAsDouble() + vertexes.stream().mapToDouble(CadPoint::getY).min().getAsDouble()) / 2d);
            } else {
                List<CadPoint> list = list(vertexes).with(vertexes.get(0));
                for (int i = 0; i < list.size() - 1; i++) {
                    BigDecimal xi = BigDecimal.valueOf(list.get(i).getX()),
                            xi1 = BigDecimal.valueOf(list.get(i + 1).getX()),
                            yi = BigDecimal.valueOf(list.get(i).getY()),
                            yi1 = BigDecimal.valueOf(list.get(i + 1).getY());
                    x = x.add(xi.add(xi1).multiply(xi.multiply(yi1).subtract(xi1.multiply(yi))));
                    y = y.add(yi.add(yi1).multiply(xi.multiply(yi1).subtract(xi1.multiply(yi))));
                }
                CadPoint center = point(x.divide(area6, RoundingMode.HALF_UP).doubleValue(), y.divide(area6, RoundingMode.HALF_UP).doubleValue());
                LOGGER.debug("center of {} is {}", lazyString(() -> new CadPolyline(vertexes)), center);
                return center;
            }
        }
    }

    public static CadPoint getCenter(CadPoint... vertexes) {
        return getCenter(list(vertexes));
    }

    private static double getSurfaceAreaSigned(List<CadPoint> vertexes) {
        if (vertexes.size() == 1) {
            return 0;
        } else {
            BigDecimal sum = BigDecimal.ZERO;
            List<CadPoint> list = list(vertexes).with(vertexes.get(0));
            for (int i = 0; i < list.size() - 1; i++) {
                BigDecimal xi = BigDecimal.valueOf(list.get(i).getX()),
                        xi1 = BigDecimal.valueOf(list.get(i + 1).getX()),
                        yi = BigDecimal.valueOf(list.get(i).getY()),
                        yi1 = BigDecimal.valueOf(list.get(i + 1).getY());
                sum = sum.add(xi.multiply(yi1).subtract(xi1.multiply(yi)));
            }
            sum = sum.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
            LOGGER.debug("surface of {} is {}", lazyString(() -> new CadPolyline(list)), sum);
            return sum.doubleValue();
        }
    }

    public static boolean contains(CadPolyline polyline, CadPoint point) {
        if (polyline instanceof CadRectangle cadRectangle) {
            return contains(cadRectangle, point);
        } else if (!getBoundingBox(polyline).contains(point)) {
            return false;
        } else {
            return toGeometry(polyline).covers(toGeometry(point));
        }
    }

    public static boolean contains(CadPolyline polyline, CadPolyline inner) {
        return toGeometry(polyline).covers(toGeometry(inner));
    }

    public static boolean intersects(CadPolyline polyline, CadPolyline inner) {
        return toGeometry(polyline).intersects(toGeometry(inner));
    }

    public static boolean contains(CadPolyline polyline, CadPolyline inner, double overlapAmount) {
        Geometry first = toGeometry(polyline), other = toGeometry(inner);
        return first.covers(other) || (first.intersects(other) && first.intersection(other).getArea() > other.getArea() * overlapAmount);
    }

    public static double intersectionSize(CadPolyline first, CadPolyline other) {
        return toGeometry(first).intersection(toGeometry(other)).getArea();
    }

    private static Geometry toGeometry(CadPolyline polyline) {
        if (polyline.isPoint()) {
            return toGeometry(polyline.getCenter());
        } else {
            return JTSFactoryFinder.getGeometryFactory().createPolygon(list(polyline.getVertexes()).with(polyline.getVertexes().get(0)).map(p -> new Coordinate(p.getX(), p.getY())).toArray(Coordinate[]::new));
        }
    }

    private static Geometry toGeometry(CadPoint point) {
        return JTSFactoryFinder.getGeometryFactory().createPoint(new Coordinate(point.getX(), point.getY()));
    }

    public static boolean contains(CadRectangle rectangle, CadPoint point) {
        return isBetween(point.getX(), rectangle.getX1(), rectangle.getX2()) && isBetween(point.getY(), rectangle.getY1(), rectangle.getY2());
    }

    public static boolean isBetween(double x, double a, double b) {
        return (a <= x && x <= b) || (b <= x && x <= a);
    }

    public static double distance(DxfVertex v1, DxfVertex v2) {
        double a = v1.getX() - v2.getX(), b = v1.getY() - v2.getY();
        return sqrt(a * a + b * b);
    }

    public static DxfVertex mid(DxfVertex v1, DxfVertex v2) {
        return new DxfVertexImpl((v1.getX() + v2.getX()) / 2, (v1.getY() + v2.getY()) / 2);
    }

    public static double getDirection(DxfVertex from, DxfVertex to) {
        if (from.getY() == to.getY()) {
            return from.getX() < to.getX() ? 0 : PI;
        } else {
            double a = atan((to.getX() - from.getX()) / (to.getY() - from.getY()));
            return to.getY() > from.getY() ? PI / 2 - a : PI * 3 / 2 - a;
        }
    }

    public static double getDirectionAcadDeg(DxfVertex from, DxfVertex to) {
        return (PI / 2 - getDirection(from, to)) * 180 / PI;
    }

    public static DxfVertex findPoint(DxfVertex point, double direction, double distance) {
        return new DxfVertexImpl(point.getX() + cos(direction) * distance, point.getY() + sin(direction) * distance);
    }

}
