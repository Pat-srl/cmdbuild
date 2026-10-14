/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import org.cmdbuild.utils.cad.model.CadPoint;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GeoserverLayerInfoImpl implements GeoserverLayerInfo {

    private final String layerName, storeName;
    private final CadPoint center;

    public GeoserverLayerInfoImpl(String storeName, String layerName, CadPoint center) {
        this.layerName = checkNotBlank(layerName);
        this.storeName = checkNotBlank(storeName);
        this.center = center;
    }

    @Override
    public String getStoreName() {
        return storeName;
    }

    @Override
    public String getLayerName() {
        return layerName;
    }

    @Override
    public CadPoint getCenter() {
        return center;
    }
}
