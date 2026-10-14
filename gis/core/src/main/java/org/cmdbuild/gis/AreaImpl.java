/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import org.cmdbuild.gis.model.PointImpl;

public class AreaImpl implements Area {

    private final double x1, y1, x2, y2;

    public AreaImpl(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public double getX1() {
        return x1;
    }

    @Override
    public double getY1() {
        return y1;
    }

    @Override
    public double getX2() {
        return x2;
    }

    @Override
    public double getY2() {
        return y2;
    }

    @Override
    public Point getCenter() {
        return new PointImpl((getX1() + getX2()) / 2, (getY1() + getY2()) / 2);
    }

    @Override
    public String toString() {
        return "AreaImpl{" + "x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + '}';
    }

}
