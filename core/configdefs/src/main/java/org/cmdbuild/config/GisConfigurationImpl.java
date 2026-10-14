package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.gis")
public final class GisConfigurationImpl implements GisConfiguration {

    @ConfigValue(key = "center.lon", defaultValue = "0")
    private double centerLon;

    @ConfigValue(key = "center.lat", defaultValue = "0")
    private double centerLat;

    @ConfigValue(key = "initialZoomLevel", defaultValue = "3")
    private double initialZoomLevel;

    @ConfigValue(key = "keepZoomAndPosition.enabled", defaultValue = FALSE)
    private boolean isKeepZoomAndPositionEnabled;

    @ConfigValue(key = "minZoomLevel", defaultValue = "0")
    private double minZoomLevel;

    @ConfigValue(key = "maxZoomLevel", defaultValue = "24")
    private double maxZoomLevel;

    @ConfigValue(key = "enabled", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = "geoserver.enabled", defaultValue = FALSE)
    private boolean isGeoserverEnabled;

    @ConfigValue(key = "navigation.enabled", defaultValue = FALSE)
    private boolean isNavigationEnabled;

    @ConfigValue(key = "enableAngleDisplacementProcessing", defaultValue = TRUE)
    private boolean enableAngleDisplacementProcessing;

    @ConfigValue(key = "geoserver.url", defaultValue = "http://localhost:12080/geoserver", category = CC_ENV)
    private String geoserverUrl;

    @ConfigValue(key = "geoserver.workspace", defaultValue = "cmdbuild", category = CC_ENV)
    private String geoserverWorkspace;

    @ConfigValue(key = "geoserver.admin.user", defaultValue = "admin", category = CC_ENV)
    private String geoserverAdminUser;

    @ConfigValue(key = "geoserver.admin.password", defaultValue = "geoserver", category = CC_ENV)
    private String geoserverAdminPassword;

    @ConfigValue(key = "dwg2dxf.url", defaultValue = "http://localhost:8080/dwg2dxf/api/v1", category = CC_ENV)
    private String dwg2dxfUrl;

    @ConfigValue(key = "dwg2dxf.username", defaultValue = "admin", category = CC_ENV)
    private String dwg2dxfUsername;

    @ConfigValue(key = "dwg2dxf.password", defaultValue = "password", category = CC_ENV)
    private String dwg2dxfPassword;

    @Override
    public boolean isNavigationEnabled() {
        return isNavigationEnabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public double getCenterLon() {
        return centerLon;
    }

    @Override
    public double getCenterLat() {
        return centerLat;
    }

    @Override
    public double getInitialZoomLevel() {
        return initialZoomLevel;
    }

    @Override
    public boolean isKeepZoomAndPositionEnabled() {
        return isKeepZoomAndPositionEnabled;
    }

    @Override
    public double minZoomLevel() {
        return minZoomLevel;
    }

    @Override
    public double maxZoomLevel() {
        return maxZoomLevel;
    }

    @Override
    public boolean isGeoServerEnabled() {
        return isGeoserverEnabled;
    }

    @Override
    public boolean enableAngleDisplacementProcessing() {
        return enableAngleDisplacementProcessing;
    }

    @Override
    public String getGeoServerUrl() {
        return geoserverUrl.replaceFirst("[/]$", "");
    }

    @Override
    public String getGeoServerWorkspace() {
        return geoserverWorkspace;
    }

    @Override
    public String getGeoServerAdminUser() {
        return geoserverAdminUser;
    }

    @Override
    public String getGeoServerAdminPassword() {
        return geoserverAdminPassword;
    }

    @Override
    public String getUrl() {
        return dwg2dxfUrl;
    }

    @Override
    public String getUsername() {
        return dwg2dxfUsername;
    }

    @Override
    public String getPassword() {
        return dwg2dxfPassword;
    }

    @Override
    public Long getTimeout() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
