/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class GeoserverLayerSerializationHelper {

    public static FluentMap<String, Object> serializeLayer(GeoserverLayer l, GisService gisService, DaoService daoService) {
        GisAttribute a = gisService.getGisAttributeIncludeInherited(l.getOwnerClass(), l.getAttributeName());
        return map(
                "_id", l.getId(),
                "name", a.getLayerName(),
                "attribute_id", a.getId(),
                "active", a.isActive() && l.isActive(),
                "attribute_active", a.isActive(),
                "layer_active", l.isActive(),
                "_type", serializeEnum(a.getType()),
                "description", a.getDescription(),
                "index", a.getIndex(),
                "geoserver_name", l.getGeoserverLayer(),//TODO rename this, then remove
                "geoserver_store", l.getGeoserverStore(),
                "geoserver_layer", l.getGeoserverLayer(),
                "description", a.getDescription(),
                "x", l.getCenter().isZero() ? null : l.getCenter().getX(),
                "y", l.getCenter().isZero() ? null : l.getCenter().getY(),
                "_owner_description", daoService.getInfo(daoService.getClasse(l.getOwnerClass()), l.getOwnerCard()).getDescription(),
                ZOOM_MIN, a.getMinimumZoom(),
                ZOOM_DEF, a.getDefaultZoom(),
                ZOOM_MAX, a.getMaximumZoom(),
                "visibility", a.getVisibility(),
                "_owner_type", l.getOwnerClass(),
                "_owner_id", l.getOwnerCard(),
                "_beginDate", toIsoDateTime(l.getBeginDate()));
    }
}
