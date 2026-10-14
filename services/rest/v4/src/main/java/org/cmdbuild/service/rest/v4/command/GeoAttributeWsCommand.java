/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisAttributeImpl;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.service.rest.v4.model.WsGeoAttributeData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

/**
 * @author ldare
 */
@Component
public class GeoAttributeWsCommand {

    private final GisService gisService;
    private final UserClassService userClassService;
    private final CoreConfiguration coreConfiguration;
    private final EasyuploadService easyuploadService;

    public GeoAttributeWsCommand(GisService gisService, CoreConfiguration coreConfiguration, EasyuploadService easyuploadService, UserClassService userClassService) {
        this.gisService = checkNotNull(gisService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.userClassService = checkNotNull(userClassService);
    }

    public PagedElements<GisAttribute> doReadAllAttributes(String classId, Boolean visible, Integer limit, Integer offset) {
        List<GisAttribute> elements;
        if (equal(classId, "_ANY")) {
            elements = gisService.getGisAttributes();
        } else if (visible) {
            elements = gisService.getGisAttributesVisibleFromClass(classId);
        } else {
            elements = gisService.getGisAttributesByOwnerClassIncludeInherited(classId);
        }
        return paged(elements, offset, limit);
    }

    public GisAttribute doReadAttribute(String classId, String attributeId) {
        GisAttribute layer = gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId);
        checkArgument(userClassService.getUserClass(layer.getOwnerClassName()).hasGisAttributeReadPermission(layer.getLayerName()), format("User not allowed to access the specified GeoAttribute < %s >", layer.getLayerName()));

        return layer;
    }

    public List<GisAttribute> doReorder(String classId, List<Long> attrOrder) {
        checkArgument(equal(classId, "_ANY"), "service available only for _ANY classes");
        return gisService.updateGisAttributesOrder(attrOrder);
    }

    public GisAttribute doCreate(String classId, WsGeoAttributeData data) {
        GisAttribute layer = toGisAttribute(data, classId).build();
        return gisService.createGisAttribute(layer);
    }

    public List<GisAttribute> doUpdateVisibility(String classId, Map<Long, Boolean> geoAttributes) {
        gisService.updateGeoAttributesVisibilityForClass(classId, geoAttributes);
        return gisService.getGisAttributesVisibleFromClass(classId);
    }

    public GisAttribute doUpdate(String classId, String attributeId, WsGeoAttributeData data) {
        GisAttribute gisAttribute = toGisAttribute(data, classId).withId(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId).getId()).build();
        return gisService.updateGisAttribute(gisAttribute);
    }

    public void doDelete(String classId, String attributeId) {
        gisService.deleteGisAttribute(classId, gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(classId, attributeId).getLayerName());
    }

    private GisAttributeImpl.GisAttributeImplBuilder toGisAttribute(WsGeoAttributeData data, String classId) {
        return data.toGisAttribute(coreConfiguration.hasDefaultTextContentSecurity(TCS_HTML_SAFE)).withOwnerClassName(classId).accept((b) -> {
            Map<String, Object> styleMap = map(data.getStyle());
            if (isNotNullAndGtZero(data.getIcon())) {
                styleMap.put("externalGraphic", easyuploadService.getById(data.getIcon()).getPath());
            }
            b.withMapStyle(styleMap);
        });
    }
}
