package org.cmdbuild.gis.geoserver;

import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.utils.cad.model.CadPoint;
import org.cmdbuild.utils.io.BigByteArray;

public interface GeoserverService {

    boolean isEnabled();

    CadPoint getGeoserverLayerCenter(String layerName);

    GeoserverLayer set(GeoserverLayer layerMetadata, BigByteArray data);

    void delete(GeoserverLayer layer);
}
