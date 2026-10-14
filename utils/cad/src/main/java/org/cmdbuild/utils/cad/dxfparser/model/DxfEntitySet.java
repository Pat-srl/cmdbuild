/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import java.util.List;

public interface DxfEntitySet extends DxfEntity {

    List<DxfEntity> getDxfEntities();

}
