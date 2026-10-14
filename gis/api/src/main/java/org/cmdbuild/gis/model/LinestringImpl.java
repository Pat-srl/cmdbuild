/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.model;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.cmdbuild.gis.Linestring;
import org.cmdbuild.gis.Point;

public class LinestringImpl implements Linestring {

	private final List<Point> points;

	public LinestringImpl(Iterable<Point> points) {
		this.points = ImmutableList.copyOf(points);
		checkArgument(this.points.size() >= 2);
	}

	@Override
	public List<Point> getPoints() {
		return points;
	}

}
