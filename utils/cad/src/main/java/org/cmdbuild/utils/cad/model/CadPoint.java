/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class CadPoint {

    private final double x, y;

    public CadPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static CadPoint point(double x, double y) {
        return new CadPoint(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public CadPoint transform(AffineTransform transform) {
        Point2D.Double point = new Point2D.Double(x, y);
        transform.transform(point, point);
        return point(point.getX(), point.getY());
    }

    public CadPoint getOffsetFrom(CadPoint otherPoint) {
        return point(x - otherPoint.getX(), y - otherPoint.getY());
    }

    public CadPoint addOffset(CadPoint offset) {
        return offset.isZero() ? this : point(offset.getX() + x, offset.getY() + y);
    }

    public CadPoint scale(double scaleX, double scaleY) {
        return scaleX == 1 && scaleY == 1 ? this : point(x * scaleX, y * scaleY);
    }

    public CadPoint rotate(Double rotationAngle) {
        return rotationAngle == 0 ? this : point(x * Math.cos(rotationAngle) - y * Math.sin(rotationAngle), x * Math.sin(rotationAngle) + y * Math.cos(rotationAngle));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CadPoint other = (CadPoint) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public boolean isOne() {
        return x == 1 && y == 1;
    }

    public boolean isSquare() {
        return x == y;
    }

}
