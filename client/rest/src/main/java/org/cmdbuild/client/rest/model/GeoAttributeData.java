package org.cmdbuild.client.rest.model;

import java.util.Map;
import org.cmdbuild.gis.GisAttributeType;

public interface GeoAttributeData {

    long getId();

    String getName();

    Long getIcon();

    String getDescription();

    String getType();

    GisAttributeType getSubType();

    boolean isActive();

    Integer getIndex();

    Integer getZoomMin();

    Integer getZoomDef();

    Integer getZoomMax();

    Map<String, Boolean> getVisibility();

    Map<String, Object> getStyle();

    boolean isInfoWindowEnabled();

    String getInfoWindowContent();

    String getInfoWindowImage();

}
