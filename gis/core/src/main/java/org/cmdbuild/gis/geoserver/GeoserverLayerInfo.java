/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import org.cmdbuild.utils.cad.model.CadPoint;

public interface GeoserverLayerInfo {

    String getStoreName();

    String getLayerName();

    CadPoint getCenter();

}
