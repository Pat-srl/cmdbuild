/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import jakarta.annotation.Nullable;
import org.cmdbuild.utils.cad.model.CadPolyline;

public interface DxfEntity {

    @Nullable
    String getLayer();

    String getType();

    DxfExtendedData getXdata();

    CadPolyline getPerimeter();

    boolean isClosedPerimeter();

    default boolean hasXdata() {
        return getXdata().isNotEmpty();
    }
}
