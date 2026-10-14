package org.cmdbuild.config;

import org.cmdbuild.utils.http.ExtServiceConfiguration;

public interface GisConfiguration extends ExtServiceConfiguration {

    boolean isEnabled();

    boolean isGeoServerEnabled();

    boolean isNavigationEnabled();

    boolean enableAngleDisplacementProcessing();

    String getGeoServerUrl();

    String getGeoServerWorkspace();

    String getGeoServerAdminUser();

    String getGeoServerAdminPassword();

    double getCenterLat();

    double getCenterLon();

    double getInitialZoomLevel();

    boolean isKeepZoomAndPositionEnabled();

    double minZoomLevel();

    double maxZoomLevel();
}
