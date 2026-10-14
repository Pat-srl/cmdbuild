/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;


import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_GEOMETRY;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
public class GisAttributeSerializationHelper {

    public static FluentMap<String, Object> serializeGisAttribute(GisAttribute layer, TranslationService translationService, EasyuploadService easyuploadService, UserClassService userClassService, GisService gisService) {
        return mapOf(String.class, Object.class).with(
                "_id", layer.getId(),
                "name", layer.getLayerName(),
                "owner_type", layer.getOwnerClassName(),
                "active", layer.isActive(),
                "type", layer.isPostgis() ? TYPE_GEOMETRY : serializeEnum(layer.getType()),
                "subtype", serializeEnum(layer.getType()),
                "description", layer.getDescription(),
                "_description_translation", translationService.translateGisAttributeDescription(layer),
                "index", layer.getIndex(),
                "visibility", layer.getVisibilityMap(),
                "zoomMin", layer.getMinimumZoom(),
                "zoomMax", layer.getMaximumZoom(),
                "zoomDef", layer.getDefaultZoom(),
                "_beginDate", CmDateUtils.toIsoDateTime(layer.getBeginDate()),
                "_is_geometry", layer.isPostgis(),
                "_is_coverage", layer.isGeoserver(),
                "style", map(layer.getMapStyleMap()).withoutKeys("externalGraphic")).accept((m) -> {
            String icon = toStringOrNull(layer.getMapStyleMap().get("externalGraphic"));
            if (isNotBlank(icon)) {
                m.put("_icon", Optional.ofNullable(easyuploadService.getByPathOrNull(icon)).map(EasyuploadItem::getId).orElse(null));
            }
            if (layer.isPostgis()) {
                m.put("writable", userClassService.getUserClass(layer.getOwnerClassName()).hasGisAttributeWritePermission(layer.getLayerName()));
            } else {
                m.put("writable", gisService.isGeoserverEnabled() ? userClassService.getUserClass(layer.getOwnerClassName()).hasGisAttributeWritePermission(layer.getLayerName()) : false);
            }
        }).with("infoWindowEnabled", layer.getConfig().getInfoWindowEnabled(),
                "infoWindowContent", layer.getConfig().getInfoWindowContent(),
                "infoWindowImage", layer.getConfig().getInfoWindowImage());
    }
}
