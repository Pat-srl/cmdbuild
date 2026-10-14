/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import static java.lang.String.format;

public interface Point extends CmGeometry {

    @Override
    default GisValueType getType() {
        return GisValueType.POINT;
    }

    double getX();

    double getY();

    default double getLat() {
        return getY();
    }

    default double getLng() {
        return getX();
    }

    default String serializeLatLng() {
        return format("%s, %s", getLat(), getLng());
    }
}
