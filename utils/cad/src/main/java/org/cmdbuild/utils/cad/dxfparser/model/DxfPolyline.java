/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import java.util.List;
import org.cmdbuild.utils.cad.model.CadPolyline;

public interface DxfPolyline extends DxfEntity {

    List<DxfVertex> getVertexes();

    CadPolyline getPolyline();

    default boolean isPoint() {
        return getPolyline().isPoint();
    }
}
