/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.gis.GisAttributeConfigImpl;
import org.cmdbuild.gis.GisAttributeImpl;
import org.cmdbuild.gis.GisAttributeType;

import java.util.Map;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtml;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class WsGeoAttributeData {

    private final static String TYPE_GEOMETRY = "geometry";

    private final String name;
    private final Long icon;
    private final String description;
    private final GisAttributeType type;
    private final boolean active;
    private final Integer index, zoomMin, zoomDef, zoomMax;
    private final Map<String, Boolean> visibility;
    private final Map<String, Object> style;
    private final boolean infoWindowEnabled;
    private final String infoWindowContent;
    private final String infoWindowImage;

    public WsGeoAttributeData(
            @JsonProperty("_icon") Long icon,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("active") boolean active,
            @JsonProperty("type") String type,
            @JsonProperty("subtype") String subtype,
            @JsonProperty("index") Integer index,
            @JsonProperty("zoomMin") Integer zoomMin,
            @JsonProperty("zoomDef") Integer zoomDef,
            @JsonProperty("zoomMax") Integer zoomMax,
            @JsonProperty("visibility") Map<String, Boolean> visibility,
            @JsonProperty("style") Map<String, Object> style,
            @JsonProperty("infoWindowEnabled") boolean infoWindowEnabled,
            @JsonProperty("infoWindowContent") String infoWindowContent,
            @JsonProperty("infoWindowImage") String infoWindowImage) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.active = active;
        this.type = parseEnum(equal(type, TYPE_GEOMETRY) ? subtype : type, GisAttributeType.class);
        this.index = index;
        this.zoomMin = zoomMin;
        this.zoomDef = zoomDef;
        this.zoomMax = zoomMax;
        this.visibility = map(visibility).immutable();
        this.style = map(style).immutable();
        this.infoWindowEnabled = infoWindowEnabled;
        this.infoWindowContent = infoWindowContent;
        this.infoWindowImage = infoWindowImage;
    }

    public GisAttributeImpl.GisAttributeImplBuilder toGisAttribute(boolean sanitizeHtml) {
        return GisAttributeImpl.builder()
                .withLayerName(name)
                .withDescription(description)
                .withActive(active)
                .withType(type)
                .withIndex(index)
                .withMinimumZoom(zoomMin)
                .withDefaultZoom(zoomDef)
                .withMaximumZoom(zoomMax)
                .withVisibility(visibility)
                .withConfig(GisAttributeConfigImpl.builder()
                        .withInfoWindowEnabled(infoWindowEnabled)
                        .withInfoWindowContent(sanitizeHtml ? sanitizeHtml(infoWindowContent) : infoWindowContent)
                        .withInfoWindowImage(infoWindowImage)
                        .build());
    }

    public Map<String, Object> getStyle() {
        return this.style;
    }

    public Long getIcon() {
        return this.icon;
    }
}
