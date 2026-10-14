/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import java.util.List;

public interface Polygon extends CmGeometry {

	@Override
	default GisValueType getType() {
		return GisValueType.POLYGON;
	}

	List<Point> getPoints();
}
