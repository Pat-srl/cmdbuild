/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.model;

import org.cmdbuild.gis.Point;

public class PointImpl implements Point {

	private final double x, y;

	public PointImpl(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "PointImpl{" + "x=" + x + ", y=" + y + '}';
	}

}
