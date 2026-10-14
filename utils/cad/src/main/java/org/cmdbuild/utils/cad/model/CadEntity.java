/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.model;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;

public interface CadEntity {

    String getLayer();

    Map<String, String> getMetadata();

    CadPoint getPosition();

    CadPolyline getPerimeter();

    CadRectangle getBoundingBox();

    CadPolyline getPolyline();

    double getSurface();

    boolean isClosed();

    default void checkIsClosed() {
        checkArgument(isClosed(), "expected closed cad entity, but this is not closed");
    }

    default boolean contains(CadEntity other) {
        checkIsClosed();
        return getPerimeter().contains(other.getPerimeter());
    }

    default boolean intersect(CadEntity other) {
        return getPerimeter().intersect(other.getPerimeter());
    }

}
