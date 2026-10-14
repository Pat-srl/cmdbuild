/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;

public interface CmGeometry {

    GisValueType getType();

    default <T extends CmGeometry> T as(Class<T> c) {
        return (T) this;
    }

    default boolean isPoint() {
        return equal(GisValueType.POINT, getType());
    }
}
