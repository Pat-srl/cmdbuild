/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.model;

import static com.google.common.base.Preconditions.checkArgument;
import static org.cmdbuild.utils.cad.model.CadPoint.point;

public class CadRectangle extends CadPolyline {

    private final double x1, y1, x2, y2;

    public CadRectangle(double x1, double y1, double x2, double y2) {
        super(point(x1, y1), point(x2, y1), point(x2, y2), point(x2, y1));
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        checkArgument(x1 <= x2 && y1 <= y2, "invalid rectangle point order");
    }

    public static CadRectangle rectangle(double x1, double y1, double x2, double y2) {
        return new CadRectangle(x1, y1, x2, y2);
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    @Override
    public CadPoint getCenter() {
        return point((x1 + x2) / 2, (y1 + y2) / 2);
    }

    public double getArea() {
        return (x2 - x1) * (y2 - y1);
    }

    @Override
    public String toString() {
        return "CadRectangle{ (" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ") }";
    }

}
