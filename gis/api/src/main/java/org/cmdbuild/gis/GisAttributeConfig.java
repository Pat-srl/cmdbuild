package org.cmdbuild.gis;

import org.cmdbuild.utils.json.JsonBean;

@JsonBean(GisAttributeConfigImpl.class)
public interface GisAttributeConfig {

    boolean getInfoWindowEnabled();

    String getInfoWindowContent();

    String getInfoWindowImage();

}
